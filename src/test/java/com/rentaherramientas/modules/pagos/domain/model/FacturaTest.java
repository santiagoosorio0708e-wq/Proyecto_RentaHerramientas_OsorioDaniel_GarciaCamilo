package com.rentaherramientas.modules.pagos.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FacturaTest {

    @Test
    @DisplayName("Debe descomponer exactamente la base gravable y el 19% de IVA a partir del total")
    void calcularValoresFiscales_DescomponeIvaCorrectamente() {
        Factura factura = new Factura();
        BigDecimal total = new BigDecimal("119000.00");

        factura.calcularValoresFiscales(total);

        assertEquals(new BigDecimal("119000.00"), factura.getTotal());
        assertEquals(new BigDecimal("100000.00"), factura.getSubtotal());
        assertEquals(new BigDecimal("19000.00"), factura.getIva());
        assertEquals(factura.getTotal(), factura.getSubtotal().add(factura.getIva()));
    }

    @Test
    @DisplayName("Debe verificar si la factura está pagada correctamente")
    void estaPagada_VerificaEstado() {
        Factura fPagada = Factura.builder().estado(EstadoFactura.PAGADA).build();
        Factura fEmitida = Factura.builder().estado(EstadoFactura.EMITIDA).build();

        assertTrue(fPagada.estaPagada());
        assertFalse(fEmitida.estaPagada());
    }
}
