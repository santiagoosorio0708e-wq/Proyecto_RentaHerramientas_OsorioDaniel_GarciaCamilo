package com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity;

import com.rentaherramientas.modules.inventario.domain.model.TipoTarifa;
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

/** Entidad JPA de la tabla tarifas_herramienta (tarifas por modalidad, 4FN). */
@Entity
@Table(name = "tarifas_herramienta")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarifaHerramientaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarifa")
    private Long idTarifa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_herramienta", nullable = false)
    private HerramientaJpaEntity herramienta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tarifa", nullable = false, length = 20)
    private TipoTarifa tipoTarifa;

    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "activa", nullable = false)
    private boolean activa;
}
