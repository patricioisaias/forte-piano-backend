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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private BoletaService boletaService;

    @Test
    void checkoutExitosoDescuentaStockYEmiteBoleta() {
        Producto producto = producto("PD-001", "Yamaha P-45 Negro", 459990, 30, true, 390990);
        when(productoRepository.findById("PD-001")).thenReturn(Optional.of(producto));
        when(boletaRepository.findTopByOrderByFolioDesc()).thenReturn(Optional.empty());
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BoletaResponseDto respuesta = boletaService.checkout(checkout("exitoso", item("PD-001", 2)));

        assertEquals("Emitida", respuesta.getEstado());
        assertEquals(781980, respuesta.getTotal());
        assertEquals(657126, respuesta.getSubtotalNeto());
        assertEquals(124854, respuesta.getIva());
        assertEquals(28, producto.getStock());
        assertEquals(1, respuesta.getItems().size());
        assertEquals(390990, respuesta.getItems().get(0).getPrecioUnitario());
        assertEquals(781980, respuesta.getItems().get(0).getSubtotal());
        assertNotNull(respuesta.getDatosEnvio());
        verify(productoRepository).save(producto);
    }

    @Test
    void checkoutRechazadoNoDescuentaStockYGuardaEstadoRechazada() {
        Producto producto = producto("PD-001", "Yamaha P-45 Negro", 459990, 30, true, 390990);
        when(productoRepository.findById("PD-001")).thenReturn(Optional.of(producto));
        when(boletaRepository.findTopByOrderByFolioDesc()).thenReturn(Optional.empty());
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BoletaResponseDto respuesta = boletaService.checkout(checkout("rechazado", item("PD-001", 2)));

        assertEquals("Rechazada", respuesta.getEstado());
        assertEquals(30, producto.getStock());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void checkoutIntentarMasTardeNoDescuentaStockYGuardaEstadoPendiente() {
        Producto producto = producto("PD-001", "Yamaha P-45 Negro", 459990, 30, true, 390990);
        when(productoRepository.findById("PD-001")).thenReturn(Optional.of(producto));
        when(boletaRepository.findTopByOrderByFolioDesc()).thenReturn(Optional.empty());
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BoletaResponseDto respuesta = boletaService.checkout(checkout("intentar_mas_tarde", item("PD-001", 2)));

        assertEquals("Pendiente", respuesta.getEstado());
        assertEquals(30, producto.getStock());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void checkoutExitosoSinStockSuficienteLanzaConflictoYNoDescuenta() {
        Producto producto = producto("PD-001", "Yamaha P-45 Negro", 459990, 1, true, 390990);
        when(productoRepository.findById("PD-001")).thenReturn(Optional.of(producto));

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
                () -> boletaService.checkout(checkout("exitoso", item("PD-001", 2))));

        assertEquals(409, excepcion.getStatusCode().value());
        assertEquals(1, producto.getStock());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void checkoutConBoletaExistenteLaActualizaSinCrearOtra() {
        Producto producto = producto("SY-001", "Nord Electro 6D 61", 1899990, 12, true, 1709990);
        Boleta existente = boleta("BOL-SEED-1003", "Pendiente",
                detalle("SY-001", "Nord Electro 6D 61", 1709990, 1));
        when(boletaRepository.findById("BOL-SEED-1003")).thenReturn(Optional.of(existente));
        when(productoRepository.findById("SY-001")).thenReturn(Optional.of(producto));
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CheckoutRequest request = checkout("exitoso", item("SY-001", 1));
        request.setBoletaExistenteId("BOL-SEED-1003");

        BoletaResponseDto respuesta = boletaService.checkout(request);

        assertEquals("BOL-SEED-1003", respuesta.getId());
        assertEquals("Emitida", respuesta.getEstado());
        assertEquals(11, producto.getStock());
        verify(boletaRepository, never()).findTopByOrderByFolioDesc();
    }

    @Test
    void reintentarPagoExitosoDesdePendienteDescuentaStockYEmite() {
        Producto producto = producto("PD-001", "Yamaha P-45 Negro", 459990, 30, true, 390990);
        Boleta pendiente = boleta("BOL-1", "Pendiente",
                detalle("PD-001", "Yamaha P-45 Negro", 390990, 2));
        when(boletaRepository.findById("BOL-1")).thenReturn(Optional.of(pendiente));
        when(productoRepository.findById("PD-001")).thenReturn(Optional.of(producto));
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BoletaResponseDto respuesta = boletaService.reintentarPago(
                "BOL-1", new ReintentoPagoRequest("exitoso", "Webpay Plus / Tarjeta de Crédito"));

        assertEquals("Emitida", respuesta.getEstado());
        assertEquals(28, producto.getStock());
        assertEquals("Webpay Plus / Tarjeta de Crédito", respuesta.getMetodoPago());
        verify(productoRepository).save(producto);
    }

    @Test
    void reintentarPagoSoloPermiteBoletasPendientesORechazadas() {
        Boleta emitida = boleta("BOL-1", "Emitida",
                detalle("PD-001", "Yamaha P-45 Negro", 390990, 1));
        when(boletaRepository.findById("BOL-1")).thenReturn(Optional.of(emitida));

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
                () -> boletaService.reintentarPago("BOL-1", new ReintentoPagoRequest("exitoso", null)));

        assertEquals(409, excepcion.getStatusCode().value());
    }

    @Test
    void anularBoletaEmitidaReponeStock() {
        Producto producto = producto("PD-001", "Yamaha P-45 Negro", 459990, 28, true, 390990);
        Boleta emitida = boleta("BOL-1", "Emitida",
                detalle("PD-001", "Yamaha P-45 Negro", 390990, 2));
        when(boletaRepository.findById("BOL-1")).thenReturn(Optional.of(emitida));
        when(productoRepository.findById("PD-001")).thenReturn(Optional.of(producto));
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BoletaResponseDto respuesta = boletaService.anular("BOL-1");

        assertEquals("Anulada", respuesta.getEstado());
        assertEquals(30, producto.getStock());
        verify(productoRepository).save(producto);
    }

    @Test
    void anularBoletaNoEmitidaLanzaConflicto() {
        Boleta pendiente = boleta("BOL-1", "Pendiente",
                detalle("PD-001", "Yamaha P-45 Negro", 390990, 1));
        when(boletaRepository.findById("BOL-1")).thenReturn(Optional.of(pendiente));

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
                () -> boletaService.anular("BOL-1"));

        assertEquals(409, excepcion.getStatusCode().value());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    private CheckoutRequest checkout(String resultado, ItemCheckoutDto... items) {
        CheckoutRequest request = new CheckoutRequest();
        request.setClienteRun("112223339");
        request.setClienteNombre("Camila");
        request.setClienteApellidos("Soto Perez");
        request.setClienteCorreo("camila@gmail.com");
        request.setMetodoPago("Webpay Plus / Tarjeta de Débito");
        request.setDatosEnvio(new DatosEnvioDto(
                "perfil", "Camila Soto Perez", null,
                2, "Valparaiso", 4, "Valparaiso", "Los Aromos 789", null));
        request.setItems(List.of(items));
        request.setResultadoTransaccion(resultado);
        return request;
    }

    private ItemCheckoutDto item(String codigo, int cantidad) {
        return new ItemCheckoutDto(codigo, cantidad);
    }

    private Producto producto(String codigo, String nombre, int precio, int stock,
                              boolean enOferta, Integer precioOferta) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setEstado("Activo");
        producto.setEnOferta(enOferta);
        producto.setPorcentajeDescuento(0);
        producto.setPrecioOferta(precioOferta);
        return producto;
    }

    private Boleta boleta(String id, String estado, DetalleBoleta... items) {
        Boleta boleta = new Boleta();
        boleta.setId(id);
        boleta.setFolio(1001);
        boleta.setFecha("28-10-2026, 11:15:00");
        boleta.setClienteRun("112223339");
        boleta.setClienteNombre("Camila Soto Perez");
        boleta.setClienteCorreo("camila@gmail.com");
        boleta.setSubtotalNeto(0);
        boleta.setIva(0);
        boleta.setTotal(0);
        boleta.setMetodoPago("Webpay Plus / Tarjeta de Débito");
        boleta.setEstado(estado);

        for (DetalleBoleta detalle : items) {
            detalle.setBoleta(boleta);
            boleta.getItems().add(detalle);
        }
        return boleta;
    }

    private DetalleBoleta detalle(String codigo, String nombre, int precioUnitario, int cantidad) {
        DetalleBoleta detalle = new DetalleBoleta();
        detalle.setCodigo(codigo);
        detalle.setNombre(nombre);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setCantidad(cantidad);
        detalle.setSubtotal(precioUnitario * cantidad);
        return detalle;
    }
}
