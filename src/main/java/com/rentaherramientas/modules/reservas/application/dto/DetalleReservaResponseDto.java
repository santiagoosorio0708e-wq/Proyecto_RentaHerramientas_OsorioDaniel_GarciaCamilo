package com.rentaherramientas.modules.reservas.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleReservaResponseDto {

    private Long idDetalleReserva;
    private Long idHerramienta;
    private String nombreHerramienta;
    private Integer cantidad;
    private BigDecimal precioUnitarioDia;
    private Integer diasAlquiler;
    private BigDecimal subtotal;
}
