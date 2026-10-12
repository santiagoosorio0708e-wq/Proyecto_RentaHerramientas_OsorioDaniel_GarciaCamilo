package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence;

import com.rentaherramientas.modules.devoluciones.domain.ports.out.DevolucionReservaPort;
import com.rentaherramientas.modules.reservas.domain.model.EstadoReserva;
import com.rentaherramientas.modules.reservas.domain.ports.out.AuditoriaReservaPort;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity.DetalleReservaJpaEntity;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity.ReservaJpaEntity;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.repository.SpringDataReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DevolucionReservaAdapter implements DevolucionReservaPort {

    private final SpringDataReservaRepository reservaRepository;
    private final AuditoriaReservaPort auditoriaReservaPort;

    @Override
    public Optional<InfoReservaDevolucion> obtenerInfoReserva(Long idReserva) {
        return reservaRepository.findById(idReserva).map(r -> {
            List<Long> idsHerramientas = r.getDetalles() != null
                    ? r.getDetalles().stream().map(DetalleReservaJpaEntity::getIdHerramienta).collect(Collectors.toList())
                    : Collections.emptyList();

            return new InfoReservaDevolucion(
                    r.getIdReserva(),
                    r.getCodigoReserva(),
                    r.getFechaFin(),
                    r.getEstado().name(),
                    idsHerramientas
            );
        });
    }

    @Override
    public void marcarReservaComoFinalizada(Long idReserva, Long idUsuario, String observacion) {
        Optional<ReservaJpaEntity> optionalReserva = reservaRepository.findById(idReserva);
        if (optionalReserva.isPresent()) {
            ReservaJpaEntity r = optionalReserva.get();
            String estadoAnterior = r.getEstado().name();
            r.setEstado(EstadoReserva.FINALIZADA);
            reservaRepository.save(r);

            auditoriaReservaPort.registrarCambioEstado(
                    idReserva,
                    estadoAnterior,
                    EstadoReserva.FINALIZADA.name(),
                    idUsuario != null ? idUsuario : 1L,
                    observacion != null ? observacion : "Finalizada por devolución"
            );
        }
    }
}
