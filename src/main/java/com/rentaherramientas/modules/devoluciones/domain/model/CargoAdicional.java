package com.rentaherramientas.modules.devoluciones.domain.model;

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
public class CargoAdicional {
    private Long id;
    private Long idDevolucion;
    private TipoCargo tipoCargo;
    private BigDecimal monto;
    private Boolean pagado;
    private LocalDateTime fechaCargo;
    private String descripcion;

    public void marcarComoPagado() {
        this.pagado = true;
    }
}
