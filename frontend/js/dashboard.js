/* ==========================================================================
   dashboard.js - Dashboard analítico y reportes ejecutivos de negocio
   Autor: Daniel Osorio (DEV 2)
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

    'use strict';

    if (!UI.exigirSesion()) return;
    if (!API.esProveedor() && !API.esAdmin()) {
        window.location.href = 'index.html';
        return;
    }

    UI.pintarBarra('dashboard');

    const btnRefrescar = document.getElementById('btn-refrescar-dashboard');
    btnRefrescar.addEventListener('click', cargarDashboard);

    cargarDashboard();

    async function cargarDashboard() {
        UI.ocultarMensaje('mensaje');

        try {
            const data = await API.reportes.dashboard();
            pintarFinanciero(data.resumenFinanciero);
            pintarOperativas(data.metricasOperativas);
            pintarTopHerramientas(data.topHerramientasPopulares || []);
            pintarTopClientes(data.topClientesFrecuentes || []);
            pintarProveedores(data.ingresosPorProveedor || []);
        } catch (error) {
            UI.mensaje('mensaje', 'Error al consultar métricas del dashboard: ' + error.message, 'error');
        }
    }

    function pintarFinanciero(f) {
        if (!f) return;
        document.getElementById('kpi-total-recaudado').textContent = UI.pesos(f.totalRecaudado);
        document.getElementById('kpi-pagos-exitosos').textContent   = `${f.totalPagosExitosos} pagos exitosos registrados`;

        document.getElementById('kpi-subtotal').textContent        = UI.pesos(f.totalSubtotal);
        document.getElementById('kpi-iva').textContent             = UI.pesos(f.totalIva);
        document.getElementById('kpi-facturas-emitidas').textContent = `${f.totalFacturasEmitidas} facturas electrónicas emitidas`;

        const totalCargos = (Number(f.totalCargosMora) || 0) + (Number(f.totalCargosDano) || 0);
        document.getElementById('kpi-cargos-adicionales').textContent = UI.pesos(totalCargos);
        document.getElementById('kpi-detalle-cargos').textContent = `Mora: ${UI.pesos(f.totalCargosMora)} | Daños: ${UI.pesos(f.totalCargosDano)}`;
    }

    function pintarOperativas(op) {
        if (!op) return;
        document.getElementById('kpi-total-herramientas').textContent = op.totalHerramientas;
        document.getElementById('kpi-disponibles').textContent        = op.herramientasDisponibles;
        document.getElementById('kpi-alquiladas').textContent         = op.herramientasAlquiladas;
        document.getElementById('kpi-mantenimiento').textContent       = op.herramientasEnMantenimiento;

        const tasa = op.tasaIncidenciasDanoPorcentaje !== null && op.tasaIncidenciasDanoPorcentaje !== undefined
            ? op.tasaIncidenciasDanoPorcentaje.toFixed(1)
            : '0.0';
        document.getElementById('kpi-tasa-dano').textContent          = `${tasa}%`;
        document.getElementById('kpi-total-danos').textContent        = `${op.totalReportesDano} daños en ${op.totalDevoluciones} devoluciones`;
    }

    function pintarTopHerramientas(lista) {
        const tbody = document.getElementById('tabla-top-herramientas');
        if (lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" style="text-align:center; padding:18px;">Sin datos aún.</td></tr>`;
            return;
        }

        tbody.innerHTML = lista.map((h, idx) => `
            <tr>
                <td><strong>#${idx + 1} ${UI.esc(h.nombre)}</strong><br><small style="color:var(--color-texto-suave);">${UI.esc(h.marca)}</small></td>
                <td><span class="insignia insignia-info">${UI.esc(h.categoria)}</span></td>
                <td style="text-align:center;"><strong>${h.totalAlquileres}</strong> veces</td>
                <td><strong style="color:var(--color-acento);">${UI.pesos(h.ingresosGenerados)}</strong></td>
            </tr>
        `).join('');
    }

    function pintarTopClientes(lista) {
        const tbody = document.getElementById('tabla-top-clientes');
        if (lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" style="text-align:center; padding:18px;">Sin datos aún.</td></tr>`;
            return;
        }

        tbody.innerHTML = lista.map((c, idx) => `
            <tr>
                <td><strong>#${idx + 1} ${UI.esc(c.nombreCompleto)}</strong></td>
                <td>${UI.esc(c.email)}</td>
                <td style="text-align:center;"><strong>${c.totalReservas}</strong> reservas</td>
                <td><strong style="color:var(--color-exito);">${UI.pesos(c.totalGastado)}</strong></td>
            </tr>
        `).join('');
    }

    function pintarProveedores(lista) {
        const tbody = document.getElementById('tabla-proveedores');
        if (lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" style="text-align:center; padding:18px;">Sin datos de proveedores aún.</td></tr>`;
            return;
        }

        tbody.innerHTML = lista.map(p => `
            <tr>
                <td><strong>${UI.esc(p.nombreProveedor)}</strong> (ID #${p.idProveedor})</td>
                <td style="text-align:center;">${p.totalHerramientasRegistradas} equipos</td>
                <td style="text-align:center;">${p.totalAlquileres} alquileres</td>
                <td><strong style="color:var(--color-acento);">${UI.pesos(p.ingresosGenerados)}</strong></td>
            </tr>
        `).join('');
    }
});
