package com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence;

import com.rentaherramientas.modules.pagos.domain.model.MetodoPago;
import com.rentaherramientas.modules.pagos.domain.ports.out.MetodoPagoRepositoryPort;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.mapper.PagoPersistenceMapper;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.repository.SpringDataMetodoPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MetodoPagoPersistenceAdapter implements MetodoPagoRepositoryPort {

    private final SpringDataMetodoPagoRepository metodoPagoRepository;
    private final PagoPersistenceMapper mapper;

    @Override
    public List<MetodoPago> listarActivos() {
        return metodoPagoRepository.findByActivoTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<MetodoPago> buscarPorId(Long id) {
        return metodoPagoRepository.findById(id).map(mapper::toDomain);
    }
}
