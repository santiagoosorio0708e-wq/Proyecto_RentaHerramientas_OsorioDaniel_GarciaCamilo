package com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.inventario.domain.model.EstadoHerramienta;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.HerramientaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Repositorio Spring Data de la tabla herramientas. */
public interface SpringDataHerramientaRepository extends JpaRepository<HerramientaJpaEntity, Long> {

    Optional<HerramientaJpaEntity> findByNumeroSerieIgnoreCase(String numeroSerie);

    boolean existsByNumeroSerieIgnoreCase(String numeroSerie);

    List<HerramientaJpaEntity> findByIdProveedorOrderByIdHerramientaAsc(Long idProveedor);

    List<HerramientaJpaEntity> findByEstadoOperativoOrderByIdHerramientaAsc(EstadoHerramienta estado);

    /** Busca tanto por la categoria principal como por las categorias adicionales. */
    @Query("""
            SELECT DISTINCT h
            FROM HerramientaJpaEntity h
            LEFT JOIN h.categorias c
            WHERE h.categoriaPrincipal.idCategoria = :idCategoria
               OR c.idCategoria = :idCategoria
            ORDER BY h.idHerramienta ASC
            """)
    List<HerramientaJpaEntity> buscarPorCategoria(@Param("idCategoria") Long idCategoria);

    /**
     * Busqueda del catalogo con filtros opcionales.
     *
     * El patron ":parametro IS NULL OR condicion" permite que un mismo query
     * sirva aunque el usuario no envie todos los filtros: si el parametro llega
     * en null esa condicion se cumple siempre y no filtra nada.
     *
     * Para el estado se usa una bandera booleana aparte en vez de comparar el
     * enum contra NULL. Hibernate necesita conocer el tipo del parametro y un
     * enum en null puede dar problemas de inferencia; con la bandera el enum
     * siempre llega con un valor concreto.
     */
    @Query("""
            SELECT DISTINCT h
            FROM HerramientaJpaEntity h
            LEFT JOIN h.categorias c
            WHERE (:texto IS NULL
                   OR LOWER(h.nombre)  LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(h.marca)   LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(h.modelo)  LIKE LOWER(CONCAT('%', :texto, '%')))
              AND (:idCategoria IS NULL
                   OR h.categoriaPrincipal.idCategoria = :idCategoria
                   OR c.idCategoria = :idCategoria)
              AND (:precioMinimo IS NULL OR h.tarifaDiaria >= :precioMinimo)
              AND (:precioMaximo IS NULL OR h.tarifaDiaria <= :precioMaximo)
              AND (:filtrarEstado = FALSE OR h.estadoOperativo = :estado)
            ORDER BY h.idHerramienta ASC
            """)
    List<HerramientaJpaEntity> buscarPorFiltros(
            @Param("texto") String texto,
            @Param("idCategoria") Long idCategoria,
            @Param("precioMinimo") BigDecimal precioMinimo,
            @Param("precioMaximo") BigDecimal precioMaximo,
            @Param("filtrarEstado") boolean filtrarEstado,
            @Param("estado") EstadoHerramienta estado);
}
