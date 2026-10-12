package com.rentaherramientas.modules.identidad.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Permiso atomico del sistema (por ejemplo HERRAMIENTA_CREAR).
 *
 * Los permisos se agrupan dentro de un {@link Rol}. Spring Security los expone
 * como authorities, lo que permite usar @PreAuthorize("hasAuthority('...')")
 * cuando se necesita un control mas fino que el del rol completo.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "codigo")
public class Permiso {

    private Long idPermiso;
    private String codigo;
    private String descripcion;

    public static Permiso de(String codigo, String descripcion) {
        return Permiso.builder().codigo(codigo).descripcion(descripcion).build();
    }
}
