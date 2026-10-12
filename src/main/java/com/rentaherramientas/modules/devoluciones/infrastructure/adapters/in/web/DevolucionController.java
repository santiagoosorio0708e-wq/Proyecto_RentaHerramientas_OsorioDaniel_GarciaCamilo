package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.in.web;

import com.rentaherramientas.common.dto.ApiResponseDto;
import com.rentaherramientas.modules.devoluciones.application.dto.CargoAdicionalDto;
import com.rentaherramientas.modules.devoluciones.application.dto.DevolucionDtoMapper;
import com.rentaherramientas.modules.devoluciones.application.dto.DevolucionResponseDto;
import com.rentaherramientas.modules.devoluciones.application.dto.RegistrarDevolucionCommand;
import com.rentaherramientas.modules.devoluciones.application.dto.ReporteDanoRequestDto;
import com.rentaherramientas.modules.devoluciones.application.dto.ReporteDanoResponseDto;
import com.rentaherramientas.modules.devoluciones.domain.model.CargoAdicional;
import com.rentaherramientas.modules.devoluciones.domain.model.Devolucion;
import com.rentaherramientas.modules.devoluciones.domain.model.EvidenciaDano;
import com.rentaherramientas.modules.devoluciones.domain.model.ReporteDano;
import com.rentaherramientas.modules.devoluciones.domain.ports.in.ConsultarDevolucionUseCase;
import com.rentaherramientas.modules.devoluciones.domain.ports.in.GestionarCargosAdicionalesUseCase;
import com.rentaherramientas.modules.devoluciones.domain.ports.in.GestionarReporteDanoUseCase;
import com.rentaherramientas.modules.devoluciones.domain.ports.in.RegistrarDevolucionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devoluciones")
@RequiredArgsConstructor
@Tag(name = "Devoluciones", description = "Endpoints para check-in de retorno de herramientas, reportes de daños, evidencias fotográficas (4FN) y cargos adicionales")
@SecurityRequirement(name = "Bearer Authentication")
public class DevolucionController {

