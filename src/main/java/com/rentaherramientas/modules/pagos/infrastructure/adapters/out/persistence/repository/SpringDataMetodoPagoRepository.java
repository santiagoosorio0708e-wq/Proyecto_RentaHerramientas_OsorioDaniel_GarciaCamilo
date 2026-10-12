package com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity.MetodoPagoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMetodoPagoRepository extends JpaRepository<MetodoPagoJpaEntity, Long> {
    List<MetodoPagoJpaEntity> findByActivoTrue();
}
