package com.rentaherramientas.modules.pagos.infrastructure.adapters.in.web;

import com.rentaherramientas.common.dto.ApiResponseDto;
import com.rentaherramientas.modules.pagos.application.dto.*;
import com.rentaherramientas.modules.pagos.application.service.PagoApplicationService;
import com.rentaherramientas.modules.pagos.domain.model.MetodoPago;
import com.rentaherramientas.modules.pagos.domain.model.Pago;
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
 * Adaptador de Entrada Web: Controlador REST para el procesamiento de Pagos en Línea.
 * Orquesta la simulación de pasarelas de pago y la generación síncrona de facturas electrónicas.
 *
 * @author Daniel Osorio (DEV 2)
 */
@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos y Facturación", description = "Endpoints para procesamiento de pagos en línea, pasarelas y emisión automática de facturas con IVA")
public class PagoController {

    private final PagoApplicationService pagoApplicationService;

    @PostMapping
    @Operation(summary = "Procesar pago en línea y emitir factura", description = "Procesa una transacción con método de pago (PSE, Tarjeta, etc.), simula la pasarela y genera factura electrónica automáticamente.")
    @ApiResponse(responseCode = "201", description = "Pago procesado y aprobado exitosamente con factura generada")
    @ApiResponse(responseCode = "422", description = "Error en validación de pago o método de pago inactivo")
    public ResponseEntity<ApiResponseDto<PagoResponseDto>> procesarPago(
            @Valid @RequestBody ProcesarPagoCommand command
    ) {
        Pago pago = pagoApplicationService.procesarPago(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.ok("Pago aprobado y factura emitida con éxito", PagoDtoMapper.toPagoResponseDto(pago)));
    }

    @GetMapping
    @Operation(summary = "Listar todas las transacciones de pago", description = "Consulta global de transacciones de pago (para Admin / Auditor).")
    public ResponseEntity<ApiResponseDto<List<PagoResponseDto>>> listarTodos() {
        List<Pago> pagos = pagoApplicationService.listarTodosLosPagos();
        List<PagoResponseDto> dtos = pagos.stream().map(PagoDtoMapper::toPagoResponseDto).toList();
        return ResponseEntity.ok(ApiResponseDto.ok("Transacciones de pago obtenidas con éxito", dtos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar pago por ID", description = "Obtiene los detalles de una transacción de pago por su clave primaria.")
    public ResponseEntity<ApiResponseDto<PagoResponseDto>> obtenerPorId(
            @Parameter(description = "ID del pago") @PathVariable Long id
    ) {
        Pago pago = pagoApplicationService.obtenerPagoPorId(id);
        return ResponseEntity.ok(ApiResponseDto.ok(PagoDtoMapper.toPagoResponseDto(pago)));
    }

    @GetMapping("/transaccion/{numeroTransaccion}")
    @Operation(summary = "Consultar pago por código de transacción", description = "Busca un pago por su identificador único generado (ej. TRX-XXXXXX).")
    public ResponseEntity<ApiResponseDto<PagoResponseDto>> obtenerPorTransaccion(
            @PathVariable String numeroTransaccion
    ) {
        Pago pago = pagoApplicationService.obtenerPagoPorTransaccion(numeroTransaccion);
        return ResponseEntity.ok(ApiResponseDto.ok(PagoDtoMapper.toPagoResponseDto(pago)));
    }

    @GetMapping("/reserva/{idReserva}")
    @Operation(summary = "Listar pagos de una reserva", description = "Obtiene todos los pagos (anticipo, saldo, multas) asociados a una reserva.")
    public ResponseEntity<ApiResponseDto<List<PagoResponseDto>>> listarPorReserva(
            @PathVariable Long idReserva
    ) {
        List<Pago> pagos = pagoApplicationService.listarPagosPorReserva(idReserva);
        List<PagoResponseDto> dtos = pagos.stream().map(PagoDtoMapper::toPagoResponseDto).toList();
        return ResponseEntity.ok(ApiResponseDto.ok(dtos));
    }

    @GetMapping("/metodos")
    @Operation(summary = "Listar métodos de pago disponibles", description = "Retorna el catálogo activo de canales de pago (PSE, Tarjetas, Nequi, etc.).")
    public ResponseEntity<ApiResponseDto<List<MetodoPagoDto>>> listarMetodosPago() {
        List<MetodoPago> metodos = pagoApplicationService.listarMetodosPago();
        List<MetodoPagoDto> dtos = metodos.stream().map(PagoDtoMapper::toMetodoPagoDto).toList();
        return ResponseEntity.ok(ApiResponseDto.ok("Métodos de pago activos obtenidos", dtos));
    }
}
