package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity;

import com.rentaherramientas.modules.devoluciones.domain.model.EstadoEquipoGeneral;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devoluciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devolucion")
    private Long idDevolucion;

    @Column(name = "id_reserva", nullable = false, unique = true)
    private Long idReserva;

    @Column(name = "fecha_devolucion_pactada", nullable = false)
    private LocalDate fechaDevolucionPactada;

    @Column(name = "fecha_devolucion_real", nullable = false)
    private LocalDate fechaDevolucionReal;

    @Column(name = "recibido_por", nullable = false)
    private Long recibidoPor;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_equipo_general", nullable = false, length = 30)
    private EstadoEquipoGeneral estadoEquipoGeneral;

    @Column(name = "dias_retraso", nullable = false)
    private Integer diasRetraso;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Builder.Default
    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReporteDanoJpaEntity> reportesDano = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CargoAdicionalJpaEntity> cargosAdicionales = new ArrayList<>();

    public void agregarReporteDano(ReporteDanoJpaEntity reporte) {
        if (this.reportesDano == null) {
            this.reportesDano = new ArrayList<>();
        }
        reporte.setDevolucion(this);
        this.reportesDano.add(reporte);
    }

    public void agregarCargoAdicional(CargoAdicionalJpaEntity cargo) {
        if (this.cargosAdicionales == null) {
            this.cargosAdicionales = new ArrayList<>();
        }
        cargo.setDevolucion(this);
        this.cargosAdicionales.add(cargo);
    }
}
