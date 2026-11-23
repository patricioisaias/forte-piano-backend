package cl.duoc.fortepiano.controller;

import cl.duoc.fortepiano.dto.DashboardResumenDto;
import cl.duoc.fortepiano.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints para métricas analíticas y resumen estadístico de ventas, pedidos e inventario")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/resumen")
    @Operation(
            summary = "Obtener resumen ejecutivo y métricas del dashboard",
            description = "Retorna métricas consolidadas de ventas totales, cantidad de pedidos, distribución de pedidos por estado (Emitida, Pendiente, Rechazada, Anulada), productos más vendidos, productos con mayor stock y alertas de stock crítico para paneles de administración"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumen del dashboard generado exitosamente")
    })
    public ResponseEntity<DashboardResumenDto> obtenerResumen() {
        return ResponseEntity.ok(dashboardService.obtenerResumen());
    }
}