    private final RegistrarDevolucionUseCase registrarDevolucionUseCase;
    private final ConsultarDevolucionUseCase consultarDevolucionUseCase;
    private final GestionarReporteDanoUseCase gestionarReporteDanoUseCase;
    private final GestionarCargosAdicionalesUseCase gestionarCargosAdicionalesUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR')")
    @Operation(summary = "Registrar devolución de herramientas", description = "Registra el check-in físico de las herramientas devueltas, calcula mora automática si hay retraso, genera reportes de averías y finaliza la reserva.")
    @ApiResponse(responseCode = "201", description = "Devolución registrada exitosamente")
    public ResponseEntity<ApiResponseDto<DevolucionResponseDto>> registrarDevolucion(
            @Valid @RequestBody RegistrarDevolucionCommand command) {

        Devolucion devolucion = registrarDevolucionUseCase.registrarDevolucion(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Devolución registrada exitosamente", DevolucionDtoMapper.toDto(devolucion)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR', 'CLIENTE')")
    @Operation(summary = "Obtener detalle de devolución", description = "Retorna la información completa de una devolución por su ID, incluyendo daños y cargos.")
    public ResponseEntity<ApiResponseDto<DevolucionResponseDto>> obtenerPorId(@PathVariable Long id) {
        return consultarDevolucionUseCase.obtenerDevolucionPorId(id)
                .map(d -> ResponseEntity.ok(ApiResponseDto.ok("Devolución encontrada", DevolucionDtoMapper.toDto(d))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("Devolución no encontrada con ID: " + id)));
    }

    @GetMapping("/reserva/{idReserva}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR', 'CLIENTE')")
    @Operation(summary = "Obtener devolución por ID de reserva", description = "Busca el registro de check-in asociado a una reserva en específico.")
    public ResponseEntity<ApiResponseDto<DevolucionResponseDto>> obtenerPorReserva(@PathVariable Long idReserva) {
        return consultarDevolucionUseCase.obtenerDevolucionPorReserva(idReserva)
                .map(d -> ResponseEntity.ok(ApiResponseDto.ok("Devolución de reserva encontrada", DevolucionDtoMapper.toDto(d))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("No existe registro de devolución para la reserva ID: " + idReserva)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR')")
    @Operation(summary = "Listar todas las devoluciones", description = "Obtiene el historial cronológico de todas las devoluciones registradas.")
    public ResponseEntity<ApiResponseDto<List<DevolucionResponseDto>>> listarTodas() {
        List<DevolucionResponseDto> list = consultarDevolucionUseCase.listarDevoluciones().stream()
                .map(DevolucionDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponseDto.ok("Devoluciones obtenidas exitosamente", list));
    }

    @PostMapping("/{id}/danos")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR')")
    @Operation(summary = "Registrar daño adicional", description = "Agrega un informe de daño con soporte de múltiples fotos de evidencia a una devolución existente.")
    public ResponseEntity<ApiResponseDto<ReporteDanoResponseDto>> agregarDano(
            @PathVariable Long id,
            @Valid @RequestBody ReporteDanoRequestDto dto) {

        ReporteDano reporte = ReporteDano.builder()
                .idHerramienta(dto.getIdHerramienta())
                .tipoDano(dto.getTipoDano())
                .descripcionDano(dto.getDescripcionDano())
                .costoEstimadoReparacion(dto.getCostoEstimadoReparacion())
                .responsableAsume(dto.getResponsableAsume())
                .evidencias(new ArrayList<>())
                .build();

        if (dto.getEvidencias() != null) {
            dto.getEvidencias().forEach(e -> reporte.agregarEvidencia(EvidenciaDano.builder()
                    .urlFoto(e.getUrlFoto())
                    .descripcionFoto(e.getDescripcionFoto())
                    .fechaSubida(LocalDateTime.now())
                    .build()));
        }

        ReporteDano guardado = gestionarReporteDanoUseCase.registrarDano(id, reporte);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Reporte de daño agregado exitosamente", DevolucionDtoMapper.toDto(guardado)));
    }

    @GetMapping("/{id}/danos")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR', 'CLIENTE')")
    @Operation(summary = "Listar daños de una devolución", description = "Obtiene todos los reportes de daños y fotos de evidencia asociadas a la devolución.")
    public ResponseEntity<ApiResponseDto<List<ReporteDanoResponseDto>>> listarDanos(@PathVariable Long id) {
        List<ReporteDanoResponseDto> list = gestionarReporteDanoUseCase.listarDanosPorDevolucion(id).stream()
                .map(DevolucionDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponseDto.ok("Reportes de daño obtenidos", list));
    }

    @GetMapping("/{id}/cargos")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR', 'CLIENTE')")
    @Operation(summary = "Listar cargos adicionales de una devolución", description = "Obtiene todos los recargos (mora, daños, reposición) generados en la devolución.")
    public ResponseEntity<ApiResponseDto<List<CargoAdicionalDto>>> listarCargos(@PathVariable Long id) {
        List<CargoAdicionalDto> list = gestionarCargosAdicionalesUseCase.listarCargosPorDevolucion(id).stream()
                .map(DevolucionDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponseDto.ok("Cargos adicionales obtenidos", list));
    }

    @PostMapping("/cargos/{idCargo}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Marcar cargo adicional como pagado", description = "Actualiza el estado de un cargo adicional por mora o daño a pagado.")
    public ResponseEntity<ApiResponseDto<CargoAdicionalDto>> pagarCargo(@PathVariable Long idCargo) {
        CargoAdicional cargo = gestionarCargosAdicionalesUseCase.marcarCargoComoPagado(idCargo);
        return ResponseEntity.ok(ApiResponseDto.ok("Cargo adicional marcado como pagado", DevolucionDtoMapper.toDto(cargo)));
    }
}
