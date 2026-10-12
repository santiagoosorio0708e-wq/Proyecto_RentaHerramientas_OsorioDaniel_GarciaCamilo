package com.rentaherramientas.modules.reportes.domain.ports.out;

import com.rentaherramientas.modules.reportes.domain.model.ClienteFrecuente;
import com.rentaherramientas.modules.reportes.domain.model.HerramientaPopularidad;
import com.rentaherramientas.modules.reportes.domain.model.IngresoPorProveedor;
import com.rentaherramientas.modules.reportes.domain.model.MetricasOperativas;
import com.rentaherramientas.modules.reportes.domain.model.ResumenFinanciero;

import java.util.List;

public interface ReporteRepositoryPort {
    ResumenFinanciero consultarResumenFinanciero();
    List<HerramientaPopularidad> consultarTopHerramientasPopulares(int limite);
    List<ClienteFrecuente> consultarTopClientesFrecuentes(int limite);
    MetricasOperativas consultarMetricasOperativas();
    List<IngresoPorProveedor> consultarIngresosPorProveedor();
}
