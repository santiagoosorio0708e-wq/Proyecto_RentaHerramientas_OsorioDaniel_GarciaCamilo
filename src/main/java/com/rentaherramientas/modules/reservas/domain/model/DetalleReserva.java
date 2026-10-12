package com.rentaherramientas.modules.reservas.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleReserva {

    private Long idDetalleReserva;
    private Long idHerramienta;
    private String nombreHerramienta;
    private Integer cantidad;
    private BigDecimal precioUnitarioDia;
    private Integer diasAlquiler;
    private BigDecimal subtotal;

    public void calcularSubtotal() {
        if (precioUnitarioDia != null && cantidad != null && diasAlquiler != null) {
            this.subtotal = precioUnitarioDia
                    .multiply(BigDecimal.valueOf(cantidad))
                    .multiply(BigDecimal.valueOf(diasAlquiler));
        }
    }
}
