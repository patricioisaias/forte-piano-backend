package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.dto.DashboardResumenDto;
import cl.duoc.fortepiano.dto.DashboardResumenDto.*;
import cl.duoc.fortepiano.entity.Boleta;
import cl.duoc.fortepiano.entity.DetalleBoleta;
import cl.duoc.fortepiano.entity.Producto;
import cl.duoc.fortepiano.repository.BoletaRepository;
import cl.duoc.fortepiano.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private BoletaRepository boletaRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private List<Producto> productosPrueba;
    private List<Boleta> boletasPrueba;

    @BeforeEach
    void setUp() {
        // Catálogo de prueba
        productosPrueba = List.of(
                crearProducto("PD-001", "Yamaha P-45 Negro", 459990, 30, 5, 2, true),
                crearProducto("PD-006", "Yamaha Arius YDP-146", 699990, 25, 5, 2, false),
                crearProducto("AD-003", "Pedal de Sustain SP-34", 15000, 20, 5, 4, false),
                crearProducto("PA-001", "Piano Yamaha B1", 3299000, 2, 2, 1, false), // Crítico (stock <= stockCritico)
                crearProducto("AC-005", "Soporte Teclado", 17990, 0, 3, 4, false)    // Agotado (stock == 0 <= stockCritico)
        );

        // Boletas de prueba
        boletasPrueba = new ArrayList<>();

        // Boleta 1: Emitida, total 811980
        Boleta b1 = crearBoleta("BOL-1001", 1001, 811980, "Emitida");
        b1.setItems(List.of(
                crearDetalle(b1, "PD-001", "Yamaha P-45 Negro", 390990, 2, 781980),
                crearDetalle(b1, "AD-003", "Pedal de Sustain SP-34", 15000, 2, 30000)
        ));
        boletasPrueba.add(b1);

        // Boleta 2: Emitida, total 699990
        Boleta b2 = crearBoleta("BOL-1002", 1002, 699990, "Emitida");
        b2.setItems(List.of(
                crearDetalle(b2, "PD-006", "Yamaha Arius YDP-146", 699990, 1, 699990)
        ));
        boletasPrueba.add(b2);

        // Boleta 3: Pendiente, total 1709990 (no debe sumar a ventas ni unidades)
        Boleta b3 = crearBoleta("BOL-1003", 1003, 1709990, "Pendiente");
        b3.setItems(List.of(
                crearDetalle(b3, "SY-001", "Nord Electro", 1709990, 1, 1709990)
        ));
        boletasPrueba.add(b3);

        // Boleta 4: Rechazada, total 527990
        Boleta b4 = crearBoleta("BOL-1004", 1004, 527990, "Rechazada");
        boletasPrueba.add(b4);

        // Boleta 5: Anulada, total 100000
        Boleta b5 = crearBoleta("BOL-1005", 1005, 100000, "Anulada");
        boletasPrueba.add(b5);
    }

    @Test
    @DisplayName("obtenerResumen calcula métricas de ventas y pedidos correctamente")
    void obtenerResumenMetricasVentasYPedidos() {
        when(productoRepository.findAll()).thenReturn(productosPrueba);
        when(boletaRepository.findAll()).thenReturn(boletasPrueba);

        DashboardResumenDto resumen = dashboardService.obtenerResumen();

        assertNotNull(resumen);

        // Ventas totales = 811980 + 699990 = 1511970 (sólo Emitidas)
        assertEquals(1511970L, resumen.getVentasTotales());

        // Cantidad de pedidos = 5 boletas totales
        assertEquals(5L, resumen.getCantidadPedidos());

        // Unidades vendidas en emitidas = (2 + 2) + 1 = 5
        assertEquals(5L, resumen.getUnidadesVendidas());

        // Métricas de catálogo
        assertEquals(5L, resumen.getTotalProductos());
        assertEquals(1L, resumen.getProductosEnOferta()); // Sólo PD-001 en oferta
        assertEquals(2L, resumen.getProductosStockCritico()); // PA-001 (2<=2) y AC-005 (0<=3)
    }

    @Test
    @DisplayName("obtenerResumen distribuye pedidos por estado con cantidades y porcentajes exactos")
    void obtenerResumenDistribucionEstados() {
        when(productoRepository.findAll()).thenReturn(productosPrueba);
        when(boletaRepository.findAll()).thenReturn(boletasPrueba);

        DashboardResumenDto resumen = dashboardService.obtenerResumen();

        assertEquals(2L, resumen.getConteoPorEstado().get("Emitida"));
        assertEquals(1L, resumen.getConteoPorEstado().get("Pendiente"));
        assertEquals(1L, resumen.getConteoPorEstado().get("Rechazada"));
        assertEquals(1L, resumen.getConteoPorEstado().get("Anulada"));

        List<EstadoPedidoDto> dist = resumen.getDistribucionEstados();
        assertEquals(4, dist.size());

        EstadoPedidoDto emitidaDto = dist.stream().filter(e -> "Emitida".equals(e.getEstado())).findFirst().orElseThrow();
        assertEquals(2L, emitidaDto.getCantidad());
        assertEquals(40.0, emitidaDto.getPorcentaje()); // 2 de 5 = 40.0%

        EstadoPedidoDto pendienteDto = dist.stream().filter(e -> "Pendiente".equals(e.getEstado())).findFirst().orElseThrow();
        assertEquals(1L, pendienteDto.getCantidad());
        assertEquals(20.0, pendienteDto.getPorcentaje()); // 1 de 5 = 20.0%
    }

    @Test
    @DisplayName("obtenerResumen entrega el ranking de productos más vendidos")
    void obtenerResumenProductosMasVendidos() {
        when(productoRepository.findAll()).thenReturn(productosPrueba);
        when(boletaRepository.findAll()).thenReturn(boletasPrueba);

        DashboardResumenDto resumen = dashboardService.obtenerResumen();

        List<ProductoVendidoDto> top = resumen.getProductosMasVendidos();
        assertNotNull(top);
        assertFalse(top.isEmpty());

        // PD-001 y AD-003 vendieron 2 unidades cada uno, PD-006 vendió 1 unidad
        long maxUnidades = top.get(0).getUnidadesVendidas();
        assertEquals(2L, maxUnidades);
    }

    @Test
    @DisplayName("obtenerResumen entrega productos con mayor stock ordenados descendentemente")
    void obtenerResumenProductosMayorStock() {
        when(productoRepository.findAll()).thenReturn(productosPrueba);
        when(boletaRepository.findAll()).thenReturn(boletasPrueba);

        DashboardResumenDto resumen = dashboardService.obtenerResumen();

        List<ProductoStockDto> mayorStock = resumen.getProductosMayorStock();
        assertEquals(5, mayorStock.size());
        assertEquals("PD-001", mayorStock.get(0).getCodigo());
        assertEquals(30, mayorStock.get(0).getStock());
        assertEquals("PD-006", mayorStock.get(1).getCodigo());
        assertEquals(25, mayorStock.get(1).getStock());
    }

    @Test
    @DisplayName("obtenerResumen genera alertas de stock crítico correctamente")
    void obtenerResumenAlertasStockCritico() {
        when(productoRepository.findAll()).thenReturn(productosPrueba);
        when(boletaRepository.findAll()).thenReturn(boletasPrueba);

        DashboardResumenDto resumen = dashboardService.obtenerResumen();

        List<AlertaStockDto> alertas = resumen.getAlertasStockCritico();
        assertEquals(2, alertas.size());

        // El primer producto por stock ascendente es AC-005 con stock 0 (AGOTADO)
        assertEquals("AC-005", alertas.get(0).getCodigo());
        assertEquals(0, alertas.get(0).getStock());
        assertEquals("AGOTADO", alertas.get(0).getNivelAlerta());

        // El segundo es PA-001 con stock 2 (CRITICO)
        assertEquals("PA-001", alertas.get(1).getCodigo());
        assertEquals(2, alertas.get(1).getStock());
        assertEquals("CRITICO", alertas.get(1).getNivelAlerta());
    }

    @Test
    @DisplayName("calcularMetricas maneja listas vacías y valores nulos sin errores")
    void calcularMetricasConDatosVacios() {
        DashboardResumenDto vacio = dashboardService.calcularMetricas(List.of(), List.of());

        assertNotNull(vacio);
        assertEquals(0L, vacio.getVentasTotales());
        assertEquals(0L, vacio.getCantidadPedidos());
        assertEquals(0L, vacio.getUnidadesVendidas());
        assertEquals(0L, vacio.getTotalProductos());
        assertTrue(vacio.getProductosMasVendidos().isEmpty());
        assertTrue(vacio.getProductosMayorStock().isEmpty());
        assertTrue(vacio.getAlertasStockCritico().isEmpty());

        DashboardResumenDto nulos = dashboardService.calcularMetricas(null, null);
        assertNotNull(nulos);
        assertEquals(0L, nulos.getVentasTotales());
    }

    // Métodos auxiliares
    private Producto crearProducto(String codigo, String nombre, int precio, int stock, int stockCritico, int categoriaId, boolean enOferta) {
        Producto p = new Producto();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setStockCritico(stockCritico);
        p.setCategoriaId(categoriaId);
        p.setEnOferta(enOferta);
        p.setEstado("Activo");
        return p;
    }

    private Boleta crearBoleta(String id, int folio, int total, String estado) {
        Boleta b = new Boleta();
        b.setId(id);
        b.setFolio(folio);
        b.setTotal(total);
        b.setEstado(estado);
        b.setFecha("22-11-2026, 12:00:00");
        b.setClienteNombre("Cliente Prueba");
        b.setClienteCorreo("cliente@gmail.com");
        b.setSubtotalNeto((int) Math.round(total / 1.19));
        b.setIva(total - b.getSubtotalNeto());
        b.setMetodoPago("Webpay Plus");
        b.setItems(new ArrayList<>());
        return b;
    }

    private DetalleBoleta crearDetalle(Boleta boleta, String codigo, String nombre, int precioUnitario, int cantidad, int subtotal) {
        DetalleBoleta d = new DetalleBoleta();
        d.setBoleta(boleta);
        d.setCodigo(codigo);
        d.setNombre(nombre);
        d.setPrecioUnitario(precioUnitario);
        d.setCantidad(cantidad);
        d.setSubtotal(subtotal);
        return d;
    }
}
