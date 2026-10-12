package com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.mapper;

import com.rentaherramientas.modules.reservas.domain.model.DetalleReserva;
import com.rentaherramientas.modules.reservas.domain.model.RangoFechas;
import com.rentaherramientas.modules.reservas.domain.model.Reserva;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity.DetalleReservaJpaEntity;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity.ReservaJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReservaPersistenceMapper {

    public Reserva toDomain(ReservaJpaEntity entity) {
        if (entity == null) return null;

        List<DetalleReserva> detalles = new ArrayList<>();
        if (entity.getDetalles() != null) {
            for (DetalleReservaJpaEntity d : entity.getDetalles()) {
                detalles.add(DetalleReserva.builder()
                        .idDetalleReserva(d.getIdDetalleReserva())
                        .idHerramienta(d.getIdHerramienta())
                        .cantidad(d.getCantidad())
                        .precioUnitarioDia(d.getPrecioUnitarioDia())
                        .diasAlquiler(d.getDiasAlquiler())
                        .subtotal(d.getSubtotal())
                        .build());
            }
        }

        RangoFechas rango = new RangoFechas(entity.getFechaInicio(), entity.getFechaFin());

        return Reserva.builder()
                .idReserva(entity.getIdReserva())
                .codigoReserva(entity.getCodigoReserva())
                .idCliente(entity.getIdCliente())
                .fechaSolicitud(entity.getFechaSolicitud())
                .rangoFechas(rango)
                .estado(entity.getEstado())
                .detalles(detalles)
                .montoTotalEstimado(entity.getMontoTotalEstimado())
                .depositoGarantia(entity.getDepositoGarantia())
                .notasCliente(entity.getNotasCliente())
                .motivoRechazo(entity.getMotivoRechazo())
                .build();
    }

    public ReservaJpaEntity toJpaEntity(Reserva domain) {
        if (domain == null) return null;

        ReservaJpaEntity entity = ReservaJpaEntity.builder()
                .idReserva(domain.getIdReserva())
                .codigoReserva(domain.getCodigoReserva())
                .idCliente(domain.getIdCliente())
                .fechaSolicitud(domain.getFechaSolicitud())
                .fechaInicio(domain.getRangoFechas().getFechaInicio())
                .fechaFin(domain.getRangoFechas().getFechaFin())
                .estado(domain.getEstado())
                .montoTotalEstimado(domain.getMontoTotalEstimado())
                .depositoGarantia(domain.getDepositoGarantia())
                .notasCliente(domain.getNotasCliente())
                .motivoRechazo(domain.getMotivoRechazo())
                .detalles(new ArrayList<>())
                .build();

        if (domain.getDetalles() != null) {
            for (DetalleReserva d : domain.getDetalles()) {
                DetalleReservaJpaEntity dEntity = DetalleReservaJpaEntity.builder()
                        .idDetalleReserva(d.getIdDetalleReserva())
                        .reserva(entity)
                        .idHerramienta(d.getIdHerramienta())
                        .cantidad(d.getCantidad())
                        .precioUnitarioDia(d.getPrecioUnitarioDia())
                        .diasAlquiler(d.getDiasAlquiler())
                        .subtotal(d.getSubtotal())
                        .build();
                entity.getDetalles().add(dEntity);
            }
        }

        return entity;
    }
}
