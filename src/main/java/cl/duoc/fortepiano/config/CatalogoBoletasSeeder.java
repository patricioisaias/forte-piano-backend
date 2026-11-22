package cl.duoc.fortepiano.config;

import cl.duoc.fortepiano.entity.Boleta;
import cl.duoc.fortepiano.entity.Categoria;
import cl.duoc.fortepiano.entity.DetalleBoleta;
import cl.duoc.fortepiano.entity.Producto;
import cl.duoc.fortepiano.repository.BoletaRepository;
import cl.duoc.fortepiano.repository.CategoriaRepository;
import cl.duoc.fortepiano.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Datos iniciales del Modulo B (catalogo, ofertas y boletas).
 * Se ejecuta despues del seeder de usuarios (@Order(1)) y antes del de contenido (@Order(3)).
 */
@Component
@Order(2)
public class CatalogoBoletasSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CatalogoBoletasSeeder.class);

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final BoletaRepository boletaRepository;

    public CatalogoBoletasSeeder(CategoriaRepository categoriaRepository,
                                 ProductoRepository productoRepository,
                                 BoletaRepository boletaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.boletaRepository = boletaRepository;
    }

    @Override
    public void run(String... args) {
        sembrarCategorias();
        sembrarProductos();
        sembrarBoletas();
    }

    private void sembrarCategorias() {
        if (categoriaRepository.count() > 0) {
            log.info("CatalogoBoletasSeeder: las categorias ya existen, no se insertan nuevamente");
            return;
        }

        List<Categoria> categorias = List.of(
                new Categoria(null, "Pianos Acusticos"),
                new Categoria(null, "Pianos Digitales"),
                new Categoria(null, "Sintetizadores"),
                new Categoria(null, "Accesorios"),
                new Categoria(null, "Amplificadores"),
                new Categoria(null, "Partituras"));

        categoriaRepository.saveAll(categorias);
        log.info("CatalogoBoletasSeeder: {} categorias insertadas", categorias.size());
    }

    private void sembrarProductos() {
        if (productoRepository.count() > 0) {
            log.info("CatalogoBoletasSeeder: los productos ya existen, no se insertan nuevamente");
            return;
        }

        List<Producto> productos = List.of(
                producto("AC-005", "Soporte de Teclado Doble",
                        "Soporte de teclado tipo tijera de metal resistente con altura ajustable, compatible con la mayoria de pianos digitales y teclados.",
                        17990, 10, 3, 4, "productos/prod-soporte-teclado-doble.webp", false, 0, null),
                producto("PD-006", "Yamaha Arius YDP-146 Rosewood",
                        "Piano digital de mueble con teclas contrapesadas GH3, sonido Pure CF Sampling y acabado elegante en palisandro.",
                        699990, 25, 5, 2, "productos/prod-piano-yamaha-arius-ydp-146-rosewood.webp", false, 0, null),
                producto("AD-001", "Funda GTSA Key 61 con Ruedas",
                        "Funda acolchada con ruedas para teclados de 61 teclas, protege tu instrumento del polvo y la humedad durante el transporte.",
                        25000, 15, 4, 4, "productos/prod-funda-gtsa-key-61-ruedas.webp", true, 20, 20000),
                producto("PA-001", "Piano Yamaha B1",
                        "Piano acustico vertical de la serie B, ideal para estudiantes y espacios reducidos. Sonido brillante y mecanismo fiable.",
                        3299000, 5, 2, 1, "productos/prod-piano-yamaha-b10.webp", false, 0, null),
                producto("PD-001", "Yamaha P-45 Negro",
                        "Piano digital de entrada con 88 teclas contrapesadas GHS y tecnologia Pure CF Sampling. Compacto y portatil.",
                        459990, 30, 5, 2, "productos/prod-piano-yamaha-p45-negro.webp", true, 15, 390990),
                producto("SY-001", "Nord Electro 6D 61",
                        "Teclado de escenario premium con 61 teclas, motores de piano, organo y sintetizador. Sonido profesional de referencia.",
                        1899990, 12, 3, 3, "productos/prod-teclado-nord-electro-6d-61.jpg", true, 10, 1709990),
                producto("AD-003", "Pedal de Sustain SP-34",
                        "Pedal de sustain estilo piano con accion progresiva, compatible con la mayoria de pianos y teclados digitales.",
                        15000, 25, 5, 4, "productos/prod-pedal-sp-34.webp", false, 0, null),
                producto("PD-002", "Donner DDP-80 Digital Piano",
                        "Piano digital compacto de 88 teclas contrapesadas con 128 voces de polifonia y diseno moderno en madera.",
                        659990, 18, 4, 2, "productos/prod-piano-donner-ddp-80.webp", true, 20, 527990));

        productoRepository.saveAll(productos);
        log.info("CatalogoBoletasSeeder: {} productos insertados", productos.size());
    }

    private void sembrarBoletas() {
        if (boletaRepository.count() > 0) {
            log.info("CatalogoBoletasSeeder: las boletas ya existen, no se insertan nuevamente");
            return;
        }

        List<Boleta> boletas = List.of(
                boleta("BOL-SEED-1001", 1001, "28-10-2026, 11:15:00",
                        "112223339", "Camila Soto Perez", "camila@gmail.com",
                        682336, 129644, 811980, "Webpay Plus / Tarjeta de Débito", "Emitida", null,
                        "perfil", "Camila Soto Perez", null,
                        2, "Valparaiso", 4, "Valparaiso", "Los Aromos 789", null,
                        item("PD-001", "Yamaha P-45 Negro", 390990, 2),
                        item("AD-003", "Pedal de Sustain SP-34", 15000, 2)),

                boleta("BOL-SEED-1002", 1002, "29-10-2026, 15:40:00",
                        "201113334", "Diego Fernandez Rojas", "diego@duoc.cl",
                        633580, 120380, 753960, "Webpay Plus / Tarjeta de Crédito", "Emitida", null,
                        "otra_direccion", "Diego Fernandez Rojas", null,
                        3, "Biobio", 6, "Concepcion", "Av. Chacabuco 940, Depto 402", null,
                        item("PD-006", "Yamaha Arius YDP-146 Rosewood", 699990, 1),
                        item("AC-005", "Soporte de Teclado Doble", 17990, 3)),

                boleta("BOL-SEED-1003", 1003, "30-10-2026, 18:05:00",
                        "112223339", "Camila Soto Perez", "camila@gmail.com",
                        1436966, 273024, 1709990, "Webpay Plus / Tarjeta de Crédito", "Pendiente",
                        "Problema temporal de conexión bancaria. Pendiente de reintento.",
                        "retiro_tienda", "Camila Soto Perez", null,
                        null, "Región Metropolitana", null, "Providencia",
                        "Casa Matriz Forte & Piano - Av. Providencia 1234", null,
                        item("SY-001", "Nord Electro 6D 61", 1709990, 1)),

                boleta("BOL-SEED-1004", 1004, "31-10-2026, 10:20:00",
                        "201113334", "Diego Fernandez Rojas", "diego@duoc.cl",
                        443689, 84301, 527990, "Webpay Plus / Tarjeta de Débito", "Rechazada",
                        "La transacción fue rechazada por la entidad bancaria.",
                        null, null, null, null, null, null, null, null, null,
                        item("PD-002", "Donner DDP-80 Digital Piano", 527990, 1)),

                boleta("BOL-SEED-1005", 1005, "01-11-2026, 14:12:00",
                        "112223339", "Camila Soto Perez", "camila@gmail.com",
                        2772269, 526731, 3299000, "Transferencia Bancaria Electrónica", "Emitida", null,
                        "perfil", "Camila Soto Perez", null,
                        2, "Valparaiso", 4, "Valparaiso", "Los Aromos 789", null,
                        item("PA-001", "Piano Yamaha B1", 3299000, 1)));

        boletaRepository.saveAll(boletas);
        log.info("CatalogoBoletasSeeder: {} boletas insertadas", boletas.size());
    }

    private Producto producto(String codigo, String nombre, String descripcion, int precio, int stock,
                              int stockCritico, int categoriaId, String imagen, boolean enOferta,
                              int porcentajeDescuento, Integer precioOferta) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setStockCritico(stockCritico);
        producto.setCategoriaId(categoriaId);
        producto.setImagen(imagen);
        producto.setEstado("Activo");
        producto.setEnOferta(enOferta);
        producto.setPorcentajeDescuento(porcentajeDescuento);
        producto.setPrecioOferta(precioOferta);
        return producto;
    }

    private DetalleBoleta item(String codigo, String nombre, int precioUnitario, int cantidad) {
        DetalleBoleta detalle = new DetalleBoleta();
        detalle.setCodigo(codigo);
        detalle.setNombre(nombre);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setCantidad(cantidad);
        detalle.setSubtotal(precioUnitario * cantidad);
        return detalle;
    }

    private Boleta boleta(String id, int folio, String fecha, String clienteRun, String clienteNombre,
                          String clienteCorreo, int subtotalNeto, int iva, int total, String metodoPago,
                          String estado, String mensajeTransaccion, String modalidad, String destinatario,
                          String telefono, Integer regionId, String regionNombre, Integer comunaId,
                          String comunaNombre, String direccion, String referencia, DetalleBoleta... items) {
        Boleta boleta = new Boleta();
        boleta.setId(id);
        boleta.setFolio(folio);
        boleta.setFecha(fecha);
        boleta.setClienteRun(clienteRun);
        boleta.setClienteNombre(clienteNombre);
        boleta.setClienteCorreo(clienteCorreo);
        boleta.setSubtotalNeto(subtotalNeto);
        boleta.setIva(iva);
        boleta.setTotal(total);
        boleta.setMetodoPago(metodoPago);
        boleta.setEstado(estado);
        boleta.setMensajeTransaccion(mensajeTransaccion);
        boleta.setModalidad(modalidad);
        boleta.setDestinatario(destinatario);
        boleta.setTelefono(telefono);
        boleta.setRegionId(regionId);
        boleta.setRegionNombre(regionNombre);
        boleta.setComunaId(comunaId);
        boleta.setComunaNombre(comunaNombre);
        boleta.setDireccion(direccion);
        boleta.setReferencia(referencia);

        for (DetalleBoleta detalle : items) {
            detalle.setBoleta(boleta);
            boleta.getItems().add(detalle);
        }
        return boleta;
    }
}
