package com.rentaherramientas.modules.reservas.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@ToString
@EqualsAndHashCode
public class RangoFechas {

    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;

    public RangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessRuleException("Las fechas de inicio y fin de la reserva son obligatorias");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new BusinessRuleException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public long getDiasAlquiler() {
        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        return dias == 0 ? 1 : dias;
    }

    public boolean seSolapaCon(RangoFechas otro) {
        return !this.fechaFin.isBefore(otro.fechaInicio) && !otro.fechaFin.isBefore(this.fechaInicio);
    }
}
