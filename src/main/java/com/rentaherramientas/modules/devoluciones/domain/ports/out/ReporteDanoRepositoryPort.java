package com.rentaherramientas.modules.devoluciones.domain.ports.out;

import com.rentaherramientas.modules.devoluciones.domain.model.ReporteDano;

import java.util.List;
import java.util.Optional;

public interface ReporteDanoRepositoryPort {
    ReporteDano guardarReporteDano(ReporteDano reporte);
    List<ReporteDano> buscarDanosPorDevolucion(Long idDevolucion);
    Optional<ReporteDano> buscarReporteDanoPorId(Long id);
}
