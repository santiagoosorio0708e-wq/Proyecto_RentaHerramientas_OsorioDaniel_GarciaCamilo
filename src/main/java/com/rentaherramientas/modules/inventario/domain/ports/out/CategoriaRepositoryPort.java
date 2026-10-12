package com.rentaherramientas.modules.inventario.domain.ports.out;

import com.rentaherramientas.modules.inventario.domain.model.Categoria;

import java.util.List;
import java.util.Optional;

/** Puerto de salida hacia la persistencia de categorias. */
public interface CategoriaRepositoryPort {

    Categoria guardar(Categoria categoria);

    Optional<Categoria> buscarPorId(Long idCategoria);

    Optional<Categoria> buscarPorNombre(String nombre);

    boolean existePorNombre(String nombre);

    List<Categoria> listarTodas();

    void eliminarPorId(Long idCategoria);

    /** Se usa para impedir borrar una categoria que todavia tiene herramientas. */
    long contarHerramientasAsociadas(Long idCategoria);
}
