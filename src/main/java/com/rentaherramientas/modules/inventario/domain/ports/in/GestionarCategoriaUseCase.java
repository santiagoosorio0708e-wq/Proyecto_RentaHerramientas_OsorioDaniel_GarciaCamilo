package com.rentaherramientas.modules.inventario.domain.ports.in;

import com.rentaherramientas.modules.inventario.application.dto.CategoriaRequest;
import com.rentaherramientas.modules.inventario.domain.model.Categoria;

import java.util.List;

/** Puerto de entrada: administracion de las categorias del catalogo. */
public interface GestionarCategoriaUseCase {

    Categoria crear(CategoriaRequest request);

    Categoria actualizar(Long idCategoria, CategoriaRequest request);

    void eliminar(Long idCategoria);

    Categoria obtenerPorId(Long idCategoria);

    List<Categoria> listarTodas();
}
