package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.dto.BoletaResponseDto;
import cl.duoc.fortepiano.dto.CheckoutRequest;
import cl.duoc.fortepiano.dto.DatosEnvioDto;
import cl.duoc.fortepiano.dto.ItemCheckoutDto;
import cl.duoc.fortepiano.dto.ReintentoPagoRequest;
import cl.duoc.fortepiano.entity.Boleta;
import cl.duoc.fortepiano.entity.DetalleBoleta;
import cl.duoc.fortepiano.entity.Producto;
import cl.duoc.fortepiano.repository.BoletaRepository;
import cl.duoc.fortepiano.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BoletaService {

    private static final String EXITOSO = "exitoso";
    private static final String RECHAZADO = "rechazado";
    private static final String INTENTAR_MAS_TARDE = "intentar_mas_tarde";

    private static final String EMITIDA = "Emitida";
    private static final String PENDIENTE = "Pendiente";
    private static final String RECHAZADA = "Rechazada";
    private static final String ANULADA = "Anulada";

    private static final float IVA_FACTOR = 1.19f;
    private static final String METODO_PAGO_POR_DEFECTO = "Webpay Plus / Débito";
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss", Locale.forLanguageTag("es-CL"));

    private final BoletaRepository boletaRepository;
    private final ProductoRepository productoRepository;

    public BoletaService(BoletaRepository boletaRepository, ProductoRepository productoRepository) {
        this.boletaRepository = boletaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public BoletaResponseDto checkout(CheckoutRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El checkout debe incluir al menos un item");
        }

        String resultado = normalizarResultado(request.getResultadoTransaccion());
        Map<String, Integer> cantidades = agruparCantidades(request.getItems());
        Map<String, Producto> productos = cargarProductos(cantidades.keySet());

        List<DetalleBoleta> detalles = construirDetalles(cantidades, productos);
        int total = detalles.stream().mapToInt(DetalleBoleta::getSubtotal).sum();
        int subtotalNeto = Math.round(total / IVA_FACTOR);
        int iva = total - subtotalNeto;

        if (EXITOSO.equals(resultado)) {
            validarYDescontarStock(cantidades, productos);
        }

        Boleta boleta = resolverBoleta(request.getBoletaExistenteId());
        boleta.setClienteRun(recortar(request.getClienteRun()));
        boleta.setClienteNombre(nombreCompleto(request));
        boleta.setClienteCorreo(request.getClienteCorreo().trim());
        boleta.setSubtotalNeto(subtotalNeto);
        boleta.setIva(iva);
        boleta.setTotal(total);
        boleta.setMetodoPago(metodoPago(request.getMetodoPago()));
        boleta.setEstado(estadoSegunResultado(resultado));
        boleta.setMensajeTransaccion(mensajeSegunResultado(resultado));
        aplicarDatosEnvio(boleta, request.getDatosEnvio());

        boleta.getItems().clear();
        for (DetalleBoleta detalle : detalles) {
            detalle.setBoleta(boleta);
            boleta.getItems().add(detalle);
        }

        return BoletaResponseDto.from(boletaRepository.save(boleta));
    }

    @Transactional
    public BoletaResponseDto reintentarPago(String id, ReintentoPagoRequest request) {
        Boleta boleta = obtenerEntidad(id);

        if (!PENDIENTE.equals(boleta.getEstado()) && !RECHAZADA.equals(boleta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se puede reintentar el pago de boletas Pendientes o Rechazadas");
        }

        String resultado = normalizarResultado(request.getResultadoTransaccion());

        if (EXITOSO.equals(resultado)) {
            Map<String, Integer> cantidades = new LinkedHashMap<>();
            for (DetalleBoleta detalle : boleta.getItems()) {
                cantidades.merge(detalle.getCodigo(), detalle.getCantidad(), Integer::sum);
            }
            Map<String, Producto> productos = cargarProductos(cantidades.keySet());
            validarYDescontarStock(cantidades, productos);
        }

        boleta.setEstado(estadoSegunResultado(resultado));
        boleta.setMensajeTransaccion(mensajeSegunResultado(resultado));
        if (request.getMetodoPago() != null && !request.getMetodoPago().isBlank()) {
            boleta.setMetodoPago(request.getMetodoPago().trim());
        }

        return BoletaResponseDto.from(boletaRepository.save(boleta));
    }

    @Transactional
    public BoletaResponseDto anular(String id) {
        Boleta boleta = obtenerEntidad(id);

        if (!EMITIDA.equals(boleta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se pueden anular boletas en estado Emitida");
        }

        for (DetalleBoleta detalle : boleta.getItems()) {
            productoRepository.findById(detalle.getCodigo()).ifPresent(producto -> {
                int stockActual = producto.getStock() == null ? 0 : producto.getStock();
                producto.setStock(stockActual + detalle.getCantidad());
                productoRepository.save(producto);
            });
        }

        boleta.setEstado(ANULADA);
        boleta.setMensajeTransaccion("Boleta anulada. Stock repuesto correctamente.");
        return BoletaResponseDto.from(boletaRepository.save(boleta));
    }

    public List<BoletaResponseDto> listar(String clienteRun) {
        List<Boleta> boletas = (clienteRun == null || clienteRun.isBlank())
                ? boletaRepository.findAll()
                : boletaRepository.findByClienteRunIgnoreCase(clienteRun.trim());

        return boletas.stream()
                .sorted(Comparator.comparing(Boleta::getFolio).reversed())
                .map(BoletaResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<BoletaResponseDto> misBoletas(String clienteRun, String correo) {
        if ((clienteRun == null || clienteRun.isBlank()) && (correo == null || correo.isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe indicar clienteRun o correo para consultar las boletas");
        }

        List<Boleta> boletas = (clienteRun != null && !clienteRun.isBlank())
                ? boletaRepository.findByClienteRunIgnoreCase(clienteRun.trim())
                : boletaRepository.findByClienteCorreoIgnoreCase(correo.trim());

        return boletas.stream()
                .sorted(Comparator.comparing(Boleta::getFolio).reversed())
                .map(BoletaResponseDto::from)
                .collect(Collectors.toList());
    }

    public BoletaResponseDto obtener(String id) {
        return BoletaResponseDto.from(obtenerEntidad(id));
    }

    private Boleta obtenerEntidad(String id) {
        return boletaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boleta no encontrada"));
    }

    private Boleta resolverBoleta(String boletaExistenteId) {
        if (boletaExistenteId == null || boletaExistenteId.isBlank()) {
            Boleta nueva = new Boleta();
            int folio = siguienteFolio();
            nueva.setId("BOL-" + System.currentTimeMillis() + "-" + folio);
            nueva.setFolio(folio);
            nueva.setFecha(LocalDateTime.now().format(FORMATO_FECHA));
            return nueva;
        }

        Boleta existente = obtenerEntidad(boletaExistenteId.trim());
        if (EMITIDA.equals(existente.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La boleta ya fue emitida");
        }
        if (ANULADA.equals(existente.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La boleta esta anulada");
        }
        existente.setFecha(LocalDateTime.now().format(FORMATO_FECHA));
        return existente;
    }

    private int siguienteFolio() {
        return boletaRepository.findTopByOrderByFolioDesc()
                .map(boleta -> boleta.getFolio() + 1)
                .orElse(1001);
    }

    private Map<String, Integer> agruparCantidades(List<ItemCheckoutDto> items) {
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (ItemCheckoutDto item : items) {
            cantidades.merge(item.getCodigo().trim(), item.getCantidad(), Integer::sum);
        }
        return cantidades;
    }

    private Map<String, Producto> cargarProductos(Iterable<String> codigos) {
        Map<String, Producto> productos = new LinkedHashMap<>();
        for (String codigo : codigos) {
            Producto producto = productoRepository.findById(codigo)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Producto no encontrado: " + codigo));
            productos.put(codigo, producto);
        }
        return productos;
    }

    private List<DetalleBoleta> construirDetalles(Map<String, Integer> cantidades, Map<String, Producto> productos) {
        List<DetalleBoleta> detalles = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : cantidades.entrySet()) {
            Producto producto = productos.get(entrada.getKey());
            int cantidad = entrada.getValue();
            int precioUnitario = precioEfectivo(producto);

            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setCodigo(producto.getCodigo());
            detalle.setNombre(producto.getNombre());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setCantidad(cantidad);
            detalle.setSubtotal(precioUnitario * cantidad);
            detalles.add(detalle);
        }
        return detalles;
    }

    private int precioEfectivo(Producto producto) {
        if (Boolean.TRUE.equals(producto.getEnOferta())
                && producto.getPrecioOferta() != null
                && producto.getPrecioOferta() > 0) {
            return producto.getPrecioOferta();
        }
        return producto.getPrecio();
    }

    private void validarYDescontarStock(Map<String, Integer> cantidades, Map<String, Producto> productos) {
        // Primero validamos todo, para no descontar stock parcialmente.
        for (Map.Entry<String, Integer> entrada : cantidades.entrySet()) {
            Producto producto = productos.get(entrada.getKey());
            int stock = producto.getStock() == null ? 0 : producto.getStock();
            if (stock < entrada.getValue()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente para el producto " + producto.getNombre());
            }
        }

        for (Map.Entry<String, Integer> entrada : cantidades.entrySet()) {
            Producto producto = productos.get(entrada.getKey());
            producto.setStock(producto.getStock() - entrada.getValue());
            productoRepository.save(producto);
        }
    }

    private void aplicarDatosEnvio(Boleta boleta, DatosEnvioDto datosEnvio) {
        if (datosEnvio == null) {
            return;
        }
        boleta.setModalidad(datosEnvio.getModalidad());
        boleta.setDestinatario(datosEnvio.getDestinatario());
        boleta.setTelefono(datosEnvio.getTelefono());
        boleta.setRegionId(datosEnvio.getRegionId());
        boleta.setRegionNombre(datosEnvio.getRegionNombre());
        boleta.setComunaId(datosEnvio.getComunaId());
        boleta.setComunaNombre(datosEnvio.getComunaNombre());
        boleta.setDireccion(datosEnvio.getDireccion());
        boleta.setReferencia(datosEnvio.getReferencia());
    }

    private String normalizarResultado(String resultado) {
        if (resultado == null || resultado.isBlank()) {
            return EXITOSO;
        }
        String normalizado = resultado.trim().toLowerCase(Locale.ROOT);
        if (!EXITOSO.equals(normalizado)
                && !RECHAZADO.equals(normalizado)
                && !INTENTAR_MAS_TARDE.equals(normalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "resultadoTransaccion debe ser exitoso, rechazado o intentar_mas_tarde");
        }
        return normalizado;
    }

    private String estadoSegunResultado(String resultado) {
        return switch (resultado) {
            case EXITOSO -> EMITIDA;
            case RECHAZADO -> RECHAZADA;
            default -> PENDIENTE;
        };
    }

    private String mensajeSegunResultado(String resultado) {
        return switch (resultado) {
            case EXITOSO -> "Pago aprobado. Boleta emitida correctamente.";
            case RECHAZADO -> "La transacción fue rechazada por la entidad bancaria.";
            default -> "Problema temporal de conexión bancaria. Pendiente de reintento.";
        };
    }

    private String nombreCompleto(CheckoutRequest request) {
        String apellidos = request.getClienteApellidos() == null ? "" : request.getClienteApellidos();
        String nombre = (request.getClienteNombre() + " " + apellidos).trim();
        return nombre.isEmpty() ? "Cliente General" : nombre;
    }

    private String metodoPago(String metodoPago) {
        return (metodoPago == null || metodoPago.isBlank())
                ? METODO_PAGO_POR_DEFECTO
                : metodoPago.trim();
    }

    private String recortar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
