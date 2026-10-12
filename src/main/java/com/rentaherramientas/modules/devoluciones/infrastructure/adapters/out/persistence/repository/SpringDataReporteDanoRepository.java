package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.ReporteDanoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataReporteDanoRepository extends JpaRepository<ReporteDanoJpaEntity, Long> {
    List<ReporteDanoJpaEntity> findByDevolucion_IdDevolucion(Long idDevolucion);
}
