package com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence;

import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.inventario.domain.model.Categoria;
import com.rentaherramientas.modules.inventario.domain.ports.out.CategoriaRepositoryPort;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.CategoriaJpaEntity;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.mapper.HerramientaPersistenceMapper;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.repository.SpringDataCategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Adaptador de salida para la tabla categorias. */
@Component
@RequiredArgsConstructor
public class CategoriaPersistenceAdapter implements CategoriaRepositoryPort {

    private final SpringDataCategoriaRepository categoriaRepository;
    private final HerramientaPersistenceMapper mapper;

    @Override
    @Transactional
    public Categoria guardar(Categoria categoria) {
        CategoriaJpaEntity existente = (categoria.getIdCategoria() != null)
                ? categoriaRepository.findById(categoria.getIdCategoria()).orElse(null)
                : null;

        CategoriaJpaEntity entity = mapper.toCategoriaEntity(categoria, existente);
        return mapper.toCategoriaDomain(categoriaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorId(Long idCategoria) {
        return categoriaRepository.findById(idCategoria).map(mapper::toCategoriaDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombreIgnoreCase(nombre).map(mapper::toCategoriaDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombre) {
        return categoriaRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll().stream().map(mapper::toCategoriaDomain).toList();
    }

    @Override
    @Transactional
    public void eliminarPorId(Long idCategoria) {
        if (!categoriaRepository.existsById(idCategoria)) {
            throw new ResourceNotFoundException("No se encontro la categoria con id " + idCategoria);
        }
        categoriaRepository.deleteById(idCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarHerramientasAsociadas(Long idCategoria) {
        return categoriaRepository.contarHerramientasAsociadas(idCategoria);
    }
}
