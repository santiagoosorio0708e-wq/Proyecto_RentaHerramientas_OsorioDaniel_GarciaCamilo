package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.DevolucionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataDevolucionRepository extends JpaRepository<DevolucionJpaEntity, Long> {
    Optional<DevolucionJpaEntity> findByIdReserva(Long idReserva);
    List<DevolucionJpaEntity> findAllByOrderByFechaDevolucionRealDesc();
}
