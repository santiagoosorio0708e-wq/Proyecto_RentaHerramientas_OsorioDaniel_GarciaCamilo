package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity;

import com.rentaherramientas.modules.devoluciones.domain.model.ResponsableDano;
import com.rentaherramientas.modules.devoluciones.domain.model.TipoDano;
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
import java.util.List;

@Entity
@Table(name = "reportes_dano")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDanoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte_dano")
    private Long idReporteDano;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devolucion", nullable = false)
    private DevolucionJpaEntity devolucion;

    @Column(name = "id_herramienta", nullable = false)
    private Long idHerramienta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dano", nullable = false, length = 30)
    private TipoDano tipoDano;

    @Column(name = "descripcion_dano", nullable = false, columnDefinition = "TEXT")
    private String descripcionDano;

    @Column(name = "costo_estimado_reparacion", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoEstimadoReparacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "responsable_asume", nullable = false, length = 30)
    private ResponsableDano responsableAsume;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte;

    @Builder.Default
    @OneToMany(mappedBy = "reporteDano", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvidenciaDanoJpaEntity> evidencias = new ArrayList<>();

    public void agregarEvidencia(EvidenciaDanoJpaEntity evidencia) {
        if (this.evidencias == null) {
            this.evidencias = new ArrayList<>();
        }
        evidencia.setReporteDano(this);
        this.evidencias.add(evidencia);
    }
}
