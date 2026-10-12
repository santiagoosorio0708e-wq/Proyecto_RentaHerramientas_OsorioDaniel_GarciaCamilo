package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/** Datos para dar de alta una herramienta en el inventario del proveedor. */
@Schema(description = "Datos necesarios para registrar una herramienta en el inventario")
public record CrearHerramientaCommand(

        @Schema(description = "Categoria principal de la herramienta", example = "1")
        @NotNull(message = "La categoria principal es obligatoria")
        Long idCategoriaPrincipal,

        @Schema(description = "Nombre comercial", example = "Martillo demoledor electrico")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String nombre,

        @Schema(description = "Descripcion detallada", example = "Martillo demoledor de 1500W con maletin y cinceles")
        @NotBlank(message = "La descripcion es obligatoria")
        String descripcion,

        @Schema(description = "Marca", example = "Bosch")
        @NotBlank(message = "La marca es obligatoria")
        @Size(max = 60, message = "La marca no puede superar 60 caracteres")
        String marca,

        @Schema(description = "Modelo", example = "GSH 11E")
        @NotBlank(message = "El modelo es obligatorio")
        @Size(max = 60, message = "El modelo no puede superar 60 caracteres")
        String modelo,

        @Schema(description = "Numero de serie, unico en el sistema", example = "SN-BSH-0099")
        @NotBlank(message = "El numero de serie es obligatorio")
        @Size(max = 80, message = "El numero de serie no puede superar 80 caracteres")
        String numeroSerie,

        @Schema(description = "Tarifa por dia en pesos", example = "120000")
        @NotNull(message = "La tarifa diaria es obligatoria")
        @Positive(message = "La tarifa diaria debe ser mayor que cero")
        BigDecimal tarifaDiaria,

        @Schema(description = "Valor de reposicion en caso de perdida", example = "3500000")
        @NotNull(message = "El valor de reposicion es obligatorio")
        @Positive(message = "El valor de reposicion debe ser mayor que cero")
        BigDecimal valorReposicion,

        @Schema(description = "Ubicacion dentro de la bodega", example = "Pasillo B - Estante 4")
        @Size(max = 120, message = "La ubicacion no puede superar 120 caracteres")
        String ubicacionBodega,

        @Schema(description = "Categorias adicionales (4FN)", example = "[2, 5]")
        List<Long> categoriasAdicionales,

        @Schema(description = "Galeria de fotos de la herramienta")
        @Valid
        List<ImagenHerramientaRequest> imagenes,

        @Schema(description = "Ficha tecnica de la herramienta")
        @Valid
        List<EspecificacionTecnicaRequest> especificaciones,

        @Schema(description = "Tarifas adicionales (fin de semana, semanal, mensual)")
        @Valid
        List<TarifaAlquilerRequest> tarifasAdicionales
) {
}
