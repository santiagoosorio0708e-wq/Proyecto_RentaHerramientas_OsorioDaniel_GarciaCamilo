package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence;

import com.rentaherramientas.modules.devoluciones.domain.model.Devolucion;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.DevolucionRepositoryPort;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.DevolucionJpaEntity;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.mapper.DevolucionPersistenceMapper;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.repository.SpringDataDevolucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DevolucionPersistenceAdapter implements DevolucionRepositoryPort {

    private final SpringDataDevolucionRepository devolucionRepository;

    @Override
    public Devolucion guardarDevolucion(Devolucion devolucion) {
        DevolucionJpaEntity entity = DevolucionPersistenceMapper.toEntity(devolucion);
        DevolucionJpaEntity guardada = devolucionRepository.save(entity);
        return DevolucionPersistenceMapper.toDomain(guardada);
    }

    @Override
    public Optional<Devolucion> buscarDevolucionPorId(Long id) {
        return devolucionRepository.findById(id)
                .map(DevolucionPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Devolucion> buscarDevolucionPorReserva(Long idReserva) {
        return devolucionRepository.findByIdReserva(idReserva)
                .map(DevolucionPersistenceMapper::toDomain);
    }

    @Override
    public List<Devolucion> listarTodasLasDevoluciones() {
        return devolucionRepository.findAllByOrderByFechaDevolucionRealDesc().stream()
                .map(DevolucionPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
