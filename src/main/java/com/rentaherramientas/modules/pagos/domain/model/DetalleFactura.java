package com.rentaherramientas.modules.pagos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFactura {

    private Long idDetalleFactura;
    private String descripcionItem;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotalLinea;

    public void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            this.subtotalLinea = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
}
