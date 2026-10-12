package com.rentaherramientas.modules.devoluciones.application.dto;

import com.rentaherramientas.modules.devoluciones.domain.model.TipoCargo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CargoAdicionalDto {
    private Long id;
    private Long idDevolucion;
    private TipoCargo tipoCargo;
    private BigDecimal monto;
    private Boolean pagado;
    private LocalDateTime fechaCargo;
    private String descripcion;
}
