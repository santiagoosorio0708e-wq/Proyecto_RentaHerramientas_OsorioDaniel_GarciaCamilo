package com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.mapper;

import com.rentaherramientas.modules.pagos.domain.model.DetalleFactura;
import com.rentaherramientas.modules.pagos.domain.model.Factura;
import com.rentaherramientas.modules.pagos.domain.model.MetodoPago;
import com.rentaherramientas.modules.pagos.domain.model.Pago;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity.DetalleFacturaJpaEntity;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity.FacturaJpaEntity;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity.MetodoPagoJpaEntity;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity.PagoJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PagoPersistenceMapper {

    public Pago toDomain(PagoJpaEntity entity) {
        if (entity == null) return null;
        return Pago.builder()
                .idPago(entity.getIdPago())
                .numeroTransaccion(entity.getNumeroTransaccion())
                .idReserva(entity.getIdReserva())
                .idMetodoPago(entity.getIdMetodoPago())
                .monto(entity.getMonto())
                .tipoPago(entity.getTipoPago())
                .estadoPago(entity.getEstadoPago())
                .fechaPago(entity.getFechaPago())
                .referenciaPasarela(entity.getReferenciaPasarela())
                .build();
    }

    public PagoJpaEntity toJpaEntity(Pago domain) {
        if (domain == null) return null;
        return PagoJpaEntity.builder()
                .idPago(domain.getIdPago())
                .numeroTransaccion(domain.getNumeroTransaccion())
                .idReserva(domain.getIdReserva())
                .idMetodoPago(domain.getIdMetodoPago())
                .monto(domain.getMonto())
                .tipoPago(domain.getTipoPago())
                .estadoPago(domain.getEstadoPago())
                .fechaPago(domain.getFechaPago())
                .referenciaPasarela(domain.getReferenciaPasarela())
                .build();
    }

    public Factura toDomain(FacturaJpaEntity entity) {
        if (entity == null) return null;

        List<DetalleFactura> detalles = new ArrayList<>();
        if (entity.getDetalles() != null) {
            detalles = entity.getDetalles().stream()
                    .map(d -> DetalleFactura.builder()
                            .idDetalleFactura(d.getIdDetalleFactura())
                            .descripcionItem(d.getDescripcionItem())
                            .cantidad(d.getCantidad())
                            .precioUnitario(d.getPrecioUnitario())
                            .subtotalLinea(d.getSubtotalLinea())
                            .build())
                    .toList();
        }

        return Factura.builder()
                .idFactura(entity.getIdFactura())
                .numeroFactura(entity.getNumeroFactura())
                .idPago(entity.getIdPago())
                .idCliente(entity.getIdCliente())
                .fechaEmision(entity.getFechaEmision())
                .subtotal(entity.getSubtotal())
                .iva(entity.getIva())
                .total(entity.getTotal())
                .estado(entity.getEstado())
                .urlComprobantePdf(entity.getUrlComprobantePdf())
                .detalles(detalles)
                .build();
    }

    public FacturaJpaEntity toJpaEntity(Factura domain) {
        if (domain == null) return null;

        FacturaJpaEntity entity = FacturaJpaEntity.builder()
                .idFactura(domain.getIdFactura())
                .numeroFactura(domain.getNumeroFactura())
                .idPago(domain.getIdPago())
                .idCliente(domain.getIdCliente())
                .fechaEmision(domain.getFechaEmision())
                .subtotal(domain.getSubtotal())
                .iva(domain.getIva())
                .total(domain.getTotal())
                .estado(domain.getEstado())
                .urlComprobantePdf(domain.getUrlComprobantePdf())
                .detalles(new ArrayList<>())
                .build();

        if (domain.getDetalles() != null) {
            for (DetalleFactura d : domain.getDetalles()) {
                DetalleFacturaJpaEntity dEntity = DetalleFacturaJpaEntity.builder()
                        .idDetalleFactura(d.getIdDetalleFactura())
                        .factura(entity)
                        .descripcionItem(d.getDescripcionItem())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotalLinea(d.getSubtotalLinea())
                        .build();
                entity.getDetalles().add(dEntity);
            }
        }
        return entity;
    }

    public MetodoPago toDomain(MetodoPagoJpaEntity entity) {
        if (entity == null) return null;
        return MetodoPago.builder()
                .idMetodoPago(entity.getIdMetodoPago())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .activo(entity.isActivo())
                .build();
    }
}
