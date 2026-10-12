package com.rentaherramientas.modules.identidad.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Documento;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Email;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Raiz del agregado de Identidad.
 *
 * Es una clase de dominio pura: no tiene anotaciones de JPA ni de Spring. Eso es
 * lo que exige la arquitectura hexagonal, el dominio no debe saber si los datos
 * terminan en MySQL, en un archivo o en memoria. La traduccion hacia la tabla la
 * hace UsuarioPersistenceMapper en la capa de infraestructura.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long idUsuario;
    private String nombreCompleto;
    private Email email;
    private Password password;
    private String telefono;
    private String direccion;
    private Documento documento;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    @Builder.Default
    private Set<Rol> roles = new HashSet<>();

    private PerfilProveedor perfilProveedor;
    private PerfilCliente perfilCliente;

    /**
     * Fabrica de registro. Centraliza aqui las invariantes para que sea imposible
     * construir un usuario incompleto desde los casos de uso.
     */
    public static Usuario registrar(String nombreCompleto,
                                    Email email,
                                    Password password,
                                    String telefono,
                                    String direccion,
                                    Documento documento) {

        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new BusinessRuleException("El nombre completo es obligatorio");
        }
        if (nombreCompleto.trim().length() < 3) {
            throw new BusinessRuleException("El nombre completo debe tener al menos 3 caracteres");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new BusinessRuleException("El telefono es obligatorio");
        }
        if (!telefono.trim().matches("^[0-9+\\s-]{7,20}$")) {
            throw new BusinessRuleException("El telefono tiene un formato invalido");
        }
        if (direccion == null || direccion.isBlank()) {
            throw new BusinessRuleException("La direccion es obligatoria");
        }

        return Usuario.builder()
                .nombreCompleto(nombreCompleto.trim())
                .email(email)
                .password(password)
                .telefono(telefono.trim())
                .direccion(direccion.trim())
                .documento(documento)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .roles(new HashSet<>())
                .build();
    }

    public void asignarRol(Rol rol) {
        if (rol == null) {
            throw new BusinessRuleException("El rol a asignar no puede ser nulo");
        }
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(rol);
    }

    public boolean tieneRol(String nombreRol) {
        return roles != null && roles.stream()
                .anyMatch(r -> r.getNombre().equalsIgnoreCase(nombreRol));
    }

    public boolean esProveedor() {
        return tieneRol(Rol.PROVEEDOR);
    }

    public boolean esCliente() {
        return tieneRol(Rol.CLIENTE);
    }

    public boolean esAdministrador() {
        return tieneRol(Rol.ADMIN);
    }

    /** Bloqueo de cuenta usado por el panel del administrador. */
    public void bloquear() {
        if (esAdministrador()) {
            throw new BusinessRuleException("No se puede bloquear una cuenta con rol administrador");
        }
        if (!this.activo) {
            throw new BusinessRuleException("La cuenta del usuario ya se encuentra bloqueada");
        }
        this.activo = false;
    }

    public void activar() {
        if (this.activo) {
            throw new BusinessRuleException("La cuenta del usuario ya se encuentra activa");
        }
        this.activo = true;
    }

    public void cambiarPassword(Password nuevaPassword) {
        if (nuevaPassword == null) {
            throw new BusinessRuleException("La nueva contrasena es obligatoria");
        }
        this.password = nuevaPassword;
    }

    public void actualizarDatosContacto(String nombreCompleto, String telefono, String direccion) {
        if (nombreCompleto != null && !nombreCompleto.isBlank()) {
            this.nombreCompleto = nombreCompleto.trim();
        }
        if (telefono != null && !telefono.isBlank()) {
            this.telefono = telefono.trim();
        }
        if (direccion != null && !direccion.isBlank()) {
            this.direccion = direccion.trim();
        }
    }

    public void asignarPerfilProveedor(PerfilProveedor perfil) {
        if (!esProveedor()) {
            throw new BusinessRuleException("Solo un usuario con rol PROVEEDOR puede tener perfil de proveedor");
        }
        this.perfilProveedor = perfil;
    }

    public void asignarPerfilCliente(PerfilCliente perfil) {
        if (!esCliente()) {
            throw new BusinessRuleException("Solo un usuario con rol CLIENTE puede tener perfil de cliente");
        }
        this.perfilCliente = perfil;
    }

    /** Nombres de rol planos, que es lo que necesita Spring Security y el JWT. */
    public Set<String> nombresDeRoles() {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream().map(Rol::getNombre).collect(Collectors.toSet());
    }
}
