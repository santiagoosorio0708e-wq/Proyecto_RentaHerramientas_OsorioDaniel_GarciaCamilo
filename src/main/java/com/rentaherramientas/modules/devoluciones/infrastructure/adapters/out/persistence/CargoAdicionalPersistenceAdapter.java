package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence;

import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.devoluciones.domain.model.CargoAdicional;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.CargoAdicionalRepositoryPort;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.CargoAdicionalJpaEntity;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.DevolucionJpaEntity;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.mapper.DevolucionPersistenceMapper;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.repository.SpringDataCargoAdicionalRepository;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.repository.SpringDataDevolucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CargoAdicionalPersistenceAdapter implements CargoAdicionalRepositoryPort {

    private final SpringDataCargoAdicionalRepository cargoAdicionalRepository;
    private final SpringDataDevolucionRepository devolucionRepository;

    @Override
    public CargoAdicional guardarCargoAdicional(CargoAdicional cargo) {
        DevolucionJpaEntity devolucion = devolucionRepository.findById(cargo.getIdDevolucion())
                .orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada con ID: " + cargo.getIdDevolucion()));

        CargoAdicionalJpaEntity entity = DevolucionPersistenceMapper.toEntity(cargo, devolucion);
        CargoAdicionalJpaEntity guardado = cargoAdicionalRepository.save(entity);
        return DevolucionPersistenceMapper.toDomain(guardado);
    }

    @Override
    public Optional<CargoAdicional> buscarCargoAdicionalPorId(Long id) {
        return cargoAdicionalRepository.findById(id)
                .map(DevolucionPersistenceMapper::toDomain);
    }

    @Override
    public List<CargoAdicional> buscarCargosPorDevolucion(Long idDevolucion) {
        return cargoAdicionalRepository.findByDevolucion_IdDevolucion(idDevolucion).stream()
                .map(DevolucionPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
