package com.rentaherramientas.modules.inventario.domain.ports.out;

import com.rentaherramientas.modules.inventario.application.dto.FiltroCatalogoQuery;
import com.rentaherramientas.modules.inventario.domain.model.EstadoHerramienta;
import com.rentaherramientas.modules.inventario.domain.model.Herramienta;

import java.util.List;
import java.util.Optional;

/** Puerto de salida hacia la persistencia de herramientas. */
public interface HerramientaRepositoryPort {

    Herramienta guardar(Herramienta herramienta);

    Optional<Herramienta> buscarPorId(Long idHerramienta);

    Optional<Herramienta> buscarPorNumeroSerie(String numeroSerie);

    boolean existePorNumeroSerie(String numeroSerie);

    List<Herramienta> listarTodas();

    List<Herramienta> listarPorProveedor(Long idProveedor);

    List<Herramienta> listarPorCategoria(Long idCategoria);

    List<Herramienta> listarPorEstado(EstadoHerramienta estado);

    /** Busqueda del catalogo publico aplicando los filtros recibidos. */
    List<Herramienta> buscarPorFiltros(FiltroCatalogoQuery filtro);

    void eliminarPorId(Long idHerramienta);
}
