package com.rentaherramientas.modules.inventario.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Tarifa de alquiler de una herramienta segun la modalidad de cobro.
 *
 * Se usa BigDecimal y no double porque se trata de dinero: con double aparecen
 * errores de precision al sumar y multiplicar valores decimales.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"idHerramienta", "tipoTarifa"})
public class TarifaAlquiler {

    private Long idTarifa;
    private Long idHerramienta;
    private TipoTarifa tipoTarifa;
    private BigDecimal valor;
    private boolean activa;

    public static TarifaAlquiler de(TipoTarifa tipoTarifa, BigDecimal valor) {
        if (tipoTarifa == null) {
            throw new BusinessRuleException("El tipo de tarifa es obligatorio");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El valor de la tarifa debe ser mayor que cero");
        }
        return TarifaAlquiler.builder()
                .tipoTarifa(tipoTarifa)
                .valor(valor)
                .activa(true)
                .build();
    }

    public void actualizarValor(BigDecimal nuevoValor) {
        if (nuevoValor == null || nuevoValor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El valor de la tarifa debe ser mayor que cero");
        }
        this.valor = nuevoValor;
    }

    public void desactivar() {
        this.activa = false;
    }

    public void activar() {
        this.activa = true;
    }

    /** Costo equivalente por dia, util para comparar modalidades en el catalogo. */
    public BigDecimal valorPorDia() {
        return valor.divide(BigDecimal.valueOf(tipoTarifa.getDiasCubiertos()), 2, RoundingMode.HALF_UP);
    }
}
