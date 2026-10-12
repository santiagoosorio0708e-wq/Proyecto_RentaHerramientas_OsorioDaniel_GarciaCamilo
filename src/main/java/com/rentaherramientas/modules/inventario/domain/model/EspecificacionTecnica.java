package com.rentaherramientas.modules.inventario.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Ficha tecnica de una herramienta, modelada como pares atributo/valor
 * (Potencia = 1500 W, Peso = 12 kg, Voltaje = 110 V...).
 *
 * Se guarda asi, y no como columnas fijas, porque cada tipo de maquina tiene
 * especificaciones distintas: un andamio no tiene voltaje y un taladro no tiene
 * altura maxima. Es una dependencia multivaluada, por eso va en su propia tabla.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"idHerramienta", "atributo"})
public class EspecificacionTecnica {

    private Long idEspecificacion;
    private Long idHerramienta;
    private String atributo;
    private String valor;
    private String unidadMedida;

    public static EspecificacionTecnica de(String atributo, String valor, String unidadMedida) {
        if (atributo == null || atributo.isBlank()) {
            throw new BusinessRuleException("El atributo de la especificacion tecnica es obligatorio");
        }
        if (valor == null || valor.isBlank()) {
            throw new BusinessRuleException("El valor de la especificacion tecnica es obligatorio");
        }
        return EspecificacionTecnica.builder()
                .atributo(atributo.trim())
                .valor(valor.trim())
                .unidadMedida(unidadMedida == null ? null : unidadMedida.trim())
                .build();
    }

    /** Representacion lista para mostrar en el frontend: "1500 W". */
    public String valorConUnidad() {
        return unidadMedida == null || unidadMedida.isBlank() ? valor : valor + " " + unidadMedida;
    }
}
