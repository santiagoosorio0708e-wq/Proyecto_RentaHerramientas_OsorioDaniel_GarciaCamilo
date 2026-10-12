package com.rentaherramientas.modules.reportes.application.service;

import com.rentaherramientas.modules.reportes.application.dto.DashboardCompletoDto;
import com.rentaherramientas.modules.reportes.application.dto.ReporteDtoMapper;
import com.rentaherramientas.modules.reportes.domain.model.ClienteFrecuente;
import com.rentaherramientas.modules.reportes.domain.model.HerramientaPopularidad;
import com.rentaherramientas.modules.reportes.domain.model.IngresoPorProveedor;
import com.rentaherramientas.modules.reportes.domain.model.MetricasOperativas;
import com.rentaherramientas.modules.reportes.domain.model.ResumenFinanciero;
import com.rentaherramientas.modules.reportes.domain.ports.in.ConsultarReportesUseCase;
import com.rentaherramientas.modules.reportes.domain.ports.out.ReporteRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteApplicationService implements ConsultarReportesUseCase {

    private final ReporteRepositoryPort reporteRepository;

    @Override
    @Transactional(readOnly = true)
    public ResumenFinanciero obtenerResumenFinanciero() {
        log.info("Generando resumen financiero consolidado");
        return reporteRepository.consultarResumenFinanciero();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HerramientaPopularidad> obtenerTopHerramientasPopulares(int limite) {
        log.info("Generando ranking de herramientas más alquiladas (límite: {})", limite);
        return reporteRepository.consultarTopHerramientasPopulares(limite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteFrecuente> obtenerTopClientesFrecuentes(int limite) {
        log.info("Generando ranking de clientes más frecuentes (límite: {})", limite);
        return reporteRepository.consultarTopClientesFrecuentes(limite);
    }

    @Override
    @Transactional(readOnly = true)
    public MetricasOperativas obtenerMetricasOperativas() {
        log.info("Calculando métricas operativas de maquinaria e inventario");
        return reporteRepository.consultarMetricasOperativas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngresoPorProveedor> obtenerIngresosPorProveedor() {
        log.info("Calculando distribución de ingresos por proveedor");
        return reporteRepository.consultarIngresosPorProveedor();
    }

    @Transactional(readOnly = true)
    public DashboardCompletoDto obtenerDashboardCompleto() {
        log.info("Construyendo dashboard ejecutivo integral");
        ResumenFinanciero financiero = obtenerResumenFinanciero();
        MetricasOperativas operativas = obtenerMetricasOperativas();
        List<HerramientaPopularidad> topHerramientas = obtenerTopHerramientasPopulares(5);
        List<ClienteFrecuente> topClientes = obtenerTopClientesFrecuentes(5);
        List<IngresoPorProveedor> proveedores = obtenerIngresosPorProveedor();

        return DashboardCompletoDto.builder()
                .resumenFinanciero(ReporteDtoMapper.toDto(financiero))
                .metricasOperativas(ReporteDtoMapper.toDto(operativas))
                .topHerramientasPopulares(topHerramientas.stream().map(ReporteDtoMapper::toDto).collect(Collectors.toList()))
                .topClientesFrecuentes(topClientes.stream().map(ReporteDtoMapper::toDto).collect(Collectors.toList()))
                .ingresosPorProveedor(proveedores.stream().map(ReporteDtoMapper::toDto).collect(Collectors.toList()))
                .build();
    }
}
