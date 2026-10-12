package com.rentaherramientas.modules.devoluciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Devolucion {
    private Long id;
    private Long idReserva;
    private LocalDate fechaDevolucionPactada;
    private LocalDate fechaDevolucionReal;
    private Long recibidoPor;
    private EstadoEquipoGeneral estadoEquipoGeneral;
    private Integer diasRetraso;
    private String observaciones;

    @Builder.Default
    private List<ReporteDano> reportesDano = new ArrayList<>();

    @Builder.Default
    private List<CargoAdicional> cargosAdicionales = new ArrayList<>();

    public int calcularDiasRetraso() {
        if (fechaDevolucionReal == null || fechaDevolucionPactada == null) {
            this.diasRetraso = 0;
            return 0;
        }
        if (fechaDevolucionReal.isAfter(fechaDevolucionPactada)) {
            this.diasRetraso = (int) ChronoUnit.DAYS.between(fechaDevolucionPactada, fechaDevolucionReal);
        } else {
            this.diasRetraso = 0;
        }
        return this.diasRetraso;
    }

    public void agregarReporteDano(ReporteDano reporte) {
        if (this.reportesDano == null) {
            this.reportesDano = new ArrayList<>();
        }
        this.reportesDano.add(reporte);
    }

    public void agregarCargoAdicional(CargoAdicional cargo) {
        if (this.cargosAdicionales == null) {
            this.cargosAdicionales = new ArrayList<>();
        }
        this.cargosAdicionales.add(cargo);
    }

    public BigDecimal calcularTotalCargosAdicionales() {
        if (cargosAdicionales == null || cargosAdicionales.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cargosAdicionales.stream()
                .map(CargoAdicional::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean tieneRetraso() {
        return diasRetraso != null && diasRetraso > 0;
    }

    public boolean tieneDanos() {
        return reportesDano != null && !reportesDano.isEmpty();
    }
}
