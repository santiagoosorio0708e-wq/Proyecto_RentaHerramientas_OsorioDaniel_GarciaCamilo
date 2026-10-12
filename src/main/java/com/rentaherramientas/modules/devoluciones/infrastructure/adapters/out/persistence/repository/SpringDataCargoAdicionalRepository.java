package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.CargoAdicionalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataCargoAdicionalRepository extends JpaRepository<CargoAdicionalJpaEntity, Long> {
    List<CargoAdicionalJpaEntity> findByDevolucion_IdDevolucion(Long idDevolucion);
}
