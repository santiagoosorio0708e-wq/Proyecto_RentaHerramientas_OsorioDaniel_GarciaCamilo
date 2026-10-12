/* ==========================================================================
   reservas.js - Gestión de reservas, pagos y facturas para el cliente
   Autor: Daniel Osorio (DEV 2)
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

    'use strict';

    if (!UI.exigirSesion()) return;
    UI.pintarBarra('reservas');

    const tablaReservas = document.getElementById('tabla-reservas');
    const formPago      = document.getElementById('form-pago');
    const btnRefrescar  = document.getElementById('btn-refrescar');

    let reservasCache = [];

    cargarMisReservas();

    btnRefrescar.addEventListener('click', cargarMisReservas);

    // Cierre de modales
    document.querySelectorAll('[data-cerrar]').forEach(boton => {
        boton.addEventListener('click', () => UI.cerrarModal(boton.dataset.cerrar));
    });

    // ------------------------------------------------------------------
    // Listado de Reservas
    // ------------------------------------------------------------------

    async function cargarMisReservas() {
        UI.cargando(tablaReservas, 'Cargando sus reservas...');
        UI.ocultarMensaje('mensaje');

        try {
            const listado = await API.reservas.misReservas();
            reservasCache = listado || [];
            pintarReservas(reservasCache);
        } catch (error) {
            tablaReservas.innerHTML = '';
            UI.mensaje('mensaje', 'Error al cargar reservas: ' + error.message, 'error');
        }
    }

    function pintarReservas(reservas) {
        if (!reservas || reservas.length === 0) {
            tablaReservas.innerHTML = `
                <tr>
                    <td colspan="6" style="text-align:center; padding:32px; color:var(--color-texto-suave);">
                        Aún no tiene reservas registradas. ¡Explore el catálogo y alquile su primera máquina!
                    </td>
                </tr>
            `;
            return;
        }

        tablaReservas.innerHTML = reservas.map(r => {
            const puedePagar = (r.estado === 'APROBADA' || r.estado === 'SOLICITADA');
            const puedeCancelar = (r.estado === 'SOLICITADA');

            return `
                <tr>
                    <td><strong>${UI.esc(r.codigoReserva)}</strong></td>
                    <td>${UI.fecha(r.fechaInicio)} al ${UI.fecha(r.fechaFin)}</td>
                    <td>${UI.insignia(r.estado)}</td>
                    <td><strong>${UI.pesos(r.montoTotalEstimado)}</strong></td>
                    <td>${UI.pesos(r.depositoGarantia)}</td>
                    <td class="acciones-celda">
                        ${puedePagar ? `
                            <button class="boton boton-pequeno btn-pagar" data-id="${r.idReserva}">
                                Pagar
                            </button>
                        ` : ''}
                        <button class="boton boton-secundario boton-pequeno btn-factura" data-id="${r.idReserva}">
                            Ver Factura
                        </button>
                        ${puedeCancelar ? `
                            <button class="boton boton-peligro boton-pequeno btn-cancelar" data-id="${r.idReserva}">
                                Cancelar
                            </button>
                        ` : ''}
                    </td>
                </tr>
            `;
        }).join('');

        // Listeners para botones de acción
        tablaReservas.querySelectorAll('.btn-pagar').forEach(b => {
            b.addEventListener('click', () => abrirModalPago(Number(b.dataset.id)));
        });

        tablaReservas.querySelectorAll('.btn-factura').forEach(b => {
            b.addEventListener('click', () => verFacturaReserva(Number(b.dataset.id)));
        });

        tablaReservas.querySelectorAll('.btn-cancelar').forEach(b => {
            b.addEventListener('click', () => cancelarReserva(Number(b.dataset.id)));
        });
    }

    // ------------------------------------------------------------------
    // Pasarela de Pagos
    // ------------------------------------------------------------------

    function abrirModalPago(idReserva) {
        const reserva = reservasCache.find(r => r.idReserva === idReserva);
        if (!reserva) return;

        document.getElementById('pago-id-reserva').value = idReserva;
        document.getElementById('pago-subtitulo').textContent = `Liquidación para Reserva #${reserva.codigoReserva}`;

        const montoTotal = Number(reserva.montoTotalEstimado) || 0;
        document.getElementById('pago-monto').value = montoTotal;

        actualizarCalculoIva(montoTotal);

        UI.ocultarMensaje('pago-mensaje');
        UI.abrirModal('modal-pago');
    }

    function actualizarCalculoIva(total) {
        // En Colombia IVA = 19%. Base = Total / 1.19
        const subtotal = Math.round(total / 1.19);
        const iva = total - subtotal;

        document.getElementById('prev-subtotal').textContent = UI.pesos(subtotal);
        document.getElementById('prev-iva').textContent = UI.pesos(iva);
        document.getElementById('prev-total').textContent = UI.pesos(total);
    }

    formPago.addEventListener('submit', async (e) => {
        e.preventDefault();
        UI.ocultarMensaje('pago-mensaje');

        const idReserva = Number(document.getElementById('pago-id-reserva').value);
        const idMetodoPago = Number(document.getElementById('pago-metodo').value);
        const tipoPago = document.getElementById('pago-tipo').value;
        const monto = Number(document.getElementById('pago-monto').value);

        const btn = document.getElementById('btn-confirmar-pago');
        btn.disabled = true;
        btn.textContent = 'Procesando en pasarela...';

        try {
            const comando = {
                idReserva,
                idMetodoPago,
                tipoPago,
                monto
            };

            const respuesta = await API.pagos.procesar(comando);
            UI.cerrarModal('modal-pago');
            UI.mensaje('mensaje', `¡Pago exitoso! Transacción: ${respuesta.numeroTransaccion}. Factura emitida automáticamente.`, 'exito');
            await cargarMisReservas();
        } catch (error) {
            UI.mensaje('pago-mensaje', 'Error al procesar pago: ' + error.message, 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Confirmar y Pagar';
        }
    });

    // ------------------------------------------------------------------
    // Facturación Electrónica (Desglose 19% IVA)
    // ------------------------------------------------------------------

    async function verFacturaReserva(idReserva) {
        const contenedor = document.getElementById('factura-cuerpo');
        UI.cargando(contenedor, 'Consultando factura electrónica...');
        UI.abrirModal('modal-factura');

        try {
            // Buscamos pagos de la reserva
            const pagos = await API.pagos.porReserva(idReserva);
            if (!pagos || pagos.length === 0) {
                contenedor.innerHTML = `
                    <div class="estado-vacio">
                        <div class="icono">ℹ️</div>
                        <p>No se ha registrado ningún pago para esta reserva aún. Por favor realice el pago para generar su factura legal.</p>
                    </div>
                `;
                return;
            }

            const ultimoPago = pagos[0];
            const factura = await API.facturas.porPago(ultimoPago.idPago);

            renderizarFactura(factura);
        } catch (error) {
            contenedor.innerHTML = `<div class="mensaje mensaje-error visible">Error al consultar factura: ${UI.esc(error.message)}</div>`;
        }
    }

    function renderizarFactura(f) {
        const contenedor = document.getElementById('factura-cuerpo');

        const detallesHtml = (f.detalles || []).map(d => `
            <tr>
                <td>${UI.esc(d.descripcionItem)}</td>
                <td style="text-align:center;">${d.cantidad}</td>
                <td style="text-align:right;">${UI.pesos(d.precioUnitario)}</td>
                <td style="text-align:right;"><strong>${UI.pesos(d.subtotalLinea)}</strong></td>
            </tr>
        `).join('');

        contenedor.innerHTML = `
            <div class="caja-factura">
                <div class="factura-header">
                    <div>
                        <h3 style="color:var(--color-acento); margin-bottom:2px;">RENTA TOOLS COLOMBIA S.A.S.</h3>
                        <p style="font-size:.82rem; color:var(--color-texto-suave);">NIT: 901.458.789-4 &middot; IVA Régimen Común</p>
                        <p style="font-size:.82rem; color:var(--color-texto-suave);">Resolución DIAN No. 1876400012</p>
                    </div>
                    <div style="text-align:right;">
                        <span class="insignia insignia-en_curso">FACTURA ELECTRÓNICA</span>
                        <h4 style="margin-top:6px;">No. ${UI.esc(f.numeroFactura)}</h4>
                        <p style="font-size:.8rem; color:var(--color-texto-suave);">${UI.fecha(f.fechaEmision)}</p>
                    </div>
                </div>

                <div class="lista-datos" style="margin-bottom:16px;">
                    <div class="dato">
                        <div class="etiqueta">Cliente ID</div>
                        <div class="valor">#${f.idCliente}</div>
                    </div>
                    <div class="dato">
                        <div class="etiqueta">Estado de Factura</div>
                        <div class="valor"><span class="insignia insignia-aprobada">${UI.esc(f.estadoFactura)}</span></div>
                    </div>
                    <div class="dato">
                        <div class="etiqueta">ID de Pago</div>
                        <div class="valor">#${f.idPago}</div>
                    </div>
                </div>

                <div class="envoltura-tabla">
                    <table>
                        <thead>
                            <tr>
                                <th>Descripción del Ítem</th>
                                <th style="text-align:center;">Cant.</th>
                                <th style="text-align:right;">Valor Unitario</th>
                                <th style="text-align:right;">Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${detallesHtml || `<tr><td colspan="4">Alquiler de maquinaria y herramientas</td></tr>`}
                        </tbody>
                    </table>
                </div>

                <div class="factura-totales">
                    <div class="factura-fila-total">
                        <span>Subtotal (sin impuestos):</span>
                        <strong>${UI.pesos(f.subtotal)}</strong>
                    </div>
                    <div class="factura-fila-total">
                        <span>IVA (19% Legal):</span>
                        <strong>${UI.pesos(f.iva)}</strong>
                    </div>
                    <div class="factura-fila-total gran-total">
                        <span>Total Pagado:</span>
                        <strong>${UI.pesos(f.total)}</strong>
                    </div>
                </div>
            </div>
        `;
    }

    // ------------------------------------------------------------------
    // Cancelación
    // ------------------------------------------------------------------

    async function cancelarReserva(idReserva) {
        if (!confirm('¿Está seguro de que desea cancelar esta reserva?')) return;

        try {
            await API.reservas.cancelar(idReserva);
            UI.mensaje('mensaje', 'Reserva cancelada correctamente', 'exito');
            await cargarMisReservas();
        } catch (error) {
            UI.mensaje('mensaje', 'No se pudo cancelar la reserva: ' + error.message, 'error');
        }
    }
});
