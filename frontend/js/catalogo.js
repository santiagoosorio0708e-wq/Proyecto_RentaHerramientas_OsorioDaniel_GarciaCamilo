/* ==========================================================================
   catalogo.js - Pantalla publica del catalogo
   Autor: Camilo Garcia
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

    'use strict';

    UI.pintarBarra('catalogo');

    const formFiltros = document.getElementById('form-filtros');
    const contenedor  = document.getElementById('resultados');
    const titulo      = document.getElementById('titulo-resultados');

    cargarCategorias();
    buscar();

    formFiltros.addEventListener('submit', (e) => {
        e.preventDefault();
        buscar();
    });

    document.getElementById('btn-limpiar').addEventListener('click', () => {
        formFiltros.reset();
        buscar();
    });

    // Cierre de los modales
    document.querySelectorAll('[data-cerrar]').forEach(boton => {
        boton.addEventListener('click', () => UI.cerrarModal(boton.dataset.cerrar));
    });

    // ------------------------------------------------------------------

    async function cargarCategorias() {
        try {
            const categorias = await API.categorias.listar();
            const select = document.getElementById('idCategoria');
            categorias.forEach(c => {
                const opcion = document.createElement('option');
                opcion.value = c.idCategoria;
                opcion.textContent = c.nombre;
                select.appendChild(opcion);
            });
        } catch (e) {
            // Si fallan las categorias el catalogo igual debe poder usarse.
            console.warn('No se pudieron cargar las categorias:', e.message);
        }
    }

    async function buscar() {
        UI.cargando(contenedor, 'Consultando el catalogo...');
        UI.ocultarMensaje('mensaje');

        const filtros = UI.leerFormulario(formFiltros);

        try {
            const herramientas = await API.catalogo.buscar(filtros);
            pintar(herramientas);
        } catch (error) {
            contenedor.innerHTML = '';
            UI.mensaje('mensaje', error.message, 'error');
        }
    }

    function pintar(lista) {
        titulo.textContent = 'Resultados (' + lista.length + ')';

        if (lista.length === 0) {
            UI.vacio(contenedor, 'No se encontraron herramientas con esos criterios.');
            return;
        }

        contenedor.innerHTML = lista.map(h => `
            <article class="tarjeta-herramienta" data-id="${h.idHerramienta}">
                <div class="imagen-herramienta">
                    ${h.imagenPrincipal
                        ? `<img src="${UI.esc(h.imagenPrincipal)}" alt="${UI.esc(h.nombre)}"
                                onerror="this.parentElement.textContent='[sin foto]'">`
                        : '[sin foto]'}
                </div>
                <div class="cuerpo-tarjeta">
                    <h3>${UI.esc(h.nombre)}</h3>
                    <p class="marca-modelo">${UI.esc(h.marca)} &middot; ${UI.esc(h.modelo)}</p>
                    <p class="marca-modelo">${UI.esc(h.categoriaPrincipal || 'Sin categoria')}</p>
                    ${UI.insignia(h.estadoOperativo)}
                    <p class="precio">${UI.pesos(h.tarifaDiaria)} <small>/ dia</small></p>
                    <button class="boton boton-pequeno btn-detalle" data-id="${h.idHerramienta}">
                        Ver detalle
                    </button>
                </div>
            </article>
        `).join('');

        contenedor.querySelectorAll('.btn-detalle').forEach(boton => {
            boton.addEventListener('click', () => verDetalle(boton.dataset.id));
        });
    }

    async function verDetalle(id) {
        const cuerpo = document.getElementById('detalle-cuerpo');
        UI.cargando(cuerpo, 'Cargando ficha tecnica...');
        UI.abrirModal('modal-detalle');

        try {
            const h = await API.catalogo.detalle(id);
            document.getElementById('detalle-titulo').textContent = h.nombre;

            const especificaciones = (h.especificaciones || []).length
                ? h.especificaciones.map(e =>
                    `<div class="dato">
                        <div class="etiqueta">${UI.esc(e.atributo)}</div>
                        <div class="valor">${UI.esc(e.valorConUnidad)}</div>
                     </div>`).join('')
                : '<p class="ayuda-campo">Sin ficha tecnica registrada.</p>';

            const tarifas = (h.tarifas || []).length
                ? h.tarifas.map(t =>
                    `<tr>
                        <td>${UI.esc(t.descripcionTipo || t.tipoTarifa)}</td>
                        <td>${UI.pesos(t.valor)}</td>
                        <td>${UI.pesos(t.valorPorDia)} / dia</td>
                     </tr>`).join('')
                : '<tr><td colspan="3">Sin tarifas adicionales.</td></tr>';

            const categorias = (h.categorias || []).map(c => UI.esc(c.nombre)).join(', ');

            cuerpo.innerHTML = `
                <p style="margin-bottom:14px;">${UI.esc(h.descripcion)}</p>

                <div class="lista-datos" style="margin-bottom:18px;">
                    <div class="dato"><div class="etiqueta">Marca</div><div class="valor">${UI.esc(h.marca)}</div></div>
                    <div class="dato"><div class="etiqueta">Modelo</div><div class="valor">${UI.esc(h.modelo)}</div></div>
                    <div class="dato"><div class="etiqueta">Estado</div><div class="valor">${UI.insignia(h.estadoOperativo)}</div></div>
                    <div class="dato"><div class="etiqueta">Tarifa diaria</div><div class="valor">${UI.pesos(h.tarifaDiaria)}</div></div>
                </div>

                <h3>Categorias</h3>
                <p class="ayuda-campo" style="margin-bottom:16px;">${categorias || 'Sin categorias'}</p>

                <h3>Ficha tecnica</h3>
                <div class="lista-datos" style="margin-bottom:18px;">${especificaciones}</div>

                <h3>Tarifas</h3>
                <div class="envoltura-tabla">
                    <table>
                        <thead><tr><th>Modalidad</th><th>Valor</th><th>Equivalente</th></tr></thead>
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
    // Logica de Reserva desde el Catalogo
    // ------------------------------------------------------------------

    let herramientaActual = null;
    const btnAbrirReserva = document.getElementById('btn-abrir-reserva');
    const formCrearReserva = document.getElementById('form-crear-reserva');
    const inputInicio = document.getElementById('reserva-fecha-inicio');
    const inputFin = document.getElementById('reserva-fecha-fin');

    btnAbrirReserva.addEventListener('click', () => {
        if (!API.haySesion()) {
            alert('Para reservar y alquilar maquinaria, debe iniciar sesión con su cuenta de Cliente.');
            window.location.href = 'login.html';
            return;
        }

        if (!herramientaActual) return;

        UI.cerrarModal('modal-detalle');

        document.getElementById('reserva-id-herramienta').value = herramientaActual.idHerramienta;
        document.getElementById('reserva-tarifa-diaria').value  = herramientaActual.tarifaDiaria;
        document.getElementById('reserva-modal-subtitulo').textContent = `Alquilar: ${herramientaActual.nombre} (${herramientaActual.marca})`;

        const hoy = new Date();
        const hoyStr = hoy.toISOString().split('T')[0];
        const manana = new Date();
        manana.setDate(hoy.getDate() + 3);

        inputInicio.min   = hoyStr;
        inputFin.min      = hoyStr;
        inputInicio.value = hoyStr;
        inputFin.value    = manana.toISOString().split('T')[0];

        recalcularPresupuesto();
        UI.ocultarMensaje('reserva-mensaje');
        UI.abrirModal('modal-crear-reserva');
    });

    inputInicio.addEventListener('change', recalcularPresupuesto);
    inputFin.addEventListener('change', recalcularPresupuesto);

    function recalcularPresupuesto() {
        if (!inputInicio.value || !inputFin.value) return;

        const f1 = new Date(inputInicio.value);
        const f2 = new Date(inputFin.value);
        const tarifa = Number(document.getElementById('reserva-tarifa-diaria').value) || 0;

        let dias = Math.ceil((f2 - f1) / (1000 * 60 * 60 * 24));
        if (dias <= 0) dias = 1;

        const subtotal = tarifa * dias;
        const deposito = Math.max(100000, Math.round(subtotal * 0.2));

        document.getElementById('reserva-dias-calculados').textContent = `${dias} ${dias === 1 ? 'día' : 'días'}`;
        document.getElementById('reserva-monto-calculado').textContent = UI.pesos(subtotal);
        document.getElementById('reserva-deposito-calculado').textContent = UI.pesos(deposito);
    }

    formCrearReserva.addEventListener('submit', async (e) => {
        e.preventDefault();
        UI.ocultarMensaje('reserva-mensaje');

        const btn = document.getElementById('btn-confirmar-reserva');
        btn.disabled = true;
        btn.textContent = 'Enviando solicitud...';

        try {
            const usuario = API.obtenerUsuario();
            const idHerramienta = Number(document.getElementById('reserva-id-herramienta').value);
            const fechaInicio = inputInicio.value;
            const fechaFin = inputFin.value;
            const notas = document.getElementById('reserva-notas').value;

            const comando = {
                idCliente: usuario ? usuario.idUsuario : 1,
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
            UI.mensaje('mensaje', `¡Reserva #${reserva.codigoReserva} solicitada con éxito! Diríjase a 'Mis Reservas' para gestionarla y pagar.`, 'exito');
        } catch (error) {
            UI.mensaje('reserva-mensaje', 'Error al solicitar reserva: ' + error.message, 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Confirmar Solicitud de Reserva';
        }
    });
});
