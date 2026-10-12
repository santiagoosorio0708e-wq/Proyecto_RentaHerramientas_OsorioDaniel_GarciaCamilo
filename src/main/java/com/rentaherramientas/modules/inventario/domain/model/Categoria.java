package com.rentaherramientas.modules.inventario.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Categoria del catalogo (Demolicion, Andamios, Compactacion, etc.). */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "nombre")
public class Categoria {

    private Long idCategoria;
    private String nombre;
    private String descripcion;

    public static Categoria crear(String nombre, String descripcion) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessRuleException("El nombre de la categoria es obligatorio");
        }
        if (nombre.trim().length() > 80) {
            throw new BusinessRuleException("El nombre de la categoria no puede superar 80 caracteres");
        }
        return Categoria.builder()
                .nombre(nombre.trim())
                .descripcion(descripcion == null ? null : descripcion.trim())
                .build();
    }

    public void renombrar(String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new BusinessRuleException("El nombre de la categoria es obligatorio");
        }
        this.nombre = nuevoNombre.trim();
    }

    public void cambiarDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? null : descripcion.trim();
    }
}
