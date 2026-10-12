package com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.CategoriaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/** Repositorio Spring Data de la tabla categorias. */
public interface SpringDataCategoriaRepository extends JpaRepository<CategoriaJpaEntity, Long> {

    Optional<CategoriaJpaEntity> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Cuenta cuantas herramientas usan la categoria, ya sea como principal o
     * dentro de su lista de categorias adicionales. Se usa para no permitir
     * borrar una categoria que todavia esta en uso.
     */
    @Query("""
            SELECT COUNT(DISTINCT h.idHerramienta)
            FROM HerramientaJpaEntity h
            LEFT JOIN h.categorias c
            WHERE h.categoriaPrincipal.idCategoria = :idCategoria
               OR c.idCategoria = :idCategoria
            """)
    long contarHerramientasAsociadas(@Param("idCategoria") Long idCategoria);
}
