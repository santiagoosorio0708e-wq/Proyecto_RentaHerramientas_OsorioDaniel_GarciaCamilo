package com.rentaherramientas.modules.reportes.application.service;

import com.rentaherramientas.modules.reportes.application.dto.DashboardCompletoDto;
import com.rentaherramientas.modules.reportes.domain.model.ClienteFrecuente;
import com.rentaherramientas.modules.reportes.domain.model.HerramientaPopularidad;
import com.rentaherramientas.modules.reportes.domain.model.IngresoPorProveedor;
import com.rentaherramientas.modules.reportes.domain.model.MetricasOperativas;
import com.rentaherramientas.modules.reportes.domain.model.ResumenFinanciero;
import com.rentaherramientas.modules.reportes.domain.ports.out.ReporteRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteApplicationServiceTest {

    @Mock
    private ReporteRepositoryPort reporteRepository;

    @InjectMocks
    private ReporteApplicationService reporteService;

    @Test
    @DisplayName("Debe consultar resumen financiero correctamente")
    void testObtenerResumenFinanciero() {
        ResumenFinanciero financiero = ResumenFinanciero.builder()
                .totalRecaudado(new BigDecimal("12500000.00"))
                .totalSubtotal(new BigDecimal("10504201.68"))
                .totalIva(new BigDecimal("1995798.32"))
                .totalCargosMora(new BigDecimal("150000.00"))
                .totalCargosDano(new BigDecimal("350000.00"))
                .totalFacturasEmitidas(20L)
                .totalPagosExitosos(20L)
                .build();

        when(reporteRepository.consultarResumenFinanciero()).thenReturn(financiero);

        ResumenFinanciero resultado = reporteService.obtenerResumenFinanciero();

        assertNotNull(resultado);
        assertEquals(new BigDecimal("12500000.00"), resultado.getTotalRecaudado());
        assertEquals(20L, resultado.getTotalFacturasEmitidas());
        verify(reporteRepository).consultarResumenFinanciero();
    }

    @Test
    @DisplayName("Debe consultar ranking de herramientas populares")
    void testObtenerTopHerramientasPopulares() {
        List<HerramientaPopularidad> ranking = List.of(
                HerramientaPopularidad.builder()
                        .idHerramienta(1L)
                        .nombre("Rotomartillo SDS Max 1500W")
                        .marca("Bosch")
                        .categoria("Perforación")
                        .totalAlquileres(15L)
                        .ingresosGenerados(new BigDecimal("2250000.00"))
                        .build()
        );

        when(reporteRepository.consultarTopHerramientasPopulares(5)).thenReturn(ranking);

        List<HerramientaPopularidad> resultado = reporteService.obtenerTopHerramientasPopulares(5);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Bosch", resultado.get(0).getMarca());
        verify(reporteRepository).consultarTopHerramientasPopulares(5);
    }

    @Test
    @DisplayName("Debe consolidar el dashboard ejecutivo completo")
    void testObtenerDashboardCompleto() {
        ResumenFinanciero financiero = ResumenFinanciero.builder()
                .totalRecaudado(new BigDecimal("5000000.00"))
                .totalSubtotal(new BigDecimal("4201680.67"))
                .totalIva(new BigDecimal("798319.33"))
                .totalFacturasEmitidas(10L)
                .totalPagosExitosos(10L)
                .build();

        MetricasOperativas operativas = MetricasOperativas.builder()
                .totalHerramientas(25L)
                .herramientasDisponibles(20L)
                .herramientasAlquiladas(3L)
                .herramientasEnMantenimiento(2L)
                .tasaIncidenciasDanoPorcentaje(4.5)
                .build();

        when(reporteRepository.consultarResumenFinanciero()).thenReturn(financiero);
        when(reporteRepository.consultarMetricasOperativas()).thenReturn(operativas);
        when(reporteRepository.consultarTopHerramientasPopulares(5)).thenReturn(List.of());
        when(reporteRepository.consultarTopClientesFrecuentes(5)).thenReturn(List.of());
        when(reporteRepository.consultarIngresosPorProveedor()).thenReturn(List.of());

        DashboardCompletoDto dashboard = reporteService.obtenerDashboardCompleto();

        assertNotNull(dashboard);
        assertNotNull(dashboard.getResumenFinanciero());
        assertNotNull(dashboard.getMetricasOperativas());
        assertEquals(new BigDecimal("5000000.00"), dashboard.getResumenFinanciero().getTotalRecaudado());
        assertEquals(25L, dashboard.getMetricasOperativas().getTotalHerramientas());
    }

    @Test
    @DisplayName("Debe consultar ranking de clientes frecuentes")
    void testObtenerTopClientesFrecuentes() {
        ClienteFrecuente cliente = ClienteFrecuente.builder()
                .idCliente(8L)
                .nombreCompleto("Constructora Los Andes S.A.S.")
                .email("compras@constructora-andes.com")
                .totalReservas(12L)
                .totalGastado(new BigDecimal("4800000.00"))
                .build();

        when(reporteRepository.consultarTopClientesFrecuentes(5)).thenReturn(List.of(cliente));

        List<ClienteFrecuente> resultado = reporteService.obtenerTopClientesFrecuentes(5);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Constructora Los Andes S.A.S.", resultado.get(0).getNombreCompleto());
        verify(reporteRepository).consultarTopClientesFrecuentes(5);
    }

    @Test
    @DisplayName("Debe consultar ingresos agrupados por proveedor")
    void testObtenerIngresosPorProveedor() {
        IngresoPorProveedor proveedor = IngresoPorProveedor.builder()
                .idProveedor(2L)
                .nombreProveedor("Maquinaria Antioquia S.A.S.")
                .totalHerramientasRegistradas(10L)
                .totalAlquileres(30L)
                .ingresosGenerados(new BigDecimal("9500000.00"))
                .build();

        when(reporteRepository.consultarIngresosPorProveedor()).thenReturn(List.of(proveedor));

        List<IngresoPorProveedor> resultado = reporteService.obtenerIngresosPorProveedor();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Maquinaria Antioquia S.A.S.", resultado.get(0).getNombreProveedor());
        verify(reporteRepository).consultarIngresosPorProveedor();
    }
}


