package com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.repository;

import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.RolJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Repositorio Spring Data de la tabla roles. */
public interface SpringDataRolRepository extends JpaRepository<RolJpaEntity, Long> {

    Optional<RolJpaEntity> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
