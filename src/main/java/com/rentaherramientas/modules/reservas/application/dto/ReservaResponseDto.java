package com.rentaherramientas.modules.reservas.application.dto;

import com.rentaherramientas.modules.reservas.domain.model.EstadoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDto {

    private Long idReserva;
    private String codigoReserva;
    private Long idCliente;
    private LocalDateTime fechaSolicitud;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private long diasAlquiler;
    private EstadoReserva estado;
    private List<DetalleReservaResponseDto> detalles;
    private BigDecimal montoTotalEstimado;
    private BigDecimal depositoGarantia;
    private String notasCliente;
    private String motivoRechazo;
}
