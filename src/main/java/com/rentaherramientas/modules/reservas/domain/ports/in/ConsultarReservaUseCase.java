package com.rentaherramientas.modules.reservas.domain.ports.in;

import com.rentaherramientas.modules.reservas.domain.model.Reserva;

import java.util.List;

/**
 * Puerto de Entrada (Use Case): Consultas del Historial y Estado de Reservas.
 * Permite a clientes, proveedores y administradores consultar reservas por identificador
 * numérico, código fiscal único o listados filtrados por usuario.
 *
 * @author Daniel Osorio (DEV 2)
 */
public interface ConsultarReservaUseCase {
    Reserva obtenerPorId(Long id);
    Reserva obtenerPorCodigo(String codigo);
    List<Reserva> listarPorCliente(Long idCliente);
    List<Reserva> listarTodas();
}

