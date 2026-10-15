/* ==========================================================================
   admin.js - Tabla de control de usuarios (solo administrador)
   Autor: Camilo Garcia
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

    'use strict';

    if (!UI.exigirSesion('ROLE_ADMIN')) return;

    UI.pintarBarra('admin');

    const tabla = document.getElementById('tabla-usuarios');
    let filtroRol = '';

    cargar();

    // Pestanas de filtro por rol
    document.querySelectorAll('.pestana').forEach(pestana => {
        pestana.addEventListener('click', () => {
            document.querySelectorAll('.pestana').forEach(p => p.classList.remove('activa'));
            pestana.classList.add('activa');
            filtroRol = pestana.dataset.rol || '';
            cargar();
        });
    });

    // ==================================================================

    async function cargar() {
        tabla.innerHTML = '<tr><td colspan="8" class="cargando">Cargando usuarios...</td></tr>';
        UI.ocultarMensaje('mensaje');

        try {
            const usuarios = await API.usuarios.listar(filtroRol);

            if (usuarios.length === 0) {
                tabla.innerHTML =
                    '<tr><td colspan="8" class="estado-vacio">No hay usuarios con ese filtro.</td></tr>';
                return;
            }

            tabla.innerHTML = usuarios.map(u => construirFila(u)).join('');

            tabla.querySelectorAll('button[data-accion]').forEach(boton => {
                boton.addEventListener('click', () => {
                    const { accion, id, nombre } = boton.dataset;
                    if (accion === 'bloquear')  ejecutar(API.usuarios.bloquear, id, 'bloquear la cuenta de ' + nombre);
                    if (accion === 'activar')   ejecutar(API.usuarios.activar, id, 'activar la cuenta de ' + nombre);
                    if (accion === 'verificar') ejecutar(API.usuarios.verificarProveedor, id, 'verificar al proveedor ' + nombre);
                });
            });

        } catch (error) {
            tabla.innerHTML = '<tr><td colspan="8"></td></tr>';
            UI.mensaje('mensaje', error.message, 'error');
        }
    }

    function construirFila(u) {
        const roles = (u.roles || [])
            .map(r => r.replace('ROLE_', ''))
            .join(', ');

        const esProveedor = (u.roles || []).includes('ROLE_PROVEEDOR');
        const esAdmin     = (u.roles || []).includes('ROLE_ADMIN');

        // Columna de verificacion: solo tiene sentido para proveedores.
        let verificacion = '<span class="ayuda-campo">-</span>';
        if (esProveedor && u.perfilProveedor) {
            verificacion = u.perfilProveedor.verificado
                ? '<span class="insignia insignia-verificado">VERIFICADO</span>'
                : '<span class="insignia insignia-pendiente">PENDIENTE</span>';
        }

        // Acciones disponibles segun el estado actual de la cuenta.
        let acciones = '';

        if (!esAdmin) {
            acciones += u.activo
                ? `<button class="boton boton-peligro boton-pequeno" data-accion="bloquear"
                           data-id="${u.idUsuario}" data-nombre="${UI.esc(u.nombreCompleto)}">Bloquear</button>`
                : `<button class="boton boton-pequeno" data-accion="activar"
                           data-id="${u.idUsuario}" data-nombre="${UI.esc(u.nombreCompleto)}">Activar</button>`;
        } else {
            acciones += '<span class="ayuda-campo">Cuenta protegida</span>';
        }

        if (esProveedor && u.perfilProveedor && !u.perfilProveedor.verificado) {
            acciones += `<button class="boton boton-secundario boton-pequeno" data-accion="verificar"
                                 data-id="${u.idUsuario}" data-nombre="${UI.esc(u.nombreCompleto)}">Verificar</button>`;
        }

        return `
            <tr>
                <td>${u.idUsuario}</td>
                <td>${UI.esc(u.nombreCompleto)}</td>
                <td>${UI.esc(u.email)}</td>
                <td>${UI.esc(u.tipoDocumento)} ${UI.esc(u.numeroDocumento)}</td>
                <td><span class="ayuda-campo">${UI.esc(roles)}</span></td>
                <td>${u.activo
                        ? '<span class="insignia insignia-activo">ACTIVO</span>'
                        : '<span class="insignia insignia-bloqueado">BLOQUEADO</span>'}</td>
                <td>${verificacion}</td>
                <td><div class="acciones-celda">${acciones}</div></td>
            </tr>
        `;
    }

    /** Ejecuta la accion pidiendo confirmacion y recargando la tabla. */
    async function ejecutar(funcionApi, id, descripcion) {
        if (!confirm('Confirma ' + descripcion + '?')) return;

        try {
            await funcionApi(id);
            UI.mensaje('mensaje', 'Operacion realizada correctamente.', 'exito');
            cargar();
        } catch (error) {
            UI.mensaje('mensaje', error.message, 'error');
        }
    }
});
