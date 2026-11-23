package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.dto.DashboardResumenDto;
import cl.duoc.fortepiano.dto.DashboardResumenDto.AlertaStockDto;
import cl.duoc.fortepiano.dto.DashboardResumenDto.EstadoPedidoDto;
import cl.duoc.fortepiano.dto.DashboardResumenDto.ProductoStockDto;
import cl.duoc.fortepiano.dto.DashboardResumenDto.ProductoVendidoDto;
import cl.duoc.fortepiano.entity.Boleta;
import cl.duoc.fortepiano.entity.DetalleBoleta;
import cl.duoc.fortepiano.entity.Producto;
import cl.duoc.fortepiano.repository.BoletaRepository;
import cl.duoc.fortepiano.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final List<String> ESTADOS_PEDIDO = List.of("Emitida", "Pendiente", "Rechazada", "Anulada");

    private final ProductoRepository productoRepository;
    private final BoletaRepository boletaRepository;

    @Transactional(readOnly = true)
    public DashboardResumenDto obtenerResumen() {
        List<Producto> productos = productoRepository.findAll();
        List<Boleta> boletas = boletaRepository.findAll();

        return calcularMetricas(productos, boletas);
    }

    public DashboardResumenDto calcularMetricas(List<Producto> productos, List<Boleta> boletas) {
        if (productos == null) productos = Collections.emptyList();
        if (boletas == null) boletas = Collections.emptyList();

        // 1. Filtrar boletas emitidas para ventas y unidades
        List<Boleta> emitidas = boletas.stream()
                .filter(b -> b.getEstado() != null && b.getEstado().trim().equalsIgnoreCase("Emitida"))
                .toList();

        long ventasTotales = emitidas.stream()
                .mapToLong(b -> b.getTotal() != null ? b.getTotal() : 0L)
                .sum();

        long unidadesVendidas = emitidas.stream()
                .filter(b -> b.getItems() != null)
                .flatMap(b -> b.getItems().stream())
                .mapToLong(item -> item.getCantidad() != null ? item.getCantidad() : 0L)
                .sum();

        long cantidadPedidos = boletas.size();

        // 2. Métricas de inventario
        long totalProductos = productos.size();
        long productosEnOferta = productos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getEnOferta()))
                .count();

        long productosStockCritico = productos.stream()
                .filter(p -> p.getStock() != null && p.getStockCritico() != null && p.getStock() <= p.getStockCritico())
                .count();

        // 3. Distribución por estado
        Map<String, Long> conteoPorEstado = new LinkedHashMap<>();
        for (String est : ESTADOS_PEDIDO) {
            conteoPorEstado.put(est, 0L);
        }
        for (Boleta b : boletas) {
            String est = normalizarEstado(b.getEstado());
            conteoPorEstado.put(est, conteoPorEstado.getOrDefault(est, 0L) + 1);
        }

        List<EstadoPedidoDto> distribucionEstados = new ArrayList<>();
        for (Map.Entry<String, Long> entry : conteoPorEstado.entrySet()) {
            double porcentaje = cantidadPedidos > 0
                    ? Math.round((entry.getValue() * 100.0 / cantidadPedidos) * 10.0) / 10.0
                    : 0.0;
            distribucionEstados.add(EstadoPedidoDto.builder()
                    .estado(entry.getKey())
                    .cantidad(entry.getValue())
                    .porcentaje(porcentaje)
                    .build());
        }

        // 4. Productos más vendidos (Top 5 por unidades en boletas Emitidas)
        Map<String, ProductoVendidoAcumulado> acumulador = new LinkedHashMap<>();
        for (Boleta b : emitidas) {
            if (b.getItems() == null) continue;
            for (DetalleBoleta item : b.getItems()) {
                String codigo = item.getCodigo();
                if (codigo == null) continue;
                ProductoVendidoAcumulado acc = acumulador.computeIfAbsent(codigo, k -> new ProductoVendidoAcumulado(codigo, item.getNombre()));
                if (item.getNombre() != null && (acc.nombre == null || acc.nombre.isBlank())) {
                    acc.nombre = item.getNombre();
                }
                long cant = item.getCantidad() != null ? item.getCantidad() : 0L;
                long subtotal = item.getSubtotal() != null ? item.getSubtotal() : 0L;
                acc.unidades += cant;
                acc.ingresos += subtotal;
            }
        }

        List<ProductoVendidoDto> productosMasVendidos = acumulador.values().stream()
                .sorted(Comparator.comparingLong((ProductoVendidoAcumulado a) -> a.unidades).reversed())
                .limit(5)
                .map(a -> ProductoVendidoDto.builder()
                        .codigo(a.codigo)
                        .nombre(a.nombre)
                        .unidadesVendidas(a.unidades)
                        .ingresosGenerados(a.ingresos)
                        .build())
                .collect(Collectors.toList());

        // 5. Productos con mayor stock (Top 6 ordenados por stock descendente)
        List<ProductoStockDto> productosMayorStock = productos.stream()
                .filter(p -> p.getStock() != null)
                .sorted(Comparator.comparingInt(Producto::getStock).reversed())
                .limit(6)
                .map(p -> ProductoStockDto.builder()
                        .codigo(p.getCodigo())
                        .nombre(p.getNombre())
                        .stock(p.getStock() != null ? p.getStock() : 0)
                        .stockCritico(p.getStockCritico() != null ? p.getStockCritico() : 0)
                        .categoriaId(p.getCategoriaId())
                        .estado(p.getEstado())
                        .build())
                .collect(Collectors.toList());

        // 6. Alertas de stock crítico (stock <= stockCritico)
        List<AlertaStockDto> alertasStockCritico = productos.stream()
                .filter(p -> p.getStock() != null && p.getStockCritico() != null && p.getStock() <= p.getStockCritico())
                .sorted(Comparator.comparingInt(Producto::getStock))
                .map(p -> {
                    String nivel = (p.getStock() == 0) ? "AGOTADO" : "CRITICO";
                    return AlertaStockDto.builder()
                            .codigo(p.getCodigo())
                            .nombre(p.getNombre())
                            .stock(p.getStock())
                            .stockCritico(p.getStockCritico())
                            .nivelAlerta(nivel)
                            .build();
                })
                .collect(Collectors.toList());

        return DashboardResumenDto.builder()
                .ventasTotales(ventasTotales)
                .cantidadPedidos(cantidadPedidos)
                .unidadesVendidas(unidadesVendidas)
                .totalProductos(totalProductos)
                .productosEnOferta(productosEnOferta)
                .productosStockCritico(productosStockCritico)
                .distribucionEstados(distribucionEstados)
                .conteoPorEstado(conteoPorEstado)
                .productosMasVendidos(productosMasVendidos)
                .productosMayorStock(productosMayorStock)
                .alertasStockCritico(alertasStockCritico)
                .build();
    }

    private String normalizarEstado(String estado) {
        if (estado == null) return "Pendiente";
        String limpio = estado.trim();
        for (String est : ESTADOS_PEDIDO) {
            if (est.equalsIgnoreCase(limpio)) {
                return est;
            }
        }
        return limpio;
    }

    private static class ProductoVendidoAcumulado {
        String codigo;
        String nombre;
        long unidades;
        long ingresos;

        ProductoVendidoAcumulado(String codigo, String nombre) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.unidades = 0;
            this.ingresos = 0;
        }
    }
}
