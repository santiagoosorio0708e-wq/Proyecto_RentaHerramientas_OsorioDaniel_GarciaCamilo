package com.rentaherramientas.modules.reservas.domain.ports.out;

public interface AuditoriaReservaPort {
    void registrarCambioEstado(Long idReserva, String estadoAnterior, String estadoNuevo, Long cambiadoPor, String observacion);
}
