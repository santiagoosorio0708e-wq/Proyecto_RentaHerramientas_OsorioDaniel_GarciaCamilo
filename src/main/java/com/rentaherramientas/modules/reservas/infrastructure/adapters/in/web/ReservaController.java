package com.rentaherramientas.modules.reservas.infrastructure.adapters.in.web;

import com.rentaherramientas.common.dto.ApiResponseDto;
import com.rentaherramientas.modules.reservas.application.dto.CrearReservaCommand;
import com.rentaherramientas.modules.reservas.application.dto.RechazarReservaRequest;
import com.rentaherramientas.modules.reservas.application.dto.ReservaDtoMapper;
import com.rentaherramientas.modules.reservas.application.dto.ReservaResponseDto;
import com.rentaherramientas.modules.reservas.domain.model.Reserva;
import com.rentaherramientas.modules.reservas.domain.ports.in.CambiarEstadoReservaUseCase;
import com.rentaherramientas.modules.reservas.domain.ports.in.ConsultarReservaUseCase;
import com.rentaherramientas.modules.reservas.domain.ports.in.CrearReservaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador de Entrada Web: Controlador REST para el Bounded Context de Reservas.
 * Expone operaciones seguras para solicitar, aprobar, rechazar, cancelar y auditar reservas.
 *
 * @author Daniel Osorio (DEV 2)
 */
@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas y Alquileres", description = "Endpoints para la gestión del ciclo de vida de alquileres de herramientas")
public class ReservaController {

    private final CrearReservaUseCase crearReservaUseCase;
    private final ConsultarReservaUseCase consultarReservaUseCase;
    private final CambiarEstadoReservaUseCase cambiarEstadoReservaUseCase;

