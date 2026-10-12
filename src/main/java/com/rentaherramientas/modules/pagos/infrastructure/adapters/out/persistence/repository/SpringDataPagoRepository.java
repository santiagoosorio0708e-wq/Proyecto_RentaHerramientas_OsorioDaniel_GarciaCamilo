package com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity.PagoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataPagoRepository extends JpaRepository<PagoJpaEntity, Long> {
    Optional<PagoJpaEntity> findByNumeroTransaccion(String numeroTransaccion);
    List<PagoJpaEntity> findByIdReservaOrderByFechaPagoDesc(Long idReserva);
}
