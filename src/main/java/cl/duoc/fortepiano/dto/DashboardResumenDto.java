package cl.duoc.fortepiano.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO con métricas consolidadas del Dashboard para administración y reportes")
public class DashboardResumenDto {

    @Schema(description = "Suma total en pesos de ventas con boletas emitidas", example = "4864940")
    private Long ventasTotales;

    @Schema(description = "Cantidad total de órdenes/boletas registradas", example = "5")
    private Long cantidadPedidos;

    @Schema(description = "Cantidad total de unidades vendidas en boletas emitidas", example = "6")
    private Long unidadesVendidas;

    @Schema(description = "Total de productos registrados en el catálogo", example = "8")
    private Long totalProductos;

    @Schema(description = "Cantidad de productos actualmente en oferta", example = "4")
    private Long productosEnOferta;

    @Schema(description = "Cantidad de productos cuyo stock actual es menor o igual al stock crítico", example = "2")
    private Long productosStockCritico;

    @Schema(description = "Distribución detallada de pedidos por estado con cantidad y porcentaje")
    @Builder.Default
    private List<EstadoPedidoDto> distribucionEstados = new ArrayList<>();

    @Schema(description = "Mapa resumen con conteo directo por estado (Emitida, Pendiente, Rechazada, Anulada)")
    @Builder.Default
    private Map<String, Long> conteoPorEstado = new HashMap<>();

    @Schema(description = "Listado de los 5 productos más vendidos ordenados por unidades")
    @Builder.Default
    private List<ProductoVendidoDto> productosMasVendidos = new ArrayList<>();

    @Schema(description = "Listado de los 6 productos con mayor stock disponible")
    @Builder.Default
    private List<ProductoStockDto> productosMayorStock = new ArrayList<>();

    @Schema(description = "Alertas de productos en nivel de stock crítico o agotados")
    @Builder.Default
    private List<AlertaStockDto> alertasStockCritico = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información estadística de un estado de pedido")
    public static class EstadoPedidoDto {
        @Schema(description = "Nombre del estado (Emitida, Pendiente, Rechazada, Anulada)", example = "Emitida")
        private String estado;

        @Schema(description = "Cantidad de boletas en este estado", example = "3")
        private long cantidad;

        @Schema(description = "Porcentaje respecto al total de boletas", example = "60.0")
        private double porcentaje;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Métricas de ventas por producto")
    public static class ProductoVendidoDto {
        @Schema(description = "Código del producto", example = "PD-001")
        private String codigo;

        @Schema(description = "Nombre del producto", example = "Yamaha P-45 Negro")
        private String nombre;

        @Schema(description = "Total de unidades vendidas", example = "2")
        private long unidadesVendidas;

        @Schema(description = "Total de ingresos generados en pesos", example = "781980")
        private long ingresosGenerados;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información de inventario de producto con mayor stock")
    public static class ProductoStockDto {
        @Schema(description = "Código del producto", example = "PD-001")
        private String codigo;

        @Schema(description = "Nombre del producto", example = "Yamaha P-45 Negro")
        private String nombre;

        @Schema(description = "Stock disponible", example = "30")
        private int stock;

        @Schema(description = "Nivel de stock crítico", example = "5")
        private int stockCritico;

        @Schema(description = "ID de la categoría", example = "2")
        private Integer categoriaId;

        @Schema(description = "Estado del producto (Activo/Inactivo)", example = "Activo")
        private String estado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detalle de alerta de stock crítico")
    public static class AlertaStockDto {
        @Schema(description = "Código del producto", example = "PA-001")
        private String codigo;

        @Schema(description = "Nombre del producto", example = "Piano Yamaha B1")
        private String nombre;

        @Schema(description = "Stock actual", example = "2")
        private int stock;

        @Schema(description = "Stock crítico umbral", example = "2")
        private int stockCritico;

        @Schema(description = "Nivel de alerta (AGOTADO o CRITICO)", example = "CRITICO")
        private String nivelAlerta;
    }
}
