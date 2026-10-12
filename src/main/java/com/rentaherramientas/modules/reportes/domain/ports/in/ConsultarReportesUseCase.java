package com.rentaherramientas.modules.reportes.domain.ports.in;

import com.rentaherramientas.modules.reportes.domain.model.ClienteFrecuente;
import com.rentaherramientas.modules.reportes.domain.model.HerramientaPopularidad;
import com.rentaherramientas.modules.reportes.domain.model.IngresoPorProveedor;
import com.rentaherramientas.modules.reportes.domain.model.MetricasOperativas;
import com.rentaherramientas.modules.reportes.domain.model.ResumenFinanciero;

import java.util.List;

public interface ConsultarReportesUseCase {
    ResumenFinanciero obtenerResumenFinanciero();
    List<HerramientaPopularidad> obtenerTopHerramientasPopulares(int limite);
    List<ClienteFrecuente> obtenerTopClientesFrecuentes(int limite);
    MetricasOperativas obtenerMetricasOperativas();
    List<IngresoPorProveedor> obtenerIngresosPorProveedor();
}
