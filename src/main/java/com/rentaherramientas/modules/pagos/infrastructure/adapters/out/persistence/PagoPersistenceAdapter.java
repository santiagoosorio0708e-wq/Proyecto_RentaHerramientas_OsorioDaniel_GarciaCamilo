package com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence;

import com.rentaherramientas.modules.pagos.domain.model.Pago;
import com.rentaherramientas.modules.pagos.domain.ports.out.PagoRepositoryPort;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity.PagoJpaEntity;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.mapper.PagoPersistenceMapper;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.repository.SpringDataPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PagoPersistenceAdapter implements PagoRepositoryPort {

    private final SpringDataPagoRepository pagoRepository;
    private final PagoPersistenceMapper mapper;

    @Override
    public Pago guardar(Pago pago) {
        PagoJpaEntity entity = mapper.toJpaEntity(pago);
        PagoJpaEntity guardada = pagoRepository.save(entity);
        return mapper.toDomain(guardada);
    }

    @Override
    public Optional<Pago> buscarPorId(Long id) {
        return pagoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Pago> buscarPorTransaccion(String transaccion) {
        return pagoRepository.findByNumeroTransaccion(transaccion).map(mapper::toDomain);
    }

    @Override
    public List<Pago> buscarPorReserva(Long idReserva) {
        return pagoRepository.findByIdReservaOrderByFechaPagoDesc(idReserva)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Pago> buscarTodos() {
        return pagoRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
