package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity;

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

import java.time.LocalDateTime;

@Entity
@Table(name = "evidencias_dano")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenciaDanoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evidencia")
    private Long idEvidencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reporte_dano", nullable = false)
    private ReporteDanoJpaEntity reporteDano;

    @Column(name = "url_foto", nullable = false, length = 255)
    private String urlFoto;

    @Column(name = "descripcion_foto", length = 200)
    private String descripcionFoto;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;
}
