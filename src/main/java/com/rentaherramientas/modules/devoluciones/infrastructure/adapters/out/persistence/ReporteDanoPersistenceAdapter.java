package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence;

import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.devoluciones.domain.model.ReporteDano;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.ReporteDanoRepositoryPort;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.DevolucionJpaEntity;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.ReporteDanoJpaEntity;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.mapper.DevolucionPersistenceMapper;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.repository.SpringDataDevolucionRepository;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.repository.SpringDataReporteDanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReporteDanoPersistenceAdapter implements ReporteDanoRepositoryPort {

    private final SpringDataReporteDanoRepository reporteDanoRepository;
    private final SpringDataDevolucionRepository devolucionRepository;

    @Override
    public ReporteDano guardarReporteDano(ReporteDano reporte) {
        DevolucionJpaEntity devolucion = devolucionRepository.findById(reporte.getIdDevolucion())
                .orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada con ID: " + reporte.getIdDevolucion()));

        ReporteDanoJpaEntity entity = DevolucionPersistenceMapper.toEntity(reporte, devolucion);
        ReporteDanoJpaEntity guardado = reporteDanoRepository.save(entity);
        return DevolucionPersistenceMapper.toDomain(guardado);
    }

    @Override
    public List<ReporteDano> buscarDanosPorDevolucion(Long idDevolucion) {
        return reporteDanoRepository.findByDevolucion_IdDevolucion(idDevolucion).stream()
                .map(DevolucionPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReporteDano> buscarReporteDanoPorId(Long id) {
        return reporteDanoRepository.findById(id)
                .map(DevolucionPersistenceMapper::toDomain);
    }
}
