package com.rentaherramientas.modules.pagos.infrastructure.adapters.in.web;

import com.rentaherramientas.common.dto.ApiResponseDto;
import com.rentaherramientas.modules.pagos.application.dto.FacturaResponseDto;
import com.rentaherramientas.modules.pagos.application.dto.PagoDtoMapper;
import com.rentaherramientas.modules.pagos.application.service.PagoApplicationService;
import com.rentaherramientas.modules.pagos.domain.model.Factura;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador de Entrada Web: Controlador REST para la consulta de Facturas Electrónicas.
 * Gestiona la visualización del desglose de IVA (19%) y descarga simulada de comprobantes fiscales.
 *
 * @author Daniel Osorio (DEV 2)
 */
@RestController
@RequestMapping("/facturas")
@RequiredArgsConstructor
@Tag(name = "Facturación Fiscal", description = "Endpoints para consulta, verificación tributaria y descarga de facturas y comprobantes")
public class FacturaController {

    private final PagoApplicationService pagoApplicationService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar factura por ID", description = "Obtiene los detalles fiscales y desglose de ítems de una factura.")
    public ResponseEntity<ApiResponseDto<FacturaResponseDto>> obtenerPorId(@PathVariable Long id) {
        Factura factura = pagoApplicationService.obtenerFacturaPorId(id);
        return ResponseEntity.ok(ApiResponseDto.ok(PagoDtoMapper.toFacturaResponseDto(factura)));
    }

    @GetMapping("/numero/{numeroFactura}")
    @Operation(summary = "Consultar factura por número de radicado", description = "Busca una factura por su código fiscal único (ej. FAC-2026-0001).")
    public ResponseEntity<ApiResponseDto<FacturaResponseDto>> obtenerPorNumero(@PathVariable String numeroFactura) {
        Factura factura = pagoApplicationService.obtenerFacturaPorNumero(numeroFactura);
        return ResponseEntity.ok(ApiResponseDto.ok(PagoDtoMapper.toFacturaResponseDto(factura)));
    }

    @GetMapping("/pago/{idPago}")
    @Operation(summary = "Consultar factura por ID de pago", description = "Obtiene el comprobante de factura generado para un pago específico.")
    public ResponseEntity<ApiResponseDto<FacturaResponseDto>> obtenerPorPago(@PathVariable Long idPago) {
        Factura factura = pagoApplicationService.obtenerFacturaPorPago(idPago);
        return ResponseEntity.ok(ApiResponseDto.ok(PagoDtoMapper.toFacturaResponseDto(factura)));
    }

    @GetMapping("/cliente/{idCliente}")
    @Operation(summary = "Listar facturas de un cliente", description = "Obtiene el historial de todas las facturas emitidas a nombre de un cliente.")
    public ResponseEntity<ApiResponseDto<List<FacturaResponseDto>>> listarPorCliente(@PathVariable Long idCliente) {
        List<Factura> facturas = pagoApplicationService.listarFacturasPorCliente(idCliente);
        List<FacturaResponseDto> dtos = facturas.stream().map(PagoDtoMapper::toFacturaResponseDto).toList();
        return ResponseEntity.ok(ApiResponseDto.ok("Facturas del cliente obtenidas", dtos));
    }

    @GetMapping("/{numeroFactura}/descargar")
    @Operation(summary = "Descargar comprobante de factura", description = "Genera el comprobante oficial en formato descargable.")
    public ResponseEntity<String> descargarComprobante(@PathVariable String numeroFactura) {
        Factura factura = pagoApplicationService.obtenerFacturaPorNumero(numeroFactura);

        String reciboText = String.format("""
                ========================================================================
                          PLATAFORMA DE RENTA DE HERRAMIENTAS Y EQUIPOS
                                    FACTURA ELECTRONICA DE VENTA
                ========================================================================
                Factura No: %s
                Fecha Emision: %s
                ID Cliente: %d
                ID Pago Asociado: %d
                Estado: %s
                ------------------------------------------------------------------------
                RESUMEN FISCAL:
                Subtotal (Base Gravable): $ %,.2f COP
                IVA (19%%):               $ %,.2f COP
                ------------------------------------------------------------------------
                TOTAL PAGADO:            $ %,.2f COP
                ========================================================================
                Documento electronico valido como comprobante oficial de transaccion.
                ========================================================================
                """,
                factura.getNumeroFactura(),
                factura.getFechaEmision(),
                factura.getIdCliente(),
                factura.getIdPago(),
                factura.getEstado(),
                factura.getSubtotal(),
                factura.getIva(),
                factura.getTotal()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", factura.getNumeroFactura() + ".txt");

        return ResponseEntity.ok().headers(headers).body(reciboText);
    }
}
