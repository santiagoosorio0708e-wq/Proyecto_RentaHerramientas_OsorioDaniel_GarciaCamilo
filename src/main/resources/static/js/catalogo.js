/* ==========================================================================
   catalogo.js - Lógica interactiva del catálogo, cotizador y filtros
   Autores: Camilo Garcia & Daniel Osorio
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

    'use strict';

    UI.pintarBarra('catalogo');

    const formFiltros = document.getElementById('form-filtros');
    const contenedor  = document.getElementById('resultados');
    const titulo      = document.getElementById('titulo-resultados');
    const pildorasContenedor = document.getElementById('contenedor-pildoras');

    let herramientaActual = null;

    cargarCategorias();
    buscar();

    // Eventos de formulario
    formFiltros.addEventListener('submit', (e) => {
        e.preventDefault();
        buscar();
    });

    document.getElementById('btn-limpiar').addEventListener('click', () => {
        formFiltros.reset();
        actualizarPildoraActiva('');
        buscar();
    });

    // Píldoras de Categoría rápida
    if (pildorasContenedor) {
        pildorasContenedor.querySelectorAll('.pildora-cat').forEach(btn => {
            btn.addEventListener('click', () => {
                const idCat = btn.dataset.cat;
                const select = document.getElementById('idCategoria');
                if (select) select.value = idCat;
                actualizarPildoraActiva(idCat);
                buscar();
            });
        });
    }

    function actualizarPildoraActiva(idCat) {
        if (!pildorasContenedor) return;
        pildorasContenedor.querySelectorAll('.pildora-cat').forEach(b => {
            if (b.dataset.cat === String(idCat)) {
                b.classList.add('activa');
            } else {
                b.classList.remove('activa');
            }
        });
    }

    // Cierre de los modales
    document.querySelectorAll('[data-cerrar]').forEach(boton => {
        boton.addEventListener('click', () => UI.cerrarModal(boton.dataset.cerrar));
    });

    // ------------------------------------------------------------------
    // Carga de Datos y Búsqueda
    // ------------------------------------------------------------------

    async function cargarCategorias() {
        try {
            const categorias = await API.categorias.listar();
            const select = document.getElementById('idCategoria');
            if (!select) return;
            select.innerHTML = '<option value="">Todas las categorías</option>';
            categorias.forEach(c => {
                const opcion = document.createElement('option');
                opcion.value = c.idCategoria;
                opcion.textContent = c.nombre;
                select.appendChild(opcion);
            });
        } catch (e) {
            console.warn('Categorías no cargadas del backend:', e.message);
        }
    }

    async function buscar() {
        UI.cargando(contenedor, 'Consultando catálogo de maquinaria...');
        UI.ocultarMensaje('mensaje');

        const filtros = UI.leerFormulario(formFiltros);

        try {
            const herramientas = await API.catalogo.buscar(filtros);
            pintar(herramientas || []);
        } catch (error) {
            contenedor.innerHTML = '';
            UI.mensaje('mensaje', error.message, 'error');
        }
    }

    function pintar(lista) {
        titulo.textContent = 'Equipos Disponibles (' + lista.length + ')';

        if (lista.length === 0) {
            UI.vacio(contenedor, 'No se encontraron equipos con los criterios especificados. Intente con otros filtros.', '🚜');
            return;
        }

        const imagenFallback = 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=800&q=80';

        contenedor.innerHTML = lista.map(h => {
            const foto = h.imagenPrincipal || imagenFallback;
            const ubicacion = h.ubicacion || 'Centro de Despacho Principal';

            return `
            <article class="tarjeta-herramienta" data-id="${h.idHerramienta}">
                <div class="imagen-herramienta">
                    <img src="${UI.esc(foto)}" alt="${UI.esc(h.nombre)}"
                         onerror="this.onerror=null; this.src='${imagenFallback}';">
                    <div class="badge-flotante">
                        ${UI.insignia(h.estadoOperativo)}
                    </div>
                </div>
                <div class="cuerpo-tarjeta">
                    <span class="categoria-tag">${UI.esc(h.categoriaPrincipal || 'Equipo Especializado')}</span>
                    <h3>${UI.esc(h.nombre)}</h3>
                    <p class="marca-modelo">${UI.esc(h.marca)} &middot; Mod. ${UI.esc(h.modelo)}</p>
                    <p class="marca-modelo" style="font-size:0.8rem; color:#94a3b8;">📍 ${UI.esc(ubicacion)}</p>

                    <div class="precio-caja">
                        <div class="precio">${UI.pesos(h.tarifaDiaria)} <small>/ día</small></div>
                        <span style="font-size:0.75rem; color:#94a3b8; font-weight:600;">+ IVA 19%</span>
                    </div>

                    <div class="botones-tarjeta">
                        <button class="boton boton-pequeno btn-alquilar-directo" data-id="${h.idHerramienta}">
                            Alquilar
                        </button>
                        <button class="boton boton-secundario boton-pequeno btn-detalle" data-id="${h.idHerramienta}">
                            Ver Ficha
                        </button>
                    </div>
                </div>
            </article>
            `;
        }).join('');

        contenedor.querySelectorAll('.btn-detalle').forEach(boton => {
            boton.addEventListener('click', () => verDetalle(boton.dataset.id));
        });

        contenedor.querySelectorAll('.btn-alquilar-directo').forEach(boton => {
            boton.addEventListener('click', async () => {
                const id = boton.dataset.id;
                try {
                    const h = await API.catalogo.detalle(id);
                    abrirModalReservaConHerramienta(h);
                } catch (e) {
                    UI.toast('Error al consultar herramienta: ' + e.message, 'error');
                }
            });
        });
    }

    // ------------------------------------------------------------------
    // Modal Ficha Técnica
    // ------------------------------------------------------------------

    async function verDetalle(id) {
        const cuerpo = document.getElementById('detalle-cuerpo');
        UI.cargando(cuerpo, 'Cargando ficha técnica y especificaciones...');
        UI.abrirModal('modal-detalle');

        try {
            const h = await API.catalogo.detalle(id);
            document.getElementById('detalle-titulo').textContent = h.nombre;

            const especificaciones = (h.especificaciones || []).length
                ? h.especificaciones.map(e => `
                    <div class="dato">
                        <div class="etiqueta">${UI.esc(e.atributo)}</div>
                        <div class="valor">${UI.esc(e.valorConUnidad)}</div>
                    </div>`).join('')
                : '<p class="ayuda-campo">Ficha técnica estándar según catálogo del fabricante.</p>';

            const tarifas = (h.tarifas || []).length
                ? h.tarifas.map(t => `
                    <tr>
                        <td><strong>${UI.esc(t.descripcionTipo || t.tipoTarifa)}</strong></td>
                        <td><strong style="color:var(--color-acento);">${UI.pesos(t.valor)}</strong></td>
                        <td>${UI.pesos(t.valorPorDia)} / día</td>
                    </tr>`).join('')
                : `<tr><td>Tarifa Diaria</td><td>${UI.pesos(h.tarifaDiaria)}</td><td>${UI.pesos(h.tarifaDiaria)} / día</td></tr>`;

            cuerpo.innerHTML = `
                <div style="display:flex; gap:16px; margin-bottom:18px; align-items:center; flex-wrap:wrap;">
                    ${UI.insignia(h.estadoOperativo)}
                    <span style="font-weight:700; color:#38bdf8;">📍 ${UI.esc(h.ubicacion || 'Colombia')}</span>
                    <span style="color:#94a3b8; font-size:0.85rem;">Serie: <code>${UI.esc(h.numeroSerie || 'SN-2026')}</code></span>
                </div>

                <p style="margin-bottom:20px; font-size:1.02rem; line-height:1.6; color:#cbd5e1;">${UI.esc(h.descripcion)}</p>

                <div class="lista-datos" style="margin-bottom:22px;">
                    <div class="dato"><div class="etiqueta">Marca Fabricante</div><div class="valor">${UI.esc(h.marca)}</div></div>
                    <div class="dato"><div class="etiqueta">Modelo</div><div class="valor">${UI.esc(h.modelo)}</div></div>
                    <div class="dato"><div class="etiqueta">Tarifa Oficial</div><div class="valor" style="color:var(--color-acento);">${UI.pesos(h.tarifaDiaria)} / día</div></div>
                    <div class="dato"><div class="etiqueta">Valor Reposición</div><div class="valor">${UI.pesos(h.valorReposicion)}</div></div>
                </div>

                <h3 style="margin-bottom:12px; font-size:1.1rem; color:#f8fafc;">📋 Ficha Técnica y Rendimiento</h3>
                <div class="lista-datos" style="margin-bottom:24px;">${especificaciones}</div>

                <h3 style="margin-bottom:12px; font-size:1.1rem; color:#f8fafc;">💰 Modalidades Tarifarias</h3>
                <div class="envoltura-tabla">
                    <table>
                        <thead><tr><th>Modalidad</th><th>Tarifa Total</th><th>Equivalente por Día</th></tr></thead>
                        <tbody>${tarifas}</tbody>
                    </table>
                </div>
            `;

            herramientaActual = h;
        } catch (error) {
            cuerpo.innerHTML = `<div class="mensaje mensaje-error visible">${UI.esc(error.message)}</div>`;
        }
    }

    // ------------------------------------------------------------------
    // Modal de Reserva y Cotizador Dinámico
    // ------------------------------------------------------------------

    const btnAbrirReserva = document.getElementById('btn-abrir-reserva');
    const formCrearReserva = document.getElementById('form-crear-reserva');
    const inputInicio = document.getElementById('reserva-fecha-inicio');
    const inputFin = document.getElementById('reserva-fecha-fin');

    btnAbrirReserva.addEventListener('click', () => {
        if (!herramientaActual) return;
        if (!API.haySesion()) {
            UI.cerrarModal('modal-detalle');
            UI.toast('Debes iniciar sesión para alquilar una herramienta.', 'info');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 700);
            return;
        }
        UI.cerrarModal('modal-detalle');
        abrirModalReservaConHerramienta(herramientaActual);
    });

    function abrirModalReservaConHerramienta(h) {
        herramientaActual = h;

        document.getElementById('reserva-id-herramienta').value = h.idHerramienta;
        document.getElementById('reserva-tarifa-diaria').value  = h.tarifaDiaria;
        document.getElementById('reserva-modal-subtitulo').textContent = `Alquilar: ${h.nombre} (${h.marca} ${h.modelo})`;

        const hoy = new Date();
        const hoyStr = hoy.toISOString().split('T')[0];
        const fechaRetorno = new Date();
        fechaRetorno.setDate(hoy.getDate() + 3);

        inputInicio.min   = hoyStr;
        inputFin.min      = hoyStr;
        inputInicio.value = hoyStr;
        inputFin.value    = fechaRetorno.toISOString().split('T')[0];

        recalcularPresupuesto();
        UI.ocultarMensaje('reserva-mensaje');
        UI.abrirModal('modal-crear-reserva');
    }

    inputInicio.addEventListener('change', recalcularPresupuesto);
    inputFin.addEventListener('change', recalcularPresupuesto);

    function recalcularPresupuesto() {
        if (!inputInicio.value || !inputFin.value) return;

        const f1 = new Date(inputInicio.value);
        const f2 = new Date(inputFin.value);
        const tarifa = Number(document.getElementById('reserva-tarifa-diaria').value) || 75000;

        let dias = Math.ceil((f2 - f1) / (1000 * 60 * 60 * 24));
        if (dias <= 0) dias = 1;

        const subtotal = tarifa * dias;
        const deposito = Math.max(100000, Math.round(subtotal * 0.2));
        const totalCompromiso = subtotal + deposito;

        document.getElementById('reserva-dias-calculados').textContent = `${dias} ${dias === 1 ? 'día' : 'días'}`;
        document.getElementById('reserva-monto-calculado').textContent = UI.pesos(subtotal);
        document.getElementById('reserva-deposito-calculado').textContent = UI.pesos(deposito);
        document.getElementById('reserva-total-compromiso').textContent = UI.pesos(totalCompromiso);
    }

    formCrearReserva.addEventListener('submit', async (e) => {
        e.preventDefault();
        UI.ocultarMensaje('reserva-mensaje');

        const btn = document.getElementById('btn-confirmar-reserva');
        btn.disabled = true;
        btn.textContent = '⏳ Procesando reserva...';

        try {
            if (!API.haySesion()) {
                UI.mensaje('reserva-mensaje', 'Debes iniciar sesión para completar la reserva.', 'error');
                setTimeout(() => { window.location.href = 'login.html'; }, 800);
                return;
            }
            const usuario = API.obtenerUsuario();
            const idHerramienta = Number(document.getElementById('reserva-id-herramienta').value);
            const fechaInicio = inputInicio.value;
            const fechaFin = inputFin.value;
            const notas = document.getElementById('reserva-notas').value;

            const comando = {
                idCliente: usuario.idUsuario,
                fechaInicio,
                fechaFin,
                notasCliente: notas,
                items: [
                    {
                        idHerramienta,
                        cantidad: 1
                    }
                ]
            };

            const reserva = await API.reservas.crear(comando);
            UI.cerrarModal('modal-crear-reserva');
            UI.toast(`¡Solicitud #${reserva.codigoReserva || 'RES-2026'} creada con éxito!`, 'exito');
            UI.mensaje('mensaje', `¡Reserva #${reserva.codigoReserva || 'RES-2026'} registrada exitosamente! Puede gestionarla o pagar en "Mis Reservas".`, 'exito');

            setTimeout(() => {
                window.location.href = 'reservas.html';
            }, 1200);

        } catch (error) {
            UI.mensaje('reserva-mensaje', 'Error al solicitar reserva: ' + error.message, 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Confirmar Solicitud de Reserva';
        }
    });

});
