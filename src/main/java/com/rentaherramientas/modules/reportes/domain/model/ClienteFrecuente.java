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
public class ClienteFrecuente {
    private Long idCliente;
    private String nombreCompleto;
    private String email;
    private Long totalReservas;
    private BigDecimal totalGastado;
}
