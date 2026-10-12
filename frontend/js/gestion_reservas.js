/* ==========================================================================
   gestion_reservas.js - Operación de reservas, check-in de devolución y daños
   Autor: Daniel Osorio (DEV 2)
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

    'use strict';

    if (!UI.exigirSesion()) return;
    if (!API.esProveedor() && !API.esAdmin()) {
        window.location.href = 'index.html';
        return;
    }

    UI.pintarBarra('gestion_reservas');

    const tablaReservas      = document.getElementById('tabla-gestion-reservas');
    const tablaDevoluciones  = document.getElementById('tabla-devoluciones');
    const filtroEstado       = document.getElementById('filtro-estado-reserva');
    const btnRefrescar       = document.getElementById('btn-refrescar-todo');
    const formCheckin        = document.getElementById('form-checkin');
    const chkDanio           = document.getElementById('checkin-tiene-dano');
    const seccionDanio       = document.getElementById('seccion-dano');
    const inputFechaReal     = document.getElementById('checkin-fecha-real');

    let reservasCache = [];
    let reservaActualParaCheckin = null;

    cargarTodo();

    btnRefrescar.addEventListener('click', cargarTodo);
    filtroEstado.addEventListener('change', () => aplicarFiltroReservas());

    chkDanio.addEventListener('change', () => {
        seccionDanio.style.display = chkDanio.checked ? 'block' : 'none';
    });

    inputFechaReal.addEventListener('change', evaluarRetrasoVisual);

    // Cierre de modales
    document.querySelectorAll('[data-cerrar]').forEach(b => {
        b.addEventListener('click', () => UI.cerrarModal(b.dataset.cerrar));
    });

    async function cargarTodo() {
        await Promise.all([cargarReservas(), cargarDevoluciones()]);
    }

    // ------------------------------------------------------------------
    // 1. Gestión de Reservas
    // ------------------------------------------------------------------

    async function cargarReservas() {
        UI.cargando(tablaReservas, 'Cargando flujo de alquileres...');
        try {
            const listado = await API.reservas.listar();
            reservasCache = listado || [];
            aplicarFiltroReservas();
        } catch (error) {
            tablaReservas.innerHTML = '';
            UI.mensaje('mensaje', 'Error al consultar reservas: ' + error.message, 'error');
        }
    }

    function aplicarFiltroReservas() {
        const est = filtroEstado.value;
        const filtradas = est
            ? reservasCache.filter(r => r.estado === est)
            : reservasCache;
        pintarReservas(filtradas);
    }

    function pintarReservas(reservas) {
        if (!reservas || reservas.length === 0) {
            tablaReservas.innerHTML = `<tr><td colspan="6" style="text-align:center; padding:24px;">No hay reservas que coincidan con el filtro.</td></tr>`;
            return;
        }

        tablaReservas.innerHTML = reservas.map(r => {
            let botones = '';

            if (r.estado === 'SOLICITADA') {
                botones += `
                    <button class="boton boton-pequeno btn-aprobar" data-id="${r.idReserva}">Aprobar</button>
                    <button class="boton boton-peligro boton-pequeno btn-rechazar" data-id="${r.idReserva}">Rechazar</button>
                `;
            } else if (r.estado === 'APROBADA') {
                botones += `
                    <button class="boton boton-pequeno btn-iniciar" data-id="${r.idReserva}">Despachar Equipo</button>
                `;
            } else if (r.estado === 'EN_CURSO') {
                botones += `
                    <button class="boton boton-pequeno btn-checkin" data-id="${r.idReserva}" style="background:var(--color-exito);">
                        Check-in Devolución
                    </button>
                `;
            }

            return `
                <tr>
                    <td><strong>${UI.esc(r.codigoReserva)}</strong></td>
                    <td>Cliente #${r.idCliente}</td>
                    <td>${UI.fecha(r.fechaInicio)} al ${UI.fecha(r.fechaFin)}</td>
                    <td>${UI.insignia(r.estado)}</td>
                    <td><strong>${UI.pesos(r.montoTotalEstimado)}</strong></td>
                    <td class="acciones-celda">
                        ${botones || '<span style="color:var(--color-texto-suave); font-size:.84rem;">Completada</span>'}
                    </td>
                </tr>
            `;
        }).join('');

        tablaReservas.querySelectorAll('.btn-aprobar').forEach(b => {
            b.addEventListener('click', () => aprobarReserva(Number(b.dataset.id)));
        });

        tablaReservas.querySelectorAll('.btn-rechazar').forEach(b => {
            b.addEventListener('click', () => rechazarReserva(Number(b.dataset.id)));
        });

        tablaReservas.querySelectorAll('.btn-iniciar').forEach(b => {
            b.addEventListener('click', () => iniciarAlquiler(Number(b.dataset.id)));
        });

        tablaReservas.querySelectorAll('.btn-checkin').forEach(b => {
            b.addEventListener('click', () => abrirModalCheckin(Number(b.dataset.id)));
        });
    }

    async function aprobarReserva(id) {
        if (!confirm('¿Aprobar esta solicitud de reserva?')) return;
        try {
            await API.reservas.aprobar(id);
            UI.mensaje('mensaje', 'Reserva aprobada exitosamente', 'exito');
            await cargarReservas();
        } catch (e) {
            UI.mensaje('mensaje', 'Error al aprobar: ' + e.message, 'error');
        }
    }

    async function rechazarReserva(id) {
        const motivo = prompt('Indique el motivo del rechazo:', 'Sin disponibilidad técnica');
        if (motivo === null) return;
        try {
            await API.reservas.rechazar(id, motivo);
            UI.mensaje('mensaje', 'Reserva rechazada', 'aviso');
            await cargarReservas();
        } catch (e) {
            UI.mensaje('mensaje', 'Error al rechazar: ' + e.message, 'error');
        }
    }

    async function iniciarAlquiler(id) {
        if (!confirm('¿Confirmar despacho y entrega de la maquinaria al cliente en obra?')) return;
        try {
            await API.reservas.iniciar(id);
            UI.mensaje('mensaje', 'Alquiler iniciado. Equipo marcado EN CURSO.', 'exito');
            await cargarReservas();
        } catch (e) {
            UI.mensaje('mensaje', 'Error al iniciar alquiler: ' + e.message, 'error');
        }
    }

    // ------------------------------------------------------------------
    // 2. Check-In de Devolución Físico
    // ------------------------------------------------------------------

    function abrirModalCheckin(idReserva) {
        const reserva = reservasCache.find(r => r.idReserva === idReserva);
        if (!reserva) return;

        reservaActualParaCheckin = reserva;
        document.getElementById('checkin-id-reserva').value = idReserva;
        document.getElementById('checkin-subtitulo').textContent = `Check-in de Reserva #${reserva.codigoReserva}`;
        document.getElementById('checkin-fecha-pactada').textContent = UI.fecha(reserva.fechaFin);

        const idsHerramientas = (reserva.detalles || []).map(d => '#' + d.idHerramienta).join(', ');
        document.getElementById('checkin-herramientas-ids').textContent = idsHerramientas || 'Maquinaria asignada';

        // Preseleccionar fecha de hoy
        const hoy = new Date().toISOString().split('T')[0];
        inputFechaReal.value = hoy;
        evaluarRetrasoVisual();

        chkDanio.checked = false;
        seccionDanio.style.display = 'none';

        if (reserva.detalles && reserva.detalles.length > 0) {
            document.getElementById('dano-id-herramienta').value = reserva.detalles[0].idHerramienta;
        }

        UI.ocultarMensaje('checkin-mensaje');
        UI.abrirModal('modal-checkin');
    }

    function evaluarRetrasoVisual() {
        if (!reservaActualParaCheckin || !inputFechaReal.value) return;

        const pactada = new Date(reservaActualParaCheckin.fechaFin);
        const real = new Date(inputFechaReal.value);
        const alerta = document.getElementById('checkin-alerta-retraso');

        if (real > pactada) {
            const diffTime = Math.abs(real - pactada);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            alerta.innerHTML = `<span style="color:var(--color-error); font-weight:700;">⚠️ Retraso detectado de ${diffDays} días. Se calculará cargo por mora automático.</span>`;
        } else {
            alerta.innerHTML = `<span style="color:var(--color-exito);">✅ Entrega a tiempo sin mora.</span>`;
        }
    }

    formCheckin.addEventListener('submit', async (e) => {
        e.preventDefault();
        UI.ocultarMensaje('checkin-mensaje');

        const btn = document.getElementById('btn-guardar-checkin');
        btn.disabled = true;
        btn.textContent = 'Procesando devolución...';

        try {
            const usuario = API.obtenerUsuario();
            const idReserva = Number(document.getElementById('checkin-id-reserva').value);
            const fechaDevolucionReal = inputFechaReal.value;
            const estadoEquipoGeneral = document.getElementById('checkin-estado-general').value;
            const observaciones = document.getElementById('checkin-observaciones').value;

            const reportesDano = [];

            if (chkDanio.checked) {
                const idHerramienta = Number(document.getElementById('dano-id-herramienta').value);
                const tipoDano = document.getElementById('dano-tipo').value;
                const descripcionDano = document.getElementById('dano-descripcion').value;
                const costoEstimadoReparacion = Number(document.getElementById('dano-costo').value) || 0;
                const responsableAsume = document.getElementById('dano-responsable').value;
                const fotoUrl = document.getElementById('dano-foto-url').value;
                const fotoDesc = document.getElementById('dano-foto-desc').value;

                const evidencias = [];
                if (fotoUrl) {
                    evidencias.push({
                        urlFoto: fotoUrl,
                        descripcionFoto: fotoDesc || 'Evidencia fotográfica del daño'
                    });
                }

                reportesDano.push({
                    idHerramienta,
                    tipoDano,
                    descripcionDano,
                    costoEstimadoReparacion,
                    responsableAsume,
                    evidencias
                });
            }

            const comando = {
                idReserva,
                fechaDevolucionReal,
                recibidoPor: usuario ? usuario.idUsuario : 1,
                estadoEquipoGeneral,
                observaciones,
                reportesDano
            };

            await API.devoluciones.registrar(comando);

            UI.cerrarModal('modal-checkin');
            UI.mensaje('mensaje', 'Check-in de devolución registrado exitosamente. La reserva ha sido liquidada.', 'exito');
            await cargarTodo();
        } catch (error) {
            UI.mensaje('checkin-mensaje', 'Error al procesar devolución: ' + error.message, 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Completar Check-In y Liquidar';
        }
    });

    // ------------------------------------------------------------------
    // 3. Historial de Devoluciones Físicas
    // ------------------------------------------------------------------

    async function cargarDevoluciones() {
        UI.cargando(tablaDevoluciones, 'Cargando historial de devoluciones...');
        try {
            const listado = await API.devoluciones.listar();
            pintarDevoluciones(listado || []);
        } catch (e) {
            tablaDevoluciones.innerHTML = '';
            console.warn('Error al cargar devoluciones:', e.message);
        }
    }

    function pintarDevoluciones(lista) {
        if (!lista || lista.length === 0) {
            tablaDevoluciones.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:24px;">No se han registrado devoluciones aún.</td></tr>`;
            return;
        }

        tablaDevoluciones.innerHTML = lista.map(d => {
            const tieneDanos = d.reportesDano && d.reportesDano.length > 0;
            const tieneCargos = d.totalCargosAdicionales > 0;

            return `
                <tr>
                    <td><strong>#DEV-${d.id}</strong></td>
                    <td>Reserva #${d.idReserva}</td>
                    <td>${UI.fecha(d.fechaDevolucionReal)}</td>
                    <td>
                        ${d.diasRetraso > 0
                            ? `<span class="insignia insignia-rechazada">${d.diasRetraso} días mora</span>`
                            : `<span class="insignia insignia-buen_estado">A tiempo</span>`}
                    </td>
                    <td>${UI.insignia(d.estadoEquipoGeneral)}</td>
                    <td>
                        <strong>${UI.pesos(d.totalCargosAdicionales)}</strong>
                        ${tieneCargos ? `<br><small style="color:var(--color-error);">${d.cargosAdicionales.length} cargos</small>` : ''}
                    </td>
                    <td>
                        <button class="boton boton-secundario boton-pequeno btn-ver-devolucion" data-id="${d.id}">
                            ${tieneDanos ? 'Ver Daños (' + d.reportesDano.length + ')' : 'Ver Detalle'}
                        </button>
                    </td>
                </tr>
            `;
        }).join('');

        tablaDevoluciones.querySelectorAll('.btn-ver-devolucion').forEach(b => {
            b.addEventListener('click', () => verDetalleDevolucion(Number(b.dataset.id)));
        });
    }

    async function verDetalleDevolucion(id) {
        const contenedor = document.getElementById('cuerpo-detalle-devolucion');
        UI.cargando(contenedor, 'Cargando inspección y evidencias...');
        UI.abrirModal('modal-detalle-devolucion');

        try {
            const d = await API.devoluciones.obtener(id);

            let danosHtml = '';
            if (d.reportesDano && d.reportesDano.length > 0) {
                danosHtml = d.reportesDano.map(r => {
                    const fotos = (r.evidencias || []).map(f => `
                        <div class="tarjeta-evidencia">
                            <img src="${UI.esc(f.urlFoto)}" alt="Evidencia" onerror="this.src='https://placehold.co/140x100?text=Foto+Averia'">
                            <p>${UI.esc(f.descripcionFoto || 'Foto avería')}</p>
                        </div>
                    `).join('');

                    return `
                        <div style="background:var(--color-superficie); padding:14px; border-radius:var(--radio-sm); margin-bottom:12px; border:1px solid var(--color-borde);">
                            <div class="fila-encabezado" style="margin-bottom:6px;">
                                <strong>Herramienta #${r.idHerramienta} &middot; ${UI.esc(r.tipoDano)}</strong>
                                <span class="insignia insignia-rechazada">${UI.pesos(r.costoEstimadoReparacion)}</span>
                            </div>
                            <p style="font-size:.88rem; margin-bottom:8px;">${UI.esc(r.descripcionDano)}</p>
                            <p style="font-size:.78rem; color:var(--color-texto-suave);">Responsable: <strong>${UI.esc(r.responsableAsume)}</strong></p>
                            ${fotos ? `<h5 style="margin-top:10px; font-size:.8rem;">Evidencias Fotográficas (4FN):</h5><div class="galeria-evidencias">${fotos}</div>` : ''}
                        </div>
                    `;
                }).join('');
            } else {
                danosHtml = `<p style="color:var(--color-exito);">✅ Equipo devuelto sin averías reportadas.</p>`;
            }

            let cargosHtml = '';
            if (d.cargosAdicionales && d.cargosAdicionales.length > 0) {
                cargosHtml = `
                    <h4 style="margin:16px 0 8px;">Cargos Adicionales Liquidados:</h4>
                    <div class="envoltura-tabla">
                        <table>
                            <thead><tr><th>Concepto</th><th>Monto</th><th>Estado</th><th>Acción</th></tr></thead>
                            <tbody>
                                ${d.cargosAdicionales.map(c => `
                                    <tr>
                                        <td>${UI.esc(c.descripcion || c.tipoCargo)}</td>
                                        <td><strong>${UI.pesos(c.monto)}</strong></td>
                                        <td>${c.pagado ? '<span class="insignia insignia-buen_estado">PAGADO</span>' : '<span class="insignia insignia-rechazada">PENDIENTE</span>'}</td>
                                        <td>
                                            ${!c.pagado ? `<button class="boton boton-pequeno btn-pagar-cargo" data-id="${c.id}">Marcar Pagado</button>` : '-'}
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                `;
            }

            contenedor.innerHTML = `
                <div class="lista-datos" style="margin-bottom:16px;">
                    <div class="dato"><div class="etiqueta">ID Devolución</div><div class="valor">#${d.id}</div></div>
                    <div class="dato"><div class="etiqueta">Reserva</div><div class="valor">#${d.idReserva}</div></div>
                    <div class="dato"><div class="etiqueta">Retraso</div><div class="valor">${d.diasRetraso} días</div></div>
                    <div class="dato"><div class="etiqueta">Estado General</div><div class="valor">${UI.insignia(d.estadoEquipoGeneral)}</div></div>
                </div>
                ${d.observaciones ? `<p style="margin-bottom:14px; font-size:.9rem; color:var(--color-texto-suave);"><strong>Observaciones:</strong> ${UI.esc(d.observaciones)}</p>` : ''}
                <h4 style="margin-bottom:10px;">Informe de Averías Registrado:</h4>
                ${danosHtml}
                ${cargosHtml}
            `;

            contenedor.querySelectorAll('.btn-pagar-cargo').forEach(b => {
                b.addEventListener('click', async () => {
                    try {
                        await API.devoluciones.pagarCargo(Number(b.dataset.id));
                        alert('Cargo marcado como pagado exitosamente');
                        await verDetalleDevolucion(id);
                        await cargarDevoluciones();
                    } catch (err) {
                        alert('Error al liquidar cargo: ' + err.message);
                    }
                });
            });

        } catch (err) {
            contenedor.innerHTML = `<div class="mensaje mensaje-error visible">Error: ${UI.esc(err.message)}</div>`;
        }
    }
});
