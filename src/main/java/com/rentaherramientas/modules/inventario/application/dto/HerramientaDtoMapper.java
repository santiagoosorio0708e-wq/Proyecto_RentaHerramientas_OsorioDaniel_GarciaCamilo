package com.rentaherramientas.modules.inventario.application.dto;

import com.rentaherramientas.modules.inventario.domain.model.Categoria;
import com.rentaherramientas.modules.inventario.domain.model.EspecificacionTecnica;
import com.rentaherramientas.modules.inventario.domain.model.Herramienta;
import com.rentaherramientas.modules.inventario.domain.model.ImagenHerramienta;
import com.rentaherramientas.modules.inventario.domain.model.TarifaAlquiler;

import java.util.Comparator;
import java.util.List;

/** Traduce el dominio del inventario hacia los DTO de salida de la API. */
public final class HerramientaDtoMapper {

    private HerramientaDtoMapper() {
        // Clase de utilidades: no se instancia.
    }

    public static HerramientaResponseDto toResponseDto(Herramienta herramienta) {
        if (herramienta == null) {
            return null;
        }
        return HerramientaResponseDto.builder()
                .idHerramienta(herramienta.getIdHerramienta())
                .idProveedor(herramienta.getIdProveedor())
                .nombre(herramienta.getNombre())
                .descripcion(herramienta.getDescripcion())
                .marca(herramienta.getMarca())
                .modelo(herramienta.getModelo())
                .numeroSerie(herramienta.getNumeroSerie())
                .estadoOperativo(herramienta.getEstadoOperativo() != null
                        ? herramienta.getEstadoOperativo().name() : null)
                .disponible(herramienta.estaDisponible())
                .tarifaDiaria(herramienta.getTarifaDiaria())
                .valorReposicion(herramienta.getValorReposicion())
                .ubicacionBodega(herramienta.getUbicacionBodega())
                .fechaRegistro(herramienta.getFechaRegistro())
                .categoriaPrincipal(toCategoriaDto(herramienta.getCategoriaPrincipal()))
                .categorias(herramienta.getCategorias() == null ? List.of()
                        : herramienta.getCategorias().stream()
                                .map(HerramientaDtoMapper::toCategoriaDto)
                                .sorted(Comparator.comparing(CategoriaResponseDto::nombre,
                                        Comparator.nullsLast(Comparator.naturalOrder())))
                                .toList())
                .imagenes(herramienta.getImagenes() == null ? List.of()
                        : herramienta.getImagenes().stream()
                                .map(HerramientaDtoMapper::toImagenDto)
                                .toList())
                .especificaciones(herramienta.getEspecificaciones() == null ? List.of()
                        : herramienta.getEspecificaciones().stream()
                                .map(HerramientaDtoMapper::toEspecificacionDto)
                                .toList())
                .tarifas(herramienta.getTarifas() == null ? List.of()
                        : herramienta.getTarifas().stream()
                                .map(HerramientaDtoMapper::toTarifaDto)
                                .toList())
                .build();
    }

    /** Version ligera usada en los listados del catalogo. */
    public static HerramientaResumenDto toResumenDto(Herramienta herramienta) {
        if (herramienta == null) {
            return null;
        }
        return HerramientaResumenDto.builder()
                .idHerramienta(herramienta.getIdHerramienta())
                .nombre(herramienta.getNombre())
                .marca(herramienta.getMarca())
                .modelo(herramienta.getModelo())
                .categoriaPrincipal(herramienta.getCategoriaPrincipal() != null
                        ? herramienta.getCategoriaPrincipal().getNombre() : null)
                .estadoOperativo(herramienta.getEstadoOperativo() != null
                        ? herramienta.getEstadoOperativo().name() : null)
                .disponible(herramienta.estaDisponible())
                .tarifaDiaria(herramienta.getTarifaDiaria())
                .imagenPrincipal(herramienta.imagenPrincipal()
                        .map(ImagenHerramienta::getUrlFoto)
                        .orElse(null))
                .idProveedor(herramienta.getIdProveedor())
                .build();
    }

    public static CategoriaResponseDto toCategoriaDto(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return CategoriaResponseDto.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }

    public static ImagenHerramientaDto toImagenDto(ImagenHerramienta imagen) {
        if (imagen == null) {
            return null;
        }
        return ImagenHerramientaDto.builder()
                .idImagen(imagen.getIdImagen())
                .urlFoto(imagen.getUrlFoto())
                .descripcion(imagen.getDescripcion())
                .principal(imagen.isPrincipal())
                .orden(imagen.getOrden())
                .build();
    }

    public static EspecificacionTecnicaDto toEspecificacionDto(EspecificacionTecnica especificacion) {
        if (especificacion == null) {
            return null;
        }
        return EspecificacionTecnicaDto.builder()
                .idEspecificacion(especificacion.getIdEspecificacion())
                .atributo(especificacion.getAtributo())
                .valor(especificacion.getValor())
                .unidadMedida(especificacion.getUnidadMedida())
                .valorConUnidad(especificacion.valorConUnidad())
                .build();
    }

    public static TarifaAlquilerDto toTarifaDto(TarifaAlquiler tarifa) {
        if (tarifa == null) {
            return null;
        }
        return TarifaAlquilerDto.builder()
                .idTarifa(tarifa.getIdTarifa())
                .tipoTarifa(tarifa.getTipoTarifa() != null ? tarifa.getTipoTarifa().name() : null)
                .descripcionTipo(tarifa.getTipoTarifa() != null
                        ? tarifa.getTipoTarifa().getDescripcion() : null)
                .valor(tarifa.getValor())
                .valorPorDia(tarifa.getTipoTarifa() != null ? tarifa.valorPorDia() : null)
                .activa(tarifa.isActiva())
                .build();
    }
}
