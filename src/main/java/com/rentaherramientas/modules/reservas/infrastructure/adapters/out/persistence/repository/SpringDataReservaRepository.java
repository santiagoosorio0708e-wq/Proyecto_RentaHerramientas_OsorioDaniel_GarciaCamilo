package com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity.ReservaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataReservaRepository extends JpaRepository<ReservaJpaEntity, Long> {

    Optional<ReservaJpaEntity> findByCodigoReserva(String codigoReserva);

    List<ReservaJpaEntity> findByIdClienteOrderByFechaSolicitudDesc(Long idCliente);

    @Query("SELECT COUNT(d) > 0 FROM DetalleReservaJpaEntity d " +
           "JOIN d.reserva r " +
           "WHERE d.idHerramienta = :idHerramienta " +
           "AND r.estado IN (com.rentaherramientas.modules.reservas.domain.model.EstadoReserva.APROBADA, com.rentaherramientas.modules.reservas.domain.model.EstadoReserva.EN_CURSO) " +
           "AND r.fechaFin >= :inicio AND r.fechaInicio <= :fin")
    boolean existsConflictoFechas(
            @Param("idHerramienta") Long idHerramienta,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );
}
