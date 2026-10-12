package com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/** Entidad JPA de la tabla perfiles_proveedor (relacion 1 a 1 con usuarios). */
@Entity
@Table(name = "perfiles_proveedor")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilProveedorJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil_proveedor")
    private Long idPerfilProveedor;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private UsuarioJpaEntity usuario;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 120)
    private String nombreComercial;

    @Column(name = "ciudad_operacion", nullable = false, length = 80)
    private String ciudadOperacion;

    @Column(name = "sitio_web", length = 150)
    private String sitioWeb;

    @Column(name = "fecha_vinculacion", nullable = false)
    private LocalDate fechaVinculacion;

    @Column(name = "verificado", nullable = false)
    private boolean verificado;

    @Column(name = "calificacion_promedio")
    private Integer calificacionPromedio;
}
