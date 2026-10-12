package com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.identidad.domain.model.valueobjects.TipoDocumento;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** Repositorio Spring Data de la tabla usuarios. */
public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    Optional<UsuarioJpaEntity> findByEmailIgnoreCase(String email);

    Optional<UsuarioJpaEntity> findByTipoDocumentoAndNumeroDocumento(
            TipoDocumento tipoDocumento, String numeroDocumento);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByTipoDocumentoAndNumeroDocumento(
            TipoDocumento tipoDocumento, String numeroDocumento);

    /**
     * Consulta JPQL que navega la relacion usuario -> roles para filtrar por el
     * nombre del rol. DISTINCT evita filas repetidas cuando el usuario tiene
     * varios roles.
     */
    @Query("""
            SELECT DISTINCT u
            FROM UsuarioJpaEntity u
            JOIN u.roles r
            WHERE UPPER(r.nombre) = UPPER(:nombreRol)
            ORDER BY u.idUsuario ASC
            """)
    List<UsuarioJpaEntity> buscarPorNombreRol(@Param("nombreRol") String nombreRol);
}
