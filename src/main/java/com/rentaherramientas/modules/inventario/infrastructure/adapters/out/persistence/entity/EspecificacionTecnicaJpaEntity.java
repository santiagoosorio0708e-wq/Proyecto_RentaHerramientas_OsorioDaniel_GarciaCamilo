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

/** Entidad JPA de la tabla especificaciones_tecnicas (ficha tecnica, 4FN). */
@Entity
@Table(name = "especificaciones_tecnicas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspecificacionTecnicaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especificacion")
    private Long idEspecificacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_herramienta", nullable = false)
    private HerramientaJpaEntity herramienta;

    @Column(name = "atributo", nullable = false, length = 80)
    private String atributo;

    @Column(name = "valor", nullable = false, length = 120)
    private String valor;

    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida;
}
