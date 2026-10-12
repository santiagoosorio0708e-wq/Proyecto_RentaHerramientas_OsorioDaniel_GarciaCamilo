package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity;

import com.rentaherramientas.modules.devoluciones.domain.model.TipoCargo;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cargos_adicionales")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CargoAdicionalJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cargo")
    private Long idCargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devolucion", nullable = false)
    private DevolucionJpaEntity devolucion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cargo", nullable = false, length = 30)
    private TipoCargo tipoCargo;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "pagado", nullable = false)
    private Boolean pagado;

    @Column(name = "fecha_cargo", nullable = false)
    private LocalDateTime fechaCargo;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;
}