    @PostMapping
    @Operation(summary = "Crear nueva reserva de alquiler", description = "Permite a un cliente solicitar el alquiler de una o más herramientas en un rango de fechas.")
    @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente")
    @ApiResponse(responseCode = "422", description = "Conflicto de disponibilidad o fechas no válidas")
    public ResponseEntity<ApiResponseDto<ReservaResponseDto>> crearReserva(
            @Valid @RequestBody CrearReservaCommand command
    ) {
        Reserva reserva = crearReservaUseCase.crearReserva(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Reserva registrada con éxito", ReservaDtoMapper.toResponseDto(reserva)));
    }

    @GetMapping
    @Operation(summary = "Listar todas las reservas", description = "Retorna el listado global de reservas (para Admin o Proveedor).")
    public ResponseEntity<ApiResponseDto<List<ReservaResponseDto>>> listarTodas() {
        List<Reserva> lista = consultarReservaUseCase.listarTodas();
        List<ReservaResponseDto> dtos = lista.stream().map(ReservaDtoMapper::toResponseDto).toList();
        return ResponseEntity.ok(ApiResponseDto.ok("Reservas obtenidas exitosamente", dtos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar reserva por ID", description = "Obtiene los detalles completos de una reserva específica.")
    @ApiResponse(responseCode = "200", description = "Reserva encontrada")
    @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    public ResponseEntity<ApiResponseDto<ReservaResponseDto>> obtenerPorId(
            @Parameter(description = "ID único de la reserva") @PathVariable Long id
    ) {
        Reserva reserva = consultarReservaUseCase.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponseDto.ok(ReservaDtoMapper.toResponseDto(reserva)));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Consultar reserva por código único", description = "Busca una reserva utilizando su identificador legible (ej. RES-2026-0001).")
    public ResponseEntity<ApiResponseDto<ReservaResponseDto>> obtenerPorCodigo(
            @Parameter(description = "Código alfanumérico de la reserva") @PathVariable String codigo
    ) {
        Reserva reserva = consultarReservaUseCase.obtenerPorCodigo(codigo);
        return ResponseEntity.ok(ApiResponseDto.ok(ReservaDtoMapper.toResponseDto(reserva)));
    }

    @GetMapping("/cliente/{idCliente}")
    @Operation(summary = "Listar historial de reservas de un cliente", description = "Obtiene el historial cronológico de alquileres realizados por un cliente.")
    public ResponseEntity<ApiResponseDto<List<ReservaResponseDto>>> listarPorCliente(
            @Parameter(description = "ID del cliente") @PathVariable Long idCliente
    ) {
        List<Reserva> lista = consultarReservaUseCase.listarPorCliente(idCliente);
        List<ReservaResponseDto> dtos = lista.stream().map(ReservaDtoMapper::toResponseDto).toList();
        return ResponseEntity.ok(ApiResponseDto.ok(dtos));
    }

    @PatchMapping("/{id}/aprobar")
    @Operation(summary = "Aprobar solicitud de reserva", description = "Cambia el estado de una reserva a APROBADA (realizado por el proveedor o admin).")
    public ResponseEntity<ApiResponseDto<ReservaResponseDto>> aprobarReserva(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Long usuarioId
    ) {
        Reserva actualizada = cambiarEstadoReservaUseCase.aprobarReserva(id, usuarioId);
        return ResponseEntity.ok(ApiResponseDto.ok("Reserva aprobada con éxito", ReservaDtoMapper.toResponseDto(actualizada)));
    }

    @PatchMapping("/{id}/rechazar")
    @Operation(summary = "Rechazar solicitud de reserva", description = "Cambia el estado de una reserva a RECHAZADA especificando el motivo.")
    public ResponseEntity<ApiResponseDto<ReservaResponseDto>> rechazarReserva(
            @PathVariable Long id,
            @Valid @RequestBody RechazarReservaRequest request
    ) {
        Reserva actualizada = cambiarEstadoReservaUseCase.rechazarReserva(id, request.getMotivo(), request.getUsuarioId());
        return ResponseEntity.ok(ApiResponseDto.ok("Reserva rechazada", ReservaDtoMapper.toResponseDto(actualizada)));
    }

    @PatchMapping("/{id}/iniciar")
    @Operation(summary = "Confirmar entrega e iniciar alquiler", description = "Pasa la reserva a estado EN_CURSO cuando la maquinaria es entregada al cliente.")
    public ResponseEntity<ApiResponseDto<ReservaResponseDto>> iniciarAlquiler(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Long usuarioId
    ) {
        Reserva actualizada = cambiarEstadoReservaUseCase.iniciarAlquiler(id, usuarioId);
        return ResponseEntity.ok(ApiResponseDto.ok("Alquiler iniciado en curso", ReservaDtoMapper.toResponseDto(actualizada)));
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar alquiler", description = "Pasa la reserva a estado FINALIZADA una vez recibida la herramienta.")
    @ApiResponse(responseCode = "200", description = "Alquiler culminado exitosamente")
    @ApiResponse(responseCode = "422", description = "La reserva no está en estado válido para finalizar")
    public ResponseEntity<ApiResponseDto<ReservaResponseDto>> finalizarAlquiler(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Long usuarioId
    ) {
        Reserva actualizada = cambiarEstadoReservaUseCase.finalizarAlquiler(id, usuarioId);
        return ResponseEntity.ok(ApiResponseDto.ok("Alquiler finalizado correctamente", ReservaDtoMapper.toResponseDto(actualizada)));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva", description = "Permite cancelar una reserva antes de que el equipo sea despachado.")
    @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente")
    @ApiResponse(responseCode = "422", description = "La reserva ya fue iniciada o despachada")
    public ResponseEntity<ApiResponseDto<ReservaResponseDto>> cancelarReserva(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Long usuarioId
    ) {
        Reserva actualizada = cambiarEstadoReservaUseCase.cancelarReserva(id, usuarioId);
        return ResponseEntity.ok(ApiResponseDto.ok("Reserva cancelada", ReservaDtoMapper.toResponseDto(actualizada)));
    }
}
