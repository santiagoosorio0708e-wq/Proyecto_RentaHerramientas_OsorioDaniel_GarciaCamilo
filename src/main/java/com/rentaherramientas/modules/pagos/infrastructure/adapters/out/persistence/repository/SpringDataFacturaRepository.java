package com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity.FacturaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataFacturaRepository extends JpaRepository<FacturaJpaEntity, Long> {
    Optional<FacturaJpaEntity> findByNumeroFactura(String numeroFactura);
    Optional<FacturaJpaEntity> findByIdPago(Long idPago);
    List<FacturaJpaEntity> findByIdClienteOrderByFechaEmisionDesc(Long idCliente);
}
