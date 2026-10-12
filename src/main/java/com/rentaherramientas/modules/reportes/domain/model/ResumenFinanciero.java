package com.rentaherramientas.modules.reportes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenFinanciero {
    private BigDecimal totalRecaudado;
    private BigDecimal totalSubtotal;
    private BigDecimal totalIva;
    private BigDecimal totalCargosMora;
    private BigDecimal totalCargosDano;
    private Long totalFacturasEmitidas;
    private Long totalPagosExitosos;
}
