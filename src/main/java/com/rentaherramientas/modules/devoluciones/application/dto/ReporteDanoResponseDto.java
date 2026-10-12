package com.rentaherramientas.modules.devoluciones.application.dto;

import com.rentaherramientas.modules.devoluciones.domain.model.ResponsableDano;
import com.rentaherramientas.modules.devoluciones.domain.model.TipoDano;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDanoResponseDto {
    private Long id;
    private Long idDevolucion;
    private Long idHerramienta;
    private TipoDano tipoDano;
    private String descripcionDano;
    private BigDecimal costoEstimadoReparacion;
    private ResponsableDano responsableAsume;
    private LocalDateTime fechaReporte;
    private List<EvidenciaDanoDto> evidencias;
}
