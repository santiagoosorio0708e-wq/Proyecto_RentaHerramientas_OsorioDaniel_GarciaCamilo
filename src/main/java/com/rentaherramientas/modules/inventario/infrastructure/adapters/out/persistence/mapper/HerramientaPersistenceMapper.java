package com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.mapper;

import com.rentaherramientas.modules.inventario.domain.model.Categoria;
import com.rentaherramientas.modules.inventario.domain.model.EspecificacionTecnica;
import com.rentaherramientas.modules.inventario.domain.model.Herramienta;
import com.rentaherramientas.modules.inventario.domain.model.ImagenHerramienta;
import com.rentaherramientas.modules.inventario.domain.model.TarifaAlquiler;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.CategoriaJpaEntity;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.EspecificacionTecnicaJpaEntity;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.HerramientaJpaEntity;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.ImagenHerramientaJpaEntity;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.TarifaHerramientaJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Traductor entre el dominio del inventario y sus entidades JPA. */
@Component
public class HerramientaPersistenceMapper {

    // ------------------------------------------------------------------
    // JPA -> DOMINIO
    // ------------------------------------------------------------------

    public Herramienta toDomain(HerramientaJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        List<ImagenHerramienta> imagenes = entity.getImagenes() == null ? new ArrayList<>()
                : entity.getImagenes().stream().map(this::toImagenDomain)
                        .collect(Collectors.toCollection(ArrayList::new));

        List<EspecificacionTecnica> especificaciones = entity.getEspecificaciones() == null
                ? new ArrayList<>()
                : entity.getEspecificaciones().stream().map(this::toEspecificacionDomain)
                        .collect(Collectors.toCollection(ArrayList::new));

        List<TarifaAlquiler> tarifas = entity.getTarifas() == null ? new ArrayList<>()
                : entity.getTarifas().stream().map(this::toTarifaDomain)
                        .collect(Collectors.toCollection(ArrayList::new));

        Set<Categoria> categorias = entity.getCategorias() == null ? new HashSet<>()
                : entity.getCategorias().stream().map(this::toCategoriaDomain)
                        .collect(Collectors.toCollection(HashSet::new));

        return Herramienta.builder()
                .idHerramienta(entity.getIdHerramienta())
                .idProveedor(entity.getIdProveedor())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .marca(entity.getMarca())
                .modelo(entity.getModelo())
                .numeroSerie(entity.getNumeroSerie())
                .estadoOperativo(entity.getEstadoOperativo())
                .tarifaDiaria(entity.getTarifaDiaria())
                .valorReposicion(entity.getValorReposicion())
                .ubicacionBodega(entity.getUbicacionBodega())
                .fechaRegistro(entity.getFechaRegistro())
                .categoriaPrincipal(toCategoriaDomain(entity.getCategoriaPrincipal()))
                .categorias(categorias)
                .imagenes(imagenes)
                .especificaciones(especificaciones)
                .tarifas(tarifas)
                .build();
    }

