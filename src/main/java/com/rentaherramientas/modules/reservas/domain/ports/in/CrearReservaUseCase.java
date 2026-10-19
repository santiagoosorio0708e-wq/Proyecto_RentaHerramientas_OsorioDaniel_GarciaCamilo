package com.rentaherramientas.modules.reservas.domain.ports.in;

import com.rentaherramientas.modules.reservas.application.dto.CrearReservaCommand;
import com.rentaherramientas.modules.reservas.domain.model.Reserva;

/**
 * Puerto de Entrada (Use Case): Creación y Registro de Reservas de Maquinaria.
 * Define el contrato de aplicación para orquestar la verificación de disponibilidad
 * física de herramientas, validación de rangos de fechas y liquidación del depósito
 * en garantía antes de confirmar una reserva de alquiler.
 *
 * @author Daniel Osorio (DEV 2)
 */
public interface CrearReservaUseCase {

    /**
     * Registra una nueva solicitud de reserva en el sistema.
     *
     * @param command Comando con datos del cliente, rango de fechas e ítems a alquilar.
     * @return Entidad de dominio {@link Reserva} persistida en estado SOLICITADA.
     */
    Reserva crearReserva(CrearReservaCommand command);
}

