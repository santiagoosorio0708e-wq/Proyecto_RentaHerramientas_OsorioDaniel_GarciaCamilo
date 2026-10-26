package com.rentaherramientas;

import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.RolJpaEntity;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.repository.SpringDataRolRepository;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.repository.SpringDataUsuarioRepository;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.CategoriaJpaEntity;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.HerramientaJpaEntity;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.repository.SpringDataCategoriaRepository;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.repository.SpringDataHerramientaRepository;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity.FacturaJpaEntity;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.repository.SpringDataFacturaRepository;
import com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.repository.SpringDataMetodoPagoRepository;
import com.rentaherramientas.modules.reservas.domain.model.EstadoReserva;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity.ReservaJpaEntity;
import com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.repository.SpringDataReservaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:sqlite:target/test_sqlite.db",
        "spring.datasource.driver-class-name=org.sqlite.JDBC",
        "spring.datasource.username=",
        "spring.datasource.password=",
        "spring.datasource.hikari.maximum-pool-size=1",
        "spring.datasource.hikari.connection-init-sql=PRAGMA journal_mode=WAL; PRAGMA foreign_keys=ON; PRAGMA busy_timeout=30000;",
        "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
        "spring.jpa.hibernate.ddl-auto=update"
})
class SQLitePersistenceTest {

    @Autowired
    private SpringDataRolRepository rolRepository;

    @Autowired
    private SpringDataUsuarioRepository usuarioRepository;

    @Autowired
    private SpringDataCategoriaRepository categoriaRepository;

    @Autowired
    private SpringDataHerramientaRepository herramientaRepository;

    @Autowired
    private SpringDataMetodoPagoRepository metodoPagoRepository;

    @Autowired
    private SpringDataReservaRepository reservaRepository;

    @Autowired
    private SpringDataFacturaRepository facturaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Verificar que SQLite crea el archivo físico, siembra datos iniciales y persiste modificaciones")
    void testSQLiteDatabaseCreationAndPersistence() {
        // 1. Archivo físico creado en disco
        File dbFile = new File("target/test_sqlite.db");
        assertTrue(dbFile.exists(), "El archivo físico SQLite debe existir en target/test_sqlite.db");
        assertTrue(dbFile.length() > 0, "El archivo SQLite no debe estar vacío");

        // 2. Sembrado automático de datos maestros
        assertTrue(rolRepository.count() >= 3, "Deben existir al menos los roles ADMIN, PROVEEDOR y CLIENTE");
        assertTrue(usuarioRepository.count() >= 4, "Deben existir los usuarios preconfigurados");
        assertEquals(20, categoriaRepository.count(), "Deben existir las 20 categorías del catálogo");
        assertEquals(20, herramientaRepository.count(), "Deben existir las 20 herramientas maestras");
        assertTrue(metodoPagoRepository.count() >= 4, "Deben existir los 4 métodos de pago principales");
        assertTrue(reservaRepository.count() >= 3, "Deben existir las reservas iniciales de prueba");

        // 3. Verificación de credenciales BCrypt
        Optional<UsuarioJpaEntity> cliente = usuarioRepository.findByEmailIgnoreCase("cliente@rentatools.com");
        assertTrue(cliente.isPresent(), "El usuario cliente@rentatools.com debe existir");
        assertTrue(passwordEncoder.matches("Password123", cliente.get().getPasswordHash()),
                "La contraseña Password123 debe ser válida con el hash BCrypt");

        // 4. Verificación de Facturación con IVA 19%
        Optional<FacturaJpaEntity> factura = facturaRepository.findByNumeroFactura("FACT-2026-000101");
        assertTrue(factura.isPresent(), "La factura FACT-2026-000101 debe estar emitida");
        assertEquals(0, new BigDecimal("375000").compareTo(factura.get().getTotal()), "El total de la factura debe coincidir");
        assertTrue(factura.get().getIva().compareTo(BigDecimal.ZERO) > 0, "El IVA del 19% debe ser mayor a 0");

        // 5. Permanencia de Datos: Modificación y Creación en Tiempo de Ejecución
        String codigoDinamico = "RES-TEST-" + System.currentTimeMillis();
        ReservaJpaEntity nuevaReserva = ReservaJpaEntity.builder()
                .codigoReserva(codigoDinamico)
                .idCliente(cliente.get().getIdUsuario())
                .fechaSolicitud(LocalDateTime.now())
                .fechaInicio(LocalDate.now().plusDays(2))
                .fechaFin(LocalDate.now().plusDays(6))
                .estado(EstadoReserva.APROBADA)
                .montoTotalEstimado(BigDecimal.valueOf(250000))
                .depositoGarantia(BigDecimal.valueOf(100000))
                .notasCliente("Prueba de persistencia transaccional en SQLite")
                .build();

        ReservaJpaEntity guardada = reservaRepository.save(nuevaReserva);
        assertNotNull(guardada.getIdReserva(), "Debe asignar id autoincremental en SQLite");

        // Leerla nuevamente de la base de datos
        Optional<ReservaJpaEntity> recuperada = reservaRepository.findById(guardada.getIdReserva());
        assertTrue(recuperada.isPresent(), "La reserva modificada debe persistir en el archivo SQLite");
        assertEquals(codigoDinamico, recuperada.get().getCodigoReserva());
        assertEquals(EstadoReserva.APROBADA, recuperada.get().getEstado());

        // Actualizar estado (simulando cambio desde la web)
        recuperada.get().setEstado(EstadoReserva.EN_CURSO);
        reservaRepository.save(recuperada.get());

        ReservaJpaEntity actualizada = reservaRepository.findById(guardada.getIdReserva()).orElseThrow();
        assertEquals(EstadoReserva.EN_CURSO, actualizada.getEstado(),
                "El cambio de estado realizado debe persistir permanentemente en SQLite");
    }
}
