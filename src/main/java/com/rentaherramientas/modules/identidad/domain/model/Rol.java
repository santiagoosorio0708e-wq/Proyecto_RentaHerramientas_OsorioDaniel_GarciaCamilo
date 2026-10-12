package com.rentaherramientas.modules.identidad.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Rol del sistema. Agrupa permisos y es lo que finalmente decide a que
 * endpoints puede entrar un usuario.
 *
 * Por convencion de Spring Security el nombre siempre arranca con "ROLE_",
 * igual que los registros que ya existen en la tabla roles.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "nombre")
public class Rol {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String PROVEEDOR = "ROLE_PROVEEDOR";
    public static final String CLIENTE = "ROLE_CLIENTE";

    private Long idRol;
    private String nombre;
    private String descripcion;

    @Builder.Default
    private Set<Permiso> permisos = new HashSet<>();

    public static Rol de(String nombre, String descripcion) {
        if (nombre == null || !nombre.startsWith("ROLE_")) {
            throw new BusinessRuleException("El nombre del rol debe iniciar con el prefijo ROLE_");
        }
        return Rol.builder().nombre(nombre).descripcion(descripcion).build();
    }

    public void agregarPermiso(Permiso permiso) {
        if (permisos == null) {
            permisos = new HashSet<>();
        }
        permisos.add(permiso);
    }

    /** Nombre sin el prefijo ROLE_, util para mostrarlo en el frontend. */
    public String nombreCorto() {
        return nombre != null && nombre.startsWith("ROLE_") ? nombre.substring(5) : nombre;
    }
}
