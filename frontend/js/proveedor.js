/* ==========================================================================
   proveedor.js - Panel de inventario del proveedor
   Autor: Camilo Garcia
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

    'use strict';

    // Esta pantalla es solo para PROVEEDOR (el ADMIN tambien entra).
    if (!UI.exigirSesion('ROLE_PROVEEDOR')) return;

    UI.pintarBarra('proveedor');

    const tabla = document.getElementById('tabla-inventario');
    let categoriasCache = [];

    cargarCategorias().then(cargarInventario);

    document.getElementById('btn-nueva').addEventListener('click', abrirNueva);
    document.getElementById('btn-guardar-herramienta').addEventListener('click', guardarHerramienta);
    document.getElementById('btn-guardar-estado').addEventListener('click', guardarEstado);

    document.querySelectorAll('[data-cerrar]').forEach(boton => {
        boton.addEventListener('click', () => UI.cerrarModal(boton.dataset.cerrar));
    });

    // ==================================================================
    // Carga de datos
    // ==================================================================

    async function cargarCategorias() {
        try {
            categoriasCache = await API.categorias.listar();
            const select = document.getElementById('h-categoria');
            select.innerHTML = categoriasCache
                .map(c => `<option value="${c.idCategoria}">${UI.esc(c.nombre)}</option>`)
                .join('');
        } catch (e) {
            UI.mensaje('mensaje', 'No se pudieron cargar las categorias: ' + e.message, 'error');
        }
    }

    async function cargarInventario() {
        tabla.innerHTML = '<tr><td colspan="7" class="cargando">Cargando inventario...</td></tr>';

        try {
            const lista = await API.herramientas.miInventario();

            if (lista.length === 0) {
                tabla.innerHTML =
                    '<tr><td colspan="7" class="estado-vacio">' +
                    'Todavia no tiene herramientas registradas. Use el boton "Registrar herramienta".' +
                    '</td></tr>';
                return;
            }

            tabla.innerHTML = lista.map(h => `
                <tr>
                    <td>${h.idHerramienta}</td>
                    <td>${UI.esc(h.nombre)}</td>
                    <td>${UI.esc(h.marca)} / ${UI.esc(h.modelo)}</td>
                    <td>${UI.esc(h.categoriaPrincipal || '-')}</td>
                    <td>${UI.insignia(h.estadoOperativo)}</td>
                    <td>${UI.pesos(h.tarifaDiaria)}</td>
                    <td>
                        <div class="acciones-celda">
                            <button class="boton boton-secundario boton-pequeno"
                                    data-accion="editar" data-id="${h.idHerramienta}">Editar</button>
                            <button class="boton boton-secundario boton-pequeno"
                                    data-accion="estado" data-id="${h.idHerramienta}"
                                    data-estado="${h.estadoOperativo}">Estado</button>
                            <button class="boton boton-peligro boton-pequeno"
                                    data-accion="eliminar" data-id="${h.idHerramienta}"
                                    data-nombre="${UI.esc(h.nombre)}">Eliminar</button>
                        </div>
                    </td>
                </tr>
            `).join('');

            tabla.querySelectorAll('button[data-accion]').forEach(boton => {
                boton.addEventListener('click', () => {
                    const { accion, id, estado, nombre } = boton.dataset;
                    if (accion === 'editar')   abrirEdicion(id);
                    if (accion === 'estado')   abrirEstado(id, estado);
                    if (accion === 'eliminar') eliminar(id, nombre);
                });
            });

        } catch (error) {
            tabla.innerHTML = '<tr><td colspan="7"></td></tr>';
            UI.mensaje('mensaje', error.message, 'error');
        }
    }

    // ==================================================================
    // Alta y edicion
    // ==================================================================

    function abrirNueva() {
        document.getElementById('titulo-modal-herramienta').textContent = 'Registrar herramienta';
        document.getElementById('form-herramienta').reset();
        document.getElementById('h-id').value = '';
        document.getElementById('h-serie').required = true;
        document.getElementById('campo-serie').classList.remove('oculto');
        document.getElementById('bloque-alta').classList.remove('oculto');
        UI.ocultarMensaje('mensaje-modal');
        UI.abrirModal('modal-herramienta');
    }

    async function abrirEdicion(id) {
        UI.ocultarMensaje('mensaje-modal');
        try {
            const h = await API.herramientas.detalle(id);

            document.getElementById('titulo-modal-herramienta').textContent = 'Editar herramienta';
            document.getElementById('h-id').value = h.idHerramienta;
            document.getElementById('h-nombre').value = h.nombre || '';
            document.getElementById('h-descripcion').value = h.descripcion || '';
            document.getElementById('h-marca').value = h.marca || '';
            document.getElementById('h-modelo').value = h.modelo || '';
            document.getElementById('h-tarifa').value = h.tarifaDiaria || '';
            document.getElementById('h-reposicion').value = h.valorReposicion || '';
            document.getElementById('h-ubicacion').value = h.ubicacionBodega || '';

            if (h.categoriaPrincipal) {
                document.getElementById('h-categoria').value = h.categoriaPrincipal.idCategoria;
            }

            // El numero de serie y la carga inicial solo aplican al crear.
            document.getElementById('h-serie').required = false;
            document.getElementById('campo-serie').classList.add('oculto');
            document.getElementById('bloque-alta').classList.add('oculto');

            UI.abrirModal('modal-herramienta');
        } catch (error) {
            UI.mensaje('mensaje', error.message, 'error');
        }
    }

    async function guardarHerramienta() {
        const formulario = document.getElementById('form-herramienta');
        if (!formulario.reportValidity()) return;

        const boton = document.getElementById('btn-guardar-herramienta');
        boton.disabled = true;
        boton.textContent = 'Guardando...';
        UI.ocultarMensaje('mensaje-modal');

        const id = document.getElementById('h-id').value;

        try {
            if (id) {
                // ---------- Edicion ----------
                await API.herramientas.actualizar(id, {
                    nombre: valor('h-nombre'),
                    descripcion: valor('h-descripcion'),
                    marca: valor('h-marca'),
                    modelo: valor('h-modelo'),
                    tarifaDiaria: numero('h-tarifa'),
                    valorReposicion: numero('h-reposicion'),
                    ubicacionBodega: valor('h-ubicacion') || null,
                    idCategoriaPrincipal: numero('h-categoria')
                });
                UI.mensaje('mensaje', 'Herramienta actualizada correctamente.', 'exito');

            } else {
                // ---------- Alta ----------
                const cuerpo = {
                    idCategoriaPrincipal: numero('h-categoria'),
                    nombre: valor('h-nombre'),
                    descripcion: valor('h-descripcion'),
                    marca: valor('h-marca'),
                    modelo: valor('h-modelo'),
                    numeroSerie: valor('h-serie'),
                    tarifaDiaria: numero('h-tarifa'),
                    valorReposicion: numero('h-reposicion'),
                    ubicacionBodega: valor('h-ubicacion') || null
                };

                const urlFoto = valor('h-foto');
                if (urlFoto) {
                    cuerpo.imagenes = [{
                        urlFoto: urlFoto,
                        descripcion: 'Foto principal',
                        principal: true,
                        orden: 1
                    }];
                }

                const atributo = valor('h-atributo');
                const valorEsp = valor('h-valor');
                if (atributo && valorEsp) {
                    cuerpo.especificaciones = [{
                        atributo: atributo,
                        valor: valorEsp,
                        unidadMedida: valor('h-unidad') || null
                    }];
                }

                const tipoTarifa = valor('h-tipoTarifa');
                const valorTarifa = numero('h-valorTarifa');
                if (tipoTarifa && valorTarifa) {
                    cuerpo.tarifasAdicionales = [{ tipoTarifa: tipoTarifa, valor: valorTarifa }];
                }

                await API.herramientas.crear(cuerpo);
                UI.mensaje('mensaje', 'Herramienta registrada correctamente.', 'exito');
            }

            UI.cerrarModal('modal-herramienta');
            cargarInventario();

        } catch (error) {
            UI.mensaje('mensaje-modal', error.message, 'error');
        } finally {
            boton.disabled = false;
            boton.textContent = 'Guardar';
        }
    }

    // ==================================================================
    // Cambio de estado
    // ==================================================================

    function abrirEstado(id, estadoActual) {
        document.getElementById('e-id').value = id;
        document.getElementById('e-motivo').value = '';
        document.getElementById('estado-actual-texto').innerHTML =
            'Estado actual: ' + UI.insignia(estadoActual);
        UI.ocultarMensaje('mensaje-estado');
        UI.abrirModal('modal-estado');
    }

    async function guardarEstado() {
        const boton = document.getElementById('btn-guardar-estado');
        boton.disabled = true;
        boton.textContent = 'Aplicando...';
        UI.ocultarMensaje('mensaje-estado');

        try {
            await API.herramientas.cambiarEstado(valor('e-id'), {
                nuevoEstado: valor('e-estado'),
                motivo: valor('e-motivo') || null
            });

            UI.cerrarModal('modal-estado');
            UI.mensaje('mensaje', 'Estado actualizado correctamente.', 'exito');
            cargarInventario();

        } catch (error) {
            UI.mensaje('mensaje-estado', error.message, 'error');
        } finally {
            boton.disabled = false;
            boton.textContent = 'Aplicar';
        }
    }

    // ==================================================================
    // Eliminacion
    // ==================================================================

    async function eliminar(id, nombre) {
        if (!confirm('Seguro que desea eliminar la herramienta "' + nombre + '"?\n\nEsta accion no se puede deshacer.')) {
            return;
        }
        try {
            await API.herramientas.eliminar(id);
            UI.mensaje('mensaje', 'Herramienta eliminada correctamente.', 'exito');
            cargarInventario();
        } catch (error) {
            UI.mensaje('mensaje', error.message, 'error');
        }
    }

    // ==================================================================
    // Utilidades locales
    // ==================================================================

    function valor(id) {
        const el = document.getElementById(id);
        return el && el.value ? el.value.trim() : '';
    }

    function numero(id) {
        const v = valor(id);
        return v === '' ? null : Number(v);
    }
});
