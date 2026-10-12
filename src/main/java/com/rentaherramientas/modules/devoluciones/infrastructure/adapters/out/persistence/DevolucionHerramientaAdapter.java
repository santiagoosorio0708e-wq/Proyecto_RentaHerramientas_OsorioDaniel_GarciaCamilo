package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence;

import com.rentaherramientas.modules.devoluciones.domain.ports.out.DevolucionHerramientaPort;
import com.rentaherramientas.modules.inventario.domain.model.EstadoHerramienta;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.HerramientaJpaEntity;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.repository.SpringDataHerramientaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DevolucionHerramientaAdapter implements DevolucionHerramientaPort {

    private final SpringDataHerramientaRepository herramientaRepository;

    @Override
    public void actualizarEstadoOperativo(Long idHerramienta, String nuevoEstado, String motivo) {
        Optional<HerramientaJpaEntity> optionalHerramienta = herramientaRepository.findById(idHerramienta);
        if (optionalHerramienta.isPresent()) {
            HerramientaJpaEntity h = optionalHerramienta.get();
            try {
                EstadoHerramienta estado = EstadoHerramienta.valueOf(nuevoEstado);
                EstadoHerramienta anterior = h.getEstadoOperativo();
                h.setEstadoOperativo(estado);
                herramientaRepository.save(h);
                log.info("Herramienta {}: estado {} -> {} actualizado por devolución. Motivo: {}",
                        idHerramienta, anterior, estado, motivo);
            } catch (IllegalArgumentException e) {
                log.warn("Estado operativo desconocido: {}", nuevoEstado);
            }
        }
    }
}
