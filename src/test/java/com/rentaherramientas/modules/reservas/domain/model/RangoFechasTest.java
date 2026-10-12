package com.rentaherramientas.modules.reservas.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RangoFechasTest {

    @Test
    @DisplayName("Debe calcular correctamente la cantidad neta de días de alquiler")
    void calcularDias_RangoValido() {
        LocalDate inicio = LocalDate.of(2026, 11, 1);
        LocalDate fin = LocalDate.of(2026, 11, 6);

        RangoFechas rango = new RangoFechas(inicio, fin);

        assertEquals(5L, rango.getDiasAlquiler());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la fecha fin es anterior a la fecha inicio")
    void constructor_FechaFinAnteriorAInicio_LanzaExcepcion() {
        LocalDate inicio = LocalDate.of(2026, 11, 10);
        LocalDate fin = LocalDate.of(2026, 11, 5);

        assertThrows(BusinessRuleException.class, () -> new RangoFechas(inicio, fin));
    }

    @Test
    @DisplayName("Debe detectar solapamiento de fechas entre dos rangos")
    void solapaCon_DetectaConflicto() {
        RangoFechas r1 = new RangoFechas(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 10));
        RangoFechas r2 = new RangoFechas(LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 15));

        assertTrue(r1.seSolapaCon(r2));
        assertTrue(r2.seSolapaCon(r1));
    }
}
