package com.rentaherramientas.modules.pagos.application.dto;

import com.rentaherramientas.modules.pagos.domain.model.DetalleFactura;
import com.rentaherramientas.modules.pagos.domain.model.Factura;
import com.rentaherramientas.modules.pagos.domain.model.MetodoPago;
import com.rentaherramientas.modules.pagos.domain.model.Pago;

import java.util.ArrayList;
import java.util.List;

public class PagoDtoMapper {

    public static PagoResponseDto toPagoResponseDto(Pago pago) {
        if (pago == null) return null;

        FacturaResponseDto facturaDto = null;
        if (pago.getFactura() != null) {
            facturaDto = toFacturaResponseDto(pago.getFactura());
        }

        return PagoResponseDto.builder()
                .idPago(pago.getIdPago())
                .numeroTransaccion(pago.getNumeroTransaccion())
                .idReserva(pago.getIdReserva())
                .idMetodoPago(pago.getIdMetodoPago())
                .monto(pago.getMonto())
                .tipoPago(pago.getTipoPago())
                .estadoPago(pago.getEstadoPago())
                .fechaPago(pago.getFechaPago())
                .referenciaPasarela(pago.getReferenciaPasarela())
                .factura(facturaDto)
                .build();
    }

    public static FacturaResponseDto toFacturaResponseDto(Factura factura) {
        if (factura == null) return null;

        List<DetalleFacturaDto> detallesDto = new ArrayList<>();
        if (factura.getDetalles() != null) {
            detallesDto = factura.getDetalles().stream()
                    .map(PagoDtoMapper::toDetalleFacturaDto)
                    .toList();
        }

        return FacturaResponseDto.builder()
                .idFactura(factura.getIdFactura())
                .numeroFactura(factura.getNumeroFactura())
                .idPago(factura.getIdPago())
                .idCliente(factura.getIdCliente())
                .fechaEmision(factura.getFechaEmision())
                .subtotal(factura.getSubtotal())
                .iva(factura.getIva())
                .total(factura.getTotal())
                .estado(factura.getEstado())
                .urlComprobantePdf(factura.getUrlComprobantePdf())
                .detalles(detallesDto)
                .build();
    }

    public static DetalleFacturaDto toDetalleFacturaDto(DetalleFactura detalle) {
        if (detalle == null) return null;
        return DetalleFacturaDto.builder()
                .idDetalleFactura(detalle.getIdDetalleFactura())
                .descripcionItem(detalle.getDescripcionItem())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotalLinea(detalle.getSubtotalLinea())
                .build();
    }

    public static MetodoPagoDto toMetodoPagoDto(MetodoPago metodo) {
        if (metodo == null) return null;
        return MetodoPagoDto.builder()
                .idMetodoPago(metodo.getIdMetodoPago())
                .nombre(metodo.getNombre())
                .descripcion(metodo.getDescripcion())
                .activo(metodo.isActivo())
                .build();
    }
}
