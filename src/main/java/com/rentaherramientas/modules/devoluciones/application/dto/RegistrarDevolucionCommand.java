package com.rentaherramientas.modules.devoluciones.application.dto;

import com.rentaherramientas.modules.devoluciones.domain.model.EstadoEquipoGeneral;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Comando de Aplicación (DTO): Solicitud de Check-In Físico de Retorno de Maquinaria.
 * Encapsula la fecha de devolución real para la liquidación de mora por retraso,
 * el estado físico global del lote y la lista opcional de reportes de daños
 * con soporte de evidencias fotográficas en Cuarta Forma Normal (4FN).
 *
 * @author Daniel Osorio (DEV 2)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarDevolucionCommand {
    @NotNull(message = "El ID de la reserva es obligatorio")
    private Long idReserva;

    @NotNull(message = "La fecha de devolución real es obligatoria")
    private LocalDate fechaDevolucionReal;

    @NotNull(message = "El ID del usuario que recibe es obligatorio")
    private Long recibidoPor;

    @NotNull(message = "El estado general del equipo es obligatorio")
    private EstadoEquipoGeneral estadoEquipoGeneral;

    private String observaciones;

    @Builder.Default
    private BigDecimal tarifaDiariaMora = new BigDecimal("25000.00");

    @Valid
    @Builder.Default
    private List<ReporteDanoRequestDto> reportesDano = new ArrayList<>();
}
