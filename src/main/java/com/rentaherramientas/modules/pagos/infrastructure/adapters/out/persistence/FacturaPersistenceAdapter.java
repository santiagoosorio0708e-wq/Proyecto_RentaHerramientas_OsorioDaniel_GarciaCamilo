package com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence;

import com.rentaherramientas.modules.pagos.domain.model.Factura;
import com.rentaherramientas.modules.pagos.domain.ports.out.FacturaRepositoryPort;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity.FacturaJpaEntity;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.mapper.PagoPersistenceMapper;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.repository.SpringDataFacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FacturaPersistenceAdapter implements FacturaRepositoryPort {

    private final SpringDataFacturaRepository facturaRepository;
    private final PagoPersistenceMapper mapper;

    @Override
    public Factura guardar(Factura factura) {
        FacturaJpaEntity entity = mapper.toJpaEntity(factura);
        FacturaJpaEntity guardada = facturaRepository.save(entity);
        return mapper.toDomain(guardada);
    }

    @Override
    public Optional<Factura> buscarPorId(Long id) {
        return facturaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Factura> buscarPorNumero(String numeroFactura) {
        return facturaRepository.findByNumeroFactura(numeroFactura).map(mapper::toDomain);
    }

    @Override
    public Optional<Factura> buscarPorPago(Long idPago) {
        return facturaRepository.findByIdPago(idPago).map(mapper::toDomain);
    }

    @Override
    public List<Factura> buscarPorCliente(Long idCliente) {
        return facturaRepository.findByIdClienteOrderByFechaEmisionDesc(idCliente)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
