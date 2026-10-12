package com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity;

import com.rentaherramientas.modules.identidad.domain.model.valueobjects.TipoDocumento;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad JPA de la tabla usuarios.
 *
 * Es el "espejo tecnico" del agregado Usuario del dominio. Se mantienen
 * separados a proposito: esta clase conoce JPA y columnas, y la del dominio
 * conoce reglas de negocio.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "direccion", nullable = false, length = 180)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 30)
    private String numeroDocumento;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Relacion muchos a muchos resuelta con la tabla puente usuarios_roles.
     * Se usa EAGER porque los roles se necesitan siempre que se carga el usuario
     * (Spring Security los pide en cada peticion autenticada).
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    @Builder.Default
    private Set<RolJpaEntity> roles = new HashSet<>();

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PerfilProveedorJpaEntity perfilProveedor;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PerfilClienteJpaEntity perfilCliente;

    /** Mantiene sincronizados los dos lados de la relacion 1 a 1. */
    public void enlazarPerfilProveedor(PerfilProveedorJpaEntity perfil) {
        this.perfilProveedor = perfil;
        if (perfil != null) {
            perfil.setUsuario(this);
        }
    }

    public void enlazarPerfilCliente(PerfilClienteJpaEntity perfil) {
        this.perfilCliente = perfil;
        if (perfil != null) {
            perfil.setUsuario(this);
        }
    }
}
