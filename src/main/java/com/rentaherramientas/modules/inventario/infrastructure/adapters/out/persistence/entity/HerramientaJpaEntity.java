package com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity;

import com.rentaherramientas.modules.inventario.domain.model.EstadoHerramienta;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Entidad JPA de la tabla herramientas y de todas sus colecciones asociadas. */
@Entity
@Table(name = "herramientas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HerramientaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_herramienta")
    private Long idHerramienta;

    @Column(name = "id_proveedor", nullable = false)
    private Long idProveedor;

    /** Categoria principal: la columna id_categoria que ya existia en la tabla. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaJpaEntity categoriaPrincipal;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "marca", nullable = false, length = 60)
    private String marca;

    @Column(name = "modelo", nullable = false, length = 60)
    private String modelo;

    @Column(name = "numero_serie", nullable = false, unique = true, length = 80)
    private String numeroSerie;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_operativo", nullable = false, length = 30)
    private EstadoHerramienta estadoOperativo;

    @Column(name = "tarifa_diaria", nullable = false, precision = 12, scale = 2)
    private BigDecimal tarifaDiaria;

    @Column(name = "valor_reposicion", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorReposicion;

    @Column(name = "ubicacion_bodega", length = 120)
    private String ubicacionBodega;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    /** 4FN: categorias multiples independientes de las fotos y de las tarifas. */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "herramientas_categorias",
            joinColumns = @JoinColumn(name = "id_herramienta"),
            inverseJoinColumns = @JoinColumn(name = "id_categoria")
    )
    @Builder.Default
    private Set<CategoriaJpaEntity> categorias = new HashSet<>();

    /** 4FN: galeria de fotos en tabla aparte. */
    @OneToMany(mappedBy = "herramienta", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ImagenHerramientaJpaEntity> imagenes = new ArrayList<>();

    /** 4FN: ficha tecnica en tabla aparte. */
    @OneToMany(mappedBy = "herramienta", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EspecificacionTecnicaJpaEntity> especificaciones = new ArrayList<>();

    /** 4FN: tarifas por modalidad en tabla aparte. */
    @OneToMany(mappedBy = "herramienta", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TarifaHerramientaJpaEntity> tarifas = new ArrayList<>();

    // Metodos de conveniencia para mantener sincronizados los dos lados de
    // cada relacion. Sin esto Hibernate puede dejar el campo id_herramienta
    // en null al guardar los hijos.

    public void limpiarImagenes() {
        if (this.imagenes != null) {
            this.imagenes.clear();
        }
    }

    public void agregarImagen(ImagenHerramientaJpaEntity imagen) {
        if (this.imagenes == null) {
            this.imagenes = new ArrayList<>();
        }
        imagen.setHerramienta(this);
        this.imagenes.add(imagen);
    }

    public void limpiarEspecificaciones() {
        if (this.especificaciones != null) {
            this.especificaciones.clear();
        }
    }

    public void agregarEspecificacion(EspecificacionTecnicaJpaEntity especificacion) {
        if (this.especificaciones == null) {
            this.especificaciones = new ArrayList<>();
        }
        especificacion.setHerramienta(this);
        this.especificaciones.add(especificacion);
    }

    public void limpiarTarifas() {
        if (this.tarifas != null) {
            this.tarifas.clear();
        }
    }

    public void agregarTarifa(TarifaHerramientaJpaEntity tarifa) {
        if (this.tarifas == null) {
            this.tarifas = new ArrayList<>();
        }
        tarifa.setHerramienta(this);
        this.tarifas.add(tarifa);
    }
}
