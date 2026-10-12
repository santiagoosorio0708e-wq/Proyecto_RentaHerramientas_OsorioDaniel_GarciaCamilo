package com.rentaherramientas.modules.reportes.infrastructure.adapters.out.persistence;

import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.repository.SpringDataUsuarioRepository;
import com.rentaherramientas.modules.inventario.domain.model.EstadoHerramienta;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.entity.HerramientaJpaEntity;
import com.rentaherramientas.modules.inventario.infrastructure.adapters.out.persistence.repository.SpringDataHerramientaRepository;
import com.rentaherramientas.modules.pagos.domain.model.EstadoPago;
import com.rentaherramientas.modules.reportes.domain.model.ClienteFrecuente;
import com.rentaherramientas.modules.reportes.domain.model.HerramientaPopularidad;
import com.rentaherramientas.modules.reportes.domain.model.IngresoPorProveedor;
import com.rentaherramientas.modules.reportes.domain.model.MetricasOperativas;
import com.rentaherramientas.modules.reportes.domain.model.ResumenFinanciero;
import com.rentaherramientas.modules.reportes.domain.ports.out.ReporteRepositoryPort;
import com.rentaherramientas.modules.reservas.domain.model.EstadoReserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportePersistenceAdapter implements ReporteRepositoryPort {

    @PersistenceContext
    private final EntityManager entityManager;

    private final SpringDataHerramientaRepository herramientaRepository;
    private final SpringDataUsuarioRepository usuarioRepository;

    @Override
    public ResumenFinanciero consultarResumenFinanciero() {
        BigDecimal totalRecaudado = entityManager.createQuery(
                "SELECT COALESCE(SUM(p.monto), 0) FROM PagoJpaEntity p WHERE p.estadoPago = :estado", BigDecimal.class)
                .setParameter("estado", EstadoPago.APROBADO)
                .getSingleResult();

        BigDecimal totalSubtotal = entityManager.createQuery(
                "SELECT COALESCE(SUM(f.subtotal), 0) FROM FacturaJpaEntity f", BigDecimal.class)
                .getSingleResult();

        BigDecimal totalIva = entityManager.createQuery(
                "SELECT COALESCE(SUM(f.iva), 0) FROM FacturaJpaEntity f", BigDecimal.class)
                .getSingleResult();

        BigDecimal totalCargosMora = entityManager.createQuery(
                "SELECT COALESCE(SUM(c.monto), 0) FROM CargoAdicionalJpaEntity c WHERE c.tipoCargo = com.rentaherramientas.modules.devoluciones.domain.model.TipoCargo.MORA_RETRASO", BigDecimal.class)
                .getSingleResult();

        BigDecimal totalCargosDano = entityManager.createQuery(
                "SELECT COALESCE(SUM(c.monto), 0) FROM CargoAdicionalJpaEntity c WHERE c.tipoCargo IN (com.rentaherramientas.modules.devoluciones.domain.model.TipoCargo.REPARACION_DANO, com.rentaherramientas.modules.devoluciones.domain.model.TipoCargo.REPOSICION_EQUIPO)", BigDecimal.class)
                .getSingleResult();

        Long totalFacturas = entityManager.createQuery(
                "SELECT COUNT(f) FROM FacturaJpaEntity f", Long.class)
                .getSingleResult();

        Long totalPagos = entityManager.createQuery(
                "SELECT COUNT(p) FROM PagoJpaEntity p WHERE p.estadoPago = :estado", Long.class)
                .setParameter("estado", EstadoPago.APROBADO)
                .getSingleResult();

        return ResumenFinanciero.builder()
                .totalRecaudado(totalRecaudado)
                .totalSubtotal(totalSubtotal)
                .totalIva(totalIva)
                .totalCargosMora(totalCargosMora)
                .totalCargosDano(totalCargosDano)
                .totalFacturasEmitidas(totalFacturas)
                .totalPagosExitosos(totalPagos)
                .build();
    }

    @Override
    public List<HerramientaPopularidad> consultarTopHerramientasPopulares(int limite) {
        List<Object[]> rows = entityManager.createQuery(
                "SELECT d.idHerramienta, COUNT(d.idDetalleReserva), COALESCE(SUM(d.subtotal), 0) " +
                        "FROM DetalleReservaJpaEntity d " +
                        "GROUP BY d.idHerramienta " +
                        "ORDER BY COUNT(d.idDetalleReserva) DESC", Object[].class)
                .setMaxResults(limite > 0 ? limite : 10)
                .getResultList();

        List<HerramientaPopularidad> resultado = new ArrayList<>();
        for (Object[] row : rows) {
            Long idHerramienta = (Long) row[0];
            Long totalAlquileres = (Long) row[1];
            BigDecimal ingresos = (BigDecimal) row[2];

            String nombre = "Herramienta #" + idHerramienta;
            String marca = "N/A";
            String categoria = "General";

            var optionalH = herramientaRepository.findById(idHerramienta);
            if (optionalH.isPresent()) {
                HerramientaJpaEntity h = optionalH.get();
                nombre = h.getNombre();
                marca = h.getMarca();
                if (h.getCategoriaPrincipal() != null) {
                    categoria = h.getCategoriaPrincipal().getNombre();
                }
            }

            resultado.add(HerramientaPopularidad.builder()
                    .idHerramienta(idHerramienta)
                    .nombre(nombre)
                    .marca(marca)
                    .categoria(categoria)
                    .totalAlquileres(totalAlquileres)
                    .ingresosGenerados(ingresos)
                    .build());
        }
        return resultado;
    }

    @Override
    public List<ClienteFrecuente> consultarTopClientesFrecuentes(int limite) {
        List<Object[]> rows = entityManager.createQuery(
                "SELECT r.idCliente, COUNT(r.idReserva), COALESCE(SUM(r.montoTotalEstimado), 0) " +
                        "FROM ReservaJpaEntity r " +
                        "GROUP BY r.idCliente " +
                        "ORDER BY COUNT(r.idReserva) DESC", Object[].class)
                .setMaxResults(limite > 0 ? limite : 10)
                .getResultList();

        List<ClienteFrecuente> resultado = new ArrayList<>();
        for (Object[] row : rows) {
            Long idCliente = (Long) row[0];
            Long totalReservas = (Long) row[1];
            BigDecimal totalGastado = (BigDecimal) row[2];

            String nombreCompleto = "Cliente #" + idCliente;
            String email = "cliente" + idCliente + "@rentatool.com";

            var optionalU = usuarioRepository.findById(idCliente);
            if (optionalU.isPresent()) {
                UsuarioJpaEntity u = optionalU.get();
                nombreCompleto = u.getNombreCompleto();
                email = u.getEmail();
            }

            resultado.add(ClienteFrecuente.builder()
                    .idCliente(idCliente)
                    .nombreCompleto(nombreCompleto)
                    .email(email)
                    .totalReservas(totalReservas)
                    .totalGastado(totalGastado)
                    .build());
        }
        return resultado;
    }

    @Override
    public MetricasOperativas consultarMetricasOperativas() {
        Long totalHerramientas = entityManager.createQuery("SELECT COUNT(h) FROM HerramientaJpaEntity h", Long.class).getSingleResult();
        Long disponibles = entityManager.createQuery("SELECT COUNT(h) FROM HerramientaJpaEntity h WHERE h.estadoOperativo = :st", Long.class)
                .setParameter("st", EstadoHerramienta.DISPONIBLE).getSingleResult();
        Long alquiladas = entityManager.createQuery("SELECT COUNT(h) FROM HerramientaJpaEntity h WHERE h.estadoOperativo = :st", Long.class)
                .setParameter("st", EstadoHerramienta.ALQUILADA).getSingleResult();
        Long mantenimiento = entityManager.createQuery("SELECT COUNT(h) FROM HerramientaJpaEntity h WHERE h.estadoOperativo = :st", Long.class)
                .setParameter("st", EstadoHerramienta.EN_MANTENIMIENTO).getSingleResult();
        Long fueraServicio = entityManager.createQuery("SELECT COUNT(h) FROM HerramientaJpaEntity h WHERE h.estadoOperativo = :st", Long.class)
                .setParameter("st", EstadoHerramienta.FUERA_DE_SERVICIO).getSingleResult();

        Long totalReservas = entityManager.createQuery("SELECT COUNT(r) FROM ReservaJpaEntity r", Long.class).getSingleResult();
        Long reservasFinalizadas = entityManager.createQuery("SELECT COUNT(r) FROM ReservaJpaEntity r WHERE r.estado = :st", Long.class)
                .setParameter("st", EstadoReserva.FINALIZADA).getSingleResult();

        Long totalDevoluciones = entityManager.createQuery("SELECT COUNT(d) FROM DevolucionJpaEntity d", Long.class).getSingleResult();
        Long devolucionesConRetraso = entityManager.createQuery("SELECT COUNT(d) FROM DevolucionJpaEntity d WHERE d.diasRetraso > 0", Long.class).getSingleResult();
        Long totalReportesDano = entityManager.createQuery("SELECT COUNT(rd) FROM ReporteDanoJpaEntity rd", Long.class).getSingleResult();

        double tasaIncidencias = 0.0;
        if (totalDevoluciones > 0) {
            tasaIncidencias = BigDecimal.valueOf((totalReportesDano * 100.0) / totalDevoluciones)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return MetricasOperativas.builder()
                .totalHerramientas(totalHerramientas)
                .herramientasDisponibles(disponibles)
                .herramientasAlquiladas(alquiladas)
                .herramientasEnMantenimiento(mantenimiento)
                .herramientasFueraServicio(fueraServicio)
                .totalReservas(totalReservas)
                .reservasFinalizadas(reservasFinalizadas)
                .totalDevoluciones(totalDevoluciones)
                .devolucionesConRetraso(devolucionesConRetraso)
                .totalReportesDano(totalReportesDano)
                .tasaIncidenciasDanoPorcentaje(tasaIncidencias)
                .build();
    }

    @Override
    public List<IngresoPorProveedor> consultarIngresosPorProveedor() {
        List<Object[]> rows = entityManager.createQuery(
                "SELECT h.idProveedor, COUNT(DISTINCT h.idHerramienta), COUNT(d.idDetalleReserva), COALESCE(SUM(d.subtotal), 0) " +
                        "FROM HerramientaJpaEntity h " +
                        "LEFT JOIN DetalleReservaJpaEntity d ON d.idHerramienta = h.idHerramienta " +
                        "GROUP BY h.idProveedor " +
                        "ORDER BY COALESCE(SUM(d.subtotal), 0) DESC", Object[].class)
                .getResultList();

        List<IngresoPorProveedor> resultado = new ArrayList<>();
        for (Object[] row : rows) {
            Long idProveedor = (Long) row[0];
            Long totalHerramientas = (Long) row[1];
            Long totalAlquileres = (Long) row[2];
            BigDecimal ingresos = (BigDecimal) row[3];

            String nombre = "Proveedor #" + idProveedor;
            var optionalU = usuarioRepository.findById(idProveedor);
            if (optionalU.isPresent()) {
                nombre = optionalU.get().getNombreCompleto();
            }

            resultado.add(IngresoPorProveedor.builder()
                    .idProveedor(idProveedor)
                    .nombreProveedor(nombre)
                    .totalHerramientasRegistradas(totalHerramientas)
                    .totalAlquileres(totalAlquileres)
                    .ingresosGenerados(ingresos)
                    .build());
        }
        return resultado;
    }
}
