package com.rentaherramientas.modules.devoluciones.application.dto;

import com.rentaherramientas.modules.devoluciones.domain.model.EstadoEquipoGeneral;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionResponseDto {
    private Long id;
    private Long idReserva;
    private LocalDate fechaDevolucionPactada;
    private LocalDate fechaDevolucionReal;
    private Long recibidoPor;
    private EstadoEquipoGeneral estadoEquipoGeneral;
    private Integer diasRetraso;
    private String observaciones;
    private BigDecimal totalCargosAdicionales;
    private List<ReporteDanoResponseDto> reportesDano;
    private List<CargoAdicionalDto> cargosAdicionales;
}
