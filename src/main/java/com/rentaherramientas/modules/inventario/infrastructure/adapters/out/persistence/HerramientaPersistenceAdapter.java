package com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence;

import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.inventario.application.dto.FiltroCatalogoQuery;
import com.rentaherramientas.modules.inventario.domain.model.Categoria;
import com.rentaherramientas.modules.inventario.domain.model.EstadoHerramienta;
import com.rentaherramientas.modules.inventario.domain.model.Herramienta;
import com.rentaherramientas.modules.inventario.domain.ports.out.HerramientaRepositoryPort;
import com.rentaherramientas.modules.inventario.domain.ports.out.ProveedorPort;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.CategoriaJpaEntity;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.HerramientaJpaEntity;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.mapper.HerramientaPersistenceMapper;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.repository.SpringDataCategoriaRepository;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.repository.SpringDataHerramientaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Adaptador de salida del inventario, implementado con Spring Data JPA. */
@Component
@RequiredArgsConstructor
public class HerramientaPersistenceAdapter implements HerramientaRepositoryPort {

    private final SpringDataHerramientaRepository herramientaRepository;
    private final SpringDataCategoriaRepository categoriaRepository;
    private final HerramientaPersistenceMapper mapper;
    private final ProveedorPort proveedorPort;

    @Override
    @Transactional
    public Herramienta guardar(Herramienta herramienta) {

        HerramientaJpaEntity existente = (herramienta.getIdHerramienta() != null)
                ? herramientaRepository.findById(herramienta.getIdHerramienta()).orElse(null)
                : null;

        CategoriaJpaEntity categoriaPrincipal = resolverCategoria(herramienta.getCategoriaPrincipal());
        Set<CategoriaJpaEntity> categorias = resolverCategorias(herramienta.getCategorias());

        HerramientaJpaEntity aGuardar =
                mapper.toEntity(herramienta, existente, categoriaPrincipal, categorias);

        return mapper.toDomain(herramientaRepository.save(aGuardar));
    }

    private CategoriaJpaEntity resolverCategoria(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        if (categoria.getIdCategoria() != null) {
            return categoriaRepository.findById(categoria.getIdCategoria())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontro la categoria con id " + categoria.getIdCategoria()));
        }
        return categoriaRepository.findByNombreIgnoreCase(categoria.getNombre())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro la categoria " + categoria.getNombre()));
    }

    private Set<CategoriaJpaEntity> resolverCategorias(Set<Categoria> categorias) {
        Set<CategoriaJpaEntity> resultado = new HashSet<>();
        if (categorias == null) {
            return resultado;
        }
        categorias.forEach(categoria -> resultado.add(resolverCategoria(categoria)));
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Herramienta> buscarPorId(Long idHerramienta) {
        return herramientaRepository.findById(idHerramienta).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Herramienta> buscarPorNumeroSerie(String numeroSerie) {
        return herramientaRepository.findByNumeroSerieIgnoreCase(numeroSerie).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNumeroSerie(String numeroSerie) {
        return herramientaRepository.existsByNumeroSerieIgnoreCase(numeroSerie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Herramienta> listarTodas() {
        return herramientaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Herramienta> listarPorProveedor(Long idProveedor) {
        return herramientaRepository.findByIdProveedorOrderByIdHerramientaAsc(idProveedor)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Herramienta> listarPorCategoria(Long idCategoria) {
        return herramientaRepository.buscarPorCategoria(idCategoria)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Herramienta> listarPorEstado(EstadoHerramienta estado) {
        return herramientaRepository.findByEstadoOperativoOrderByIdHerramientaAsc(estado)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Herramienta> buscarPorFiltros(FiltroCatalogoQuery filtro) {

        // Si el usuario pidio ver solo las disponibles se activa la bandera y
        // se filtra por DISPONIBLE; si no, la bandera queda en false y el
        // estado enviado se ignora dentro de la consulta.
        boolean filtrarEstado = Boolean.TRUE.equals(filtro.soloDisponibles());

        String texto = (filtro.texto() == null || filtro.texto().isBlank()) ? null : filtro.texto().trim();

        List<Herramienta> resultado = herramientaRepository.buscarPorFiltros(
                texto,
                filtro.idCategoria(),
                filtro.precioMinimo(),
                filtro.precioMaximo(),
                filtrarEstado,
                EstadoHerramienta.DISPONIBLE
        ).stream().map(mapper::toDomain).toList();

        // El filtro por ubicacion se resuelve aqui y no en el JPQL porque la
        // ciudad vive en el perfil del proveedor, que pertenece al bounded
        // context de Identidad. Consultarla por SQL desde aqui acoplaria los
        // dos modulos; en su lugar se usa el puerto anticorrupcion.
        if (filtro.ubicacion() != null && !filtro.ubicacion().isBlank()) {
            String ciudadBuscada = filtro.ubicacion().trim().toLowerCase();
            resultado = resultado.stream()
                    .filter(herramienta -> {
                        String ciudad = proveedorPort.ciudadDeOperacion(herramienta.getIdProveedor());
                        return ciudad != null && ciudad.toLowerCase().contains(ciudadBuscada);
                    })
                    .toList();
        }

        return resultado;
    }

    @Override
    @Transactional
    public void eliminarPorId(Long idHerramienta) {
        if (!herramientaRepository.existsById(idHerramienta)) {
            throw new ResourceNotFoundException("No se encontro la herramienta con id " + idHerramienta);
        }
        herramientaRepository.deleteById(idHerramienta);
    }
}