    public Categoria toCategoriaDomain(CategoriaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Categoria.builder()
                .idCategoria(entity.getIdCategoria())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public ImagenHerramienta toImagenDomain(ImagenHerramientaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ImagenHerramienta.builder()
                .idImagen(entity.getIdImagen())
                .idHerramienta(entity.getHerramienta() != null
                        ? entity.getHerramienta().getIdHerramienta() : null)
                .urlFoto(entity.getUrlFoto())
                .descripcion(entity.getDescripcion())
                .principal(entity.isPrincipal())
                .orden(entity.getOrden())
                .build();
    }

    public EspecificacionTecnica toEspecificacionDomain(EspecificacionTecnicaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return EspecificacionTecnica.builder()
                .idEspecificacion(entity.getIdEspecificacion())
                .idHerramienta(entity.getHerramienta() != null
                        ? entity.getHerramienta().getIdHerramienta() : null)
                .atributo(entity.getAtributo())
                .valor(entity.getValor())
                .unidadMedida(entity.getUnidadMedida())
                .build();
    }

    public TarifaAlquiler toTarifaDomain(TarifaHerramientaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return TarifaAlquiler.builder()
                .idTarifa(entity.getIdTarifa())
                .idHerramienta(entity.getHerramienta() != null
                        ? entity.getHerramienta().getIdHerramienta() : null)
                .tipoTarifa(entity.getTipoTarifa())
                .valor(entity.getValor())
                .activa(entity.isActiva())
                .build();
    }

    // ------------------------------------------------------------------
    // DOMINIO -> JPA
    // ------------------------------------------------------------------

    /**
     * Vuelca el agregado sobre la entidad JPA.
     *
     * Para las colecciones hijas se vacia y se vuelve a llenar la lista
     * existente (no se reemplaza por una nueva). Es importante hacerlo asi
     * porque con orphanRemoval = true Hibernate lanza error si se le cambia
     * la referencia de la coleccion que el esta administrando.
     */
    public HerramientaJpaEntity toEntity(Herramienta herramienta,
                                         HerramientaJpaEntity existente,
                                         CategoriaJpaEntity categoriaPrincipal,
                                         Set<CategoriaJpaEntity> categorias) {

        HerramientaJpaEntity entity = (existente != null) ? existente : new HerramientaJpaEntity();

        entity.setIdHerramienta(herramienta.getIdHerramienta());
        entity.setIdProveedor(herramienta.getIdProveedor());
        entity.setCategoriaPrincipal(categoriaPrincipal);
        entity.setNombre(herramienta.getNombre());
        entity.setDescripcion(herramienta.getDescripcion());
        entity.setMarca(herramienta.getMarca());
        entity.setModelo(herramienta.getModelo());
        entity.setNumeroSerie(herramienta.getNumeroSerie());
        entity.setEstadoOperativo(herramienta.getEstadoOperativo());
        entity.setTarifaDiaria(herramienta.getTarifaDiaria());
        entity.setValorReposicion(herramienta.getValorReposicion());
        entity.setUbicacionBodega(herramienta.getUbicacionBodega());
        entity.setFechaRegistro(herramienta.getFechaRegistro());

        if (categorias != null) {
            if (entity.getCategorias() == null) {
                entity.setCategorias(new HashSet<>());
            }
            entity.getCategorias().clear();
            entity.getCategorias().addAll(categorias);
        }

        entity.limpiarImagenes();
        if (herramienta.getImagenes() != null) {
            for (ImagenHerramienta imagen : herramienta.getImagenes()) {
                entity.agregarImagen(ImagenHerramientaJpaEntity.builder()
                        .urlFoto(imagen.getUrlFoto())
                        .descripcion(imagen.getDescripcion())
                        .principal(imagen.isPrincipal())
                        .orden(imagen.getOrden())
                        .build());
            }
        }

        entity.limpiarEspecificaciones();
        if (herramienta.getEspecificaciones() != null) {
            for (EspecificacionTecnica especificacion : herramienta.getEspecificaciones()) {
                entity.agregarEspecificacion(EspecificacionTecnicaJpaEntity.builder()
                        .atributo(especificacion.getAtributo())
                        .valor(especificacion.getValor())
                        .unidadMedida(especificacion.getUnidadMedida())
                        .build());
            }
        }

        entity.limpiarTarifas();
        if (herramienta.getTarifas() != null) {
            for (TarifaAlquiler tarifa : herramienta.getTarifas()) {
                entity.agregarTarifa(TarifaHerramientaJpaEntity.builder()
                        .tipoTarifa(tarifa.getTipoTarifa())
                        .valor(tarifa.getValor())
                        .activa(tarifa.isActiva())
                        .build());
            }
        }

        return entity;
    }

    public CategoriaJpaEntity toCategoriaEntity(Categoria categoria, CategoriaJpaEntity existente) {
        CategoriaJpaEntity entity = (existente != null) ? existente : new CategoriaJpaEntity();
        entity.setIdCategoria(categoria.getIdCategoria());
        entity.setNombre(categoria.getNombre());
        entity.setDescripcion(categoria.getDescripcion());
        return entity;
    }
}
