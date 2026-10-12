package com.rentaherramientas.modules.reportes.application.dto;

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
public class ClienteFrecuenteDto {
    private Long idCliente;
    private String nombreCompleto;
    private String email;
    private Long totalReservas;
    private BigDecimal totalGastado;
}
