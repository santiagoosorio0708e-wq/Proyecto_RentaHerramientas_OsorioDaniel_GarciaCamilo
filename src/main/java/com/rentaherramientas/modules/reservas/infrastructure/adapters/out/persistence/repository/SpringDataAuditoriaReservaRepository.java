package com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity.AuditoriaReservaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataAuditoriaReservaRepository extends JpaRepository<AuditoriaReservaJpaEntity, Long> {
    List<AuditoriaReservaJpaEntity> findByIdReservaOrderByFechaCambioAsc(Long idReserva);
}
