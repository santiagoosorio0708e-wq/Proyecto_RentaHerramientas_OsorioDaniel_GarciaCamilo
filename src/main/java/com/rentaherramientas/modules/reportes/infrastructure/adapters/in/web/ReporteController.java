package com.rentaherramientas.modules.reportes.infrastructure.adapters.in.web;

import com.rentaherramientas.common.dto.ApiResponseDto;
import com.rentaherramientas.modules.reportes.application.dto.ClienteFrecuenteDto;
import com.rentaherramientas.modules.reportes.application.dto.DashboardCompletoDto;
import com.rentaherramientas.modules.reportes.application.dto.HerramientaPopularidadDto;
import com.rentaherramientas.modules.reportes.application.dto.IngresoPorProveedorDto;
import com.rentaherramientas.modules.reportes.application.dto.MetricasOperativasDto;
import com.rentaherramientas.modules.reportes.application.dto.ReporteDtoMapper;
import com.rentaherramientas.modules.reportes.application.dto.ResumenFinancieroDto;
import com.rentaherramientas.modules.reportes.application.service.ReporteApplicationService;
import com.rentaherramientas.modules.reportes.domain.ports.in.ConsultarReportesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes y Métricas", description = "Endpoints analíticos para dashboards gerenciales, métricas financieras, herramientas populares y estadísticas operativas")
@SecurityRequirement(name = "Bearer Authentication")
public class ReporteController {

    private final ConsultarReportesUseCase consultarReportesUseCase;
    private final ReporteApplicationService reporteApplicationService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR')")
    @Operation(summary = "Dashboard ejecutivo consolidado", description = "Provee una vista integral que consolida KPIs financieros, métricas operativas de la flota, herramientas más alquiladas y clientes frecuentes.")
    public ResponseEntity<ApiResponseDto<DashboardCompletoDto>> obtenerDashboard() {
        DashboardCompletoDto dashboard = reporteApplicationService.obtenerDashboardCompleto();
        return ResponseEntity.ok(ApiResponseDto.ok("Dashboard ejecutivo generado exitosamente", dashboard));
    }

    @GetMapping("/financiero")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR')")
    @Operation(summary = "Resumen financiero", description = "Retorna el desglose financiero del sistema: total recaudado, subtotal, IVA acumulado (19%) y recargos cobrados.")
    public ResponseEntity<ApiResponseDto<ResumenFinancieroDto>> obtenerResumenFinanciero() {
        var domain = consultarReportesUseCase.obtenerResumenFinanciero();
        return ResponseEntity.ok(ApiResponseDto.ok("Resumen financiero obtenido", ReporteDtoMapper.toDto(domain)));
    }

    @GetMapping("/herramientas-populares")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR')")
    @Operation(summary = "Top herramientas más alquiladas", description = "Lista ordenada de herramientas con mayor número de alquileres e ingresos generados.")
    public ResponseEntity<ApiResponseDto<List<HerramientaPopularidadDto>>> obtenerHerramientasPopulares(
            @RequestParam(defaultValue = "10") int limite) {
        List<HerramientaPopularidadDto> list = consultarReportesUseCase.obtenerTopHerramientasPopulares(limite).stream()
                .map(ReporteDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponseDto.ok("Herramientas populares obtenidas", list));
    }

    @GetMapping("/clientes-frecuentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR')")
    @Operation(summary = "Top clientes frecuentes", description = "Ranking de clientes con mayor volumen de reservas completadas y gasto total.")
    public ResponseEntity<ApiResponseDto<List<ClienteFrecuenteDto>>> obtenerClientesFrecuentes(
            @RequestParam(defaultValue = "10") int limite) {
        List<ClienteFrecuenteDto> list = consultarReportesUseCase.obtenerTopClientesFrecuentes(limite).stream()
                .map(ReporteDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponseDto.ok("Clientes frecuentes obtenidos", list));
    }

    @GetMapping("/metricas-operativas")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR')")
    @Operation(summary = "Métricas operativas de maquinaria", description = "Porcentajes de inventario disponible, en alquiler, mantenimiento y tasa de incidencias por daños.")
    public ResponseEntity<ApiResponseDto<MetricasOperativasDto>> obtenerMetricasOperativas() {
        var domain = consultarReportesUseCase.obtenerMetricasOperativas();
        return ResponseEntity.ok(ApiResponseDto.ok("Métricas operativas obtenidas", ReporteDtoMapper.toDto(domain)));
    }

    @GetMapping("/proveedores")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR')")
    @Operation(summary = "Ingresos por proveedor", description = "Distribución de ingresos y volumen de alquileres generado por cada proveedor registrado.")
    public ResponseEntity<ApiResponseDto<List<IngresoPorProveedorDto>>> obtenerIngresosPorProveedor() {
        List<IngresoPorProveedorDto> list = consultarReportesUseCase.obtenerIngresosPorProveedor().stream()
                .map(ReporteDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponseDto.ok("Ingresos por proveedor obtenidos", list));
    }
}
