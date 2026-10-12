package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Criterios de busqueda del catalogo publico.
 *
 * Todos los campos son opcionales: si llegan en null simplemente no se aplica
 * ese filtro. Asi un mismo endpoint sirve para "ver todo" y para busquedas finas.
 */
@Builder
@Schema(description = "Filtros opcionales para la busqueda de herramientas en el catalogo")
public record FiltroCatalogoQuery(

        @Schema(description = "Texto a buscar en el nombre o la marca", example = "martillo")
        String texto,

        @Schema(description = "Identificador de la categoria", example = "1")
        Long idCategoria,

        @Schema(description = "Precio diario minimo", example = "50000")
        BigDecimal precioMinimo,

        @Schema(description = "Precio diario maximo", example = "300000")
        BigDecimal precioMaximo,

        @Schema(description = "Ciudad de operacion del proveedor", example = "Bucaramanga")
        String ubicacion,

        @Schema(description = "Si es true solo devuelve herramientas DISPONIBLES", example = "true")
        Boolean soloDisponibles
) {
    /** Filtro vacio: devuelve todo el catalogo. */
    public static FiltroCatalogoQuery vacio() {
        return FiltroCatalogoQuery.builder().soloDisponibles(Boolean.TRUE).build();
    }
}
