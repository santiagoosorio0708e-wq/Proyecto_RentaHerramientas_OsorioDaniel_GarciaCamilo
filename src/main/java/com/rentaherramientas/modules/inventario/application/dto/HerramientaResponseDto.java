package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Vista completa de una herramienta, con galeria, ficha tecnica y tarifas. */
@Builder
@Schema(description = "Detalle completo de una herramienta del inventario")
public record HerramientaResponseDto(

        @Schema(description = "Identificador", example = "1")
        Long idHerramienta,

        @Schema(description = "Identificador del proveedor dueno", example = "4")
        Long idProveedor,

        @Schema(description = "Nombre comercial", example = "Martillo demoledor electrico")
        String nombre,

        @Schema(description = "Descripcion detallada")
        String descripcion,

        @Schema(description = "Marca", example = "Bosch")
        String marca,

        @Schema(description = "Modelo", example = "GSH 11E")
        String modelo,

        @Schema(description = "Numero de serie", example = "SN-BSH-0099")
        String numeroSerie,

        @Schema(description = "Estado operativo", example = "DISPONIBLE")
        String estadoOperativo,

        @Schema(description = "Indica si se puede alquilar ahora", example = "true")
        boolean disponible,

        @Schema(description = "Tarifa diaria", example = "120000.00")
        BigDecimal tarifaDiaria,

        @Schema(description = "Valor de reposicion", example = "3500000.00")
        BigDecimal valorReposicion,

        @Schema(description = "Ubicacion en bodega", example = "Pasillo B - Estante 4")
        String ubicacionBodega,

        @Schema(description = "Fecha de registro en el inventario")
        LocalDateTime fechaRegistro,

        @Schema(description = "Categoria principal")
        CategoriaResponseDto categoriaPrincipal,

        @Schema(description = "Todas las categorias asociadas (4FN)")
        List<CategoriaResponseDto> categorias,

        @Schema(description = "Galeria de fotos (4FN)")
        List<ImagenHerramientaDto> imagenes,

        @Schema(description = "Ficha tecnica (4FN)")
        List<EspecificacionTecnicaDto> especificaciones,

        @Schema(description = "Tarifas por modalidad (4FN)")
        List<TarifaAlquilerDto> tarifas
) {
}
