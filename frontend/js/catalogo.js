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
        } catch (error) {
            cuerpo.innerHTML = `<div class="mensaje mensaje-error visible">${UI.esc(error.message)}</div>`;
        }
    }
});
