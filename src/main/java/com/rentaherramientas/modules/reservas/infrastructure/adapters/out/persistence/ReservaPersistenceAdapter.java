package com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence;

import com.rentaherramientas.modules.reservas.domain.model.Reserva;
import com.rentaherramientas.modules.reservas.domain.ports.out.AuditoriaReservaPort;
import com.rentaherramientas.modules.reservas.domain.ports.out.ReservaRepositoryPort;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity.AuditoriaReservaJpaEntity;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity.ReservaJpaEntity;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.mapper.ReservaPersistenceMapper;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.repository.SpringDataAuditoriaReservaRepository;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.repository.SpringDataReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservaPersistenceAdapter implements ReservaRepositoryPort, AuditoriaReservaPort {

    private final SpringDataReservaRepository reservaRepository;
    private final SpringDataAuditoriaReservaRepository auditoriaRepository;
    private final ReservaPersistenceMapper mapper;

    @Override
    public Reserva guardar(Reserva reserva) {
        ReservaJpaEntity entity = mapper.toJpaEntity(reserva);
        ReservaJpaEntity guardada = reservaRepository.save(entity);
        return mapper.toDomain(guardada);
    }

    @Override
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Reserva> buscarPorCodigo(String codigo) {
        return reservaRepository.findByCodigoReserva(codigo).map(mapper::toDomain);
    }

    @Override
    public List<Reserva> buscarPorCliente(Long idCliente) {
        return reservaRepository.findByIdClienteOrderByFechaSolicitudDesc(idCliente)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Reserva> buscarTodas() {
        return reservaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Reserva> buscarPorRangoFechaInicio(LocalDate desde, LocalDate hasta) {
        return reservaRepository.findByFechaInicioBetweenOrderByFechaInicioAsc(desde, hasta)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existeConflictoFechas(Long idHerramienta, LocalDate inicio, LocalDate fin) {
        return reservaRepository.existsConflictoFechas(idHerramienta, inicio, fin);
    }

    @Override
    public void registrarCambioEstado(Long idReserva, String estadoAnterior, String estadoNuevo, Long cambiadoPor, String observacion) {
        AuditoriaReservaJpaEntity auditoria = AuditoriaReservaJpaEntity.builder()
                .idReserva(idReserva)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .cambiadoPor(cambiadoPor)
                .fechaCambio(LocalDateTime.now())
                .observacion(observacion)
                .build();
        auditoriaRepository.save(auditoria);
    }
}
