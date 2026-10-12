package com.rentaherramientas.modules.reservas.application.dto;

import com.rentaherramientas.modules.reservas.domain.model.Reserva;

import java.util.ArrayList;
import java.util.List;

public class ReservaDtoMapper {

    public static ReservaResponseDto toResponseDto(Reserva domain) {
        if (domain == null) return null;

        List<DetalleReservaResponseDto> detallesDto = new ArrayList<>();
        if (domain.getDetalles() != null) {
            detallesDto = domain.getDetalles().stream()
                    .map(d -> DetalleReservaResponseDto.builder()
                            .idDetalleReserva(d.getIdDetalleReserva())
                            .idHerramienta(d.getIdHerramienta())
                            .nombreHerramienta(d.getNombreHerramienta())
                            .cantidad(d.getCantidad())
                            .precioUnitarioDia(d.getPrecioUnitarioDia())
                            .diasAlquiler(d.getDiasAlquiler())
                            .subtotal(d.getSubtotal())
                            .build())
                    .toList();
        }

        return ReservaResponseDto.builder()
                .idReserva(domain.getIdReserva())
                .codigoReserva(domain.getCodigoReserva())
                .idCliente(domain.getIdCliente())
                .fechaSolicitud(domain.getFechaSolicitud())
                .fechaInicio(domain.getRangoFechas().getFechaInicio())
                .fechaFin(domain.getRangoFechas().getFechaFin())
                .diasAlquiler(domain.getRangoFechas().getDiasAlquiler())
                .estado(domain.getEstado())
                .detalles(detallesDto)
                .montoTotalEstimado(domain.getMontoTotalEstimado())
                .depositoGarantia(domain.getDepositoGarantia())
                .notasCliente(domain.getNotasCliente())
                .motivoRechazo(domain.getMotivoRechazo())
                .build();
    }
}
