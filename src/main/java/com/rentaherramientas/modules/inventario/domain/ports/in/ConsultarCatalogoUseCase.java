package com.rentaherramientas.modules.inventario.domain.ports.in;

import com.rentaherramientas.modules.inventario.application.dto.FiltroCatalogoQuery;
import com.rentaherramientas.modules.inventario.domain.model.Herramienta;

import java.util.List;

/** Puerto de entrada: consultas del catalogo publico y del inventario propio. */
public interface ConsultarCatalogoUseCase {

    Herramienta obtenerPorId(Long idHerramienta);

    List<Herramienta> buscar(FiltroCatalogoQuery filtro);

    List<Herramienta> listarTodas();

    List<Herramienta> listarPorCategoria(Long idCategoria);

    /** Inventario del proveedor autenticado. */
    List<Herramienta> listarMiInventario(String emailProveedor);
}
