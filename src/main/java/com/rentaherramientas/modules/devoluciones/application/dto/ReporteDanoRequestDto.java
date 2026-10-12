package com.rentaherramientas.modules.devoluciones.application.dto;

import com.rentaherramientas.modules.devoluciones.domain.model.ResponsableDano;
import com.rentaherramientas.modules.devoluciones.domain.model.TipoDano;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDanoRequestDto {
    @NotNull(message = "El ID de la herramienta es obligatorio")
    private Long idHerramienta;

    @NotNull(message = "El tipo de daño es obligatorio")
    private TipoDano tipoDano;

    @NotBlank(message = "La descripción del daño es obligatoria")
    private String descripcionDano;

    @NotNull(message = "El costo estimado de reparación es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo no puede ser negativo")
    private BigDecimal costoEstimadoReparacion;

    @NotNull(message = "El responsable que asume es obligatorio")
    private ResponsableDano responsableAsume;

    @Builder.Default
    private List<EvidenciaDanoDto> evidencias = new ArrayList<>();
}
