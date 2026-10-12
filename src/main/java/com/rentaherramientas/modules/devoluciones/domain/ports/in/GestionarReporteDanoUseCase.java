package com.rentaherramientas.modules.devoluciones.domain.ports.in;

import com.rentaherramientas.modules.devoluciones.domain.model.ReporteDano;

import java.util.List;

public interface GestionarReporteDanoUseCase {
    ReporteDano registrarDano(Long idDevolucion, ReporteDano reporte);
    List<ReporteDano> listarDanosPorDevolucion(Long idDevolucion);
}
