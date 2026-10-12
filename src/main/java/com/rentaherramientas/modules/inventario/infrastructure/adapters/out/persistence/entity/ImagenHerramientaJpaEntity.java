package com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Entidad JPA de la tabla herramientas_imagenes (galeria, 4FN). */
@Entity
@Table(name = "herramientas_imagenes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagenHerramientaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long idImagen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_herramienta", nullable = false)
    private HerramientaJpaEntity herramienta;

    @Column(name = "url_foto", nullable = false, length = 255)
    private String urlFoto;

    @Column(name = "descripcion_foto", length = 200)
    private String descripcion;

    @Column(name = "es_principal", nullable = false)
    private boolean principal;

    @Column(name = "orden_visualizacion")
    private Integer orden;
}
