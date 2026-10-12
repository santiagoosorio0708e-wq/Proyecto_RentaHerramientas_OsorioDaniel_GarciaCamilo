package com.rentaherramientas.modules.inventario.application.service;

import com.rentaherramientas.common.exception.BusinessRuleException;
import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.inventario.application.dto.CategoriaRequest;
import com.rentaherramientas.modules.inventario.domain.model.Categoria;
import com.rentaherramientas.modules.inventario.domain.ports.in.GestionarCategoriaUseCase;
import com.rentaherramientas.modules.inventario.domain.ports.out.CategoriaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Casos de uso de administracion de categorias del catalogo. */
@Service
@RequiredArgsConstructor
public class CategoriaApplicationService implements GestionarCategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepository;

    @Override
    @Transactional
    public Categoria crear(CategoriaRequest request) {
        if (categoriaRepository.existePorNombre(request.nombre())) {
            throw new BusinessRuleException(
                    "Ya existe una categoria con el nombre " + request.nombre());
        }
        return categoriaRepository.guardar(
                Categoria.crear(request.nombre(), request.descripcion()));
    }

    @Override
    @Transactional
    public Categoria actualizar(Long idCategoria, CategoriaRequest request) {
        Categoria categoria = obtenerPorId(idCategoria);

        // Solo se valida el duplicado si el nombre realmente cambio.
        boolean cambioNombre = !categoria.getNombre().equalsIgnoreCase(request.nombre());
        if (cambioNombre && categoriaRepository.existePorNombre(request.nombre())) {
            throw new BusinessRuleException(
                    "Ya existe otra categoria con el nombre " + request.nombre());
        }

        categoria.renombrar(request.nombre());
        categoria.cambiarDescripcion(request.descripcion());

        return categoriaRepository.guardar(categoria);
    }

    @Override
    @Transactional
    public void eliminar(Long idCategoria) {
        obtenerPorId(idCategoria);

        long asociadas = categoriaRepository.contarHerramientasAsociadas(idCategoria);
        if (asociadas > 0) {
            throw new BusinessRuleException(
                    "No se puede eliminar la categoria porque tiene " + asociadas
                            + " herramienta(s) asociada(s)");
        }

        categoriaRepository.eliminarPorId(idCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria obtenerPorId(Long idCategoria) {
        return categoriaRepository.buscarPorId(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro la categoria con id " + idCategoria));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.listarTodas();
    }
}
