/* ==========================================================================
   comun.js - Utilidades compartidas por todas las pantallas
   Autor: Camilo Garcia
   ========================================================================== */

const UI = (() => {

    'use strict';

    /** Muestra un mensaje en el contenedor indicado. */
    function mensaje(idContenedor, texto, tipo = 'error') {
        const caja = document.getElementById(idContenedor);
        if (!caja) return;
        caja.textContent = texto;
        caja.className = 'mensaje mensaje-' + tipo + ' visible';
        if (tipo === 'exito') {
            setTimeout(() => ocultarMensaje(idContenedor), 4000);
        }
    }

    function ocultarMensaje(idContenedor) {
        const caja = document.getElementById(idContenedor);
        if (caja) caja.className = 'mensaje';
    }

    /** Formatea un numero como pesos colombianos. */
    function pesos(valor) {
        if (valor === null || valor === undefined) return '-';
        return new Intl.NumberFormat('es-CO', {
            style: 'currency',
            currency: 'COP',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(valor);
    }

    function fecha(valor) {
        if (!valor) return '-';
        const d = new Date(valor);
        if (isNaN(d.getTime())) return valor;
        return d.toLocaleDateString('es-CO', { year: 'numeric', month: 'short', day: '2-digit' });
    }

    /**
     * Escapa texto antes de insertarlo en el HTML.
     * Previene XSS si algun dato guardado en la base trae etiquetas.
     */
    function esc(texto) {
        if (texto === null || texto === undefined) return '';
        return String(texto)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    function insignia(estado) {
        if (!estado) return '';
        const clase = 'insignia insignia-' + String(estado).toLowerCase();
        return '<span class="' + clase + '">' + esc(String(estado).replace(/_/g, ' ')) + '</span>';
    }

    function cargando(contenedor, texto = 'Cargando...') {
        contenedor.innerHTML = '<div class="cargando">' + esc(texto) + '</div>';
    }

    function vacio(contenedor, texto, icono = '[ ]') {
        contenedor.innerHTML =
            '<div class="estado-vacio">' +
            '<div class="icono">' + esc(icono) + '</div>' +
            '<p>' + esc(texto) + '</p>' +
            '</div>';
    }

    /**
     * Pinta la barra superior segun el estado de la sesion.
     * Se llama desde cada pagina en el DOMContentLoaded.
     */
    function pintarBarra(paginaActual) {
        const contenedor = document.getElementById('menu-sesion');
        if (!contenedor) return;

        const usuario = API.obtenerUsuario();

        if (!usuario) {
            contenedor.innerHTML =
                '<a href="login.html" class="boton boton-pequeno">Iniciar sesion</a>' +
                '<a href="registro.html" class="boton boton-secundario boton-pequeno">Registrarse</a>';
            return;
        }

        let enlaces = '<a href="index.html"' + (paginaActual === 'catalogo' ? ' class="activo"' : '') + '>Catálogo</a>';

        if (API.haySesion()) {
            enlaces += '<a href="reservas.html"' +
                (paginaActual === 'reservas' ? ' class="activo"' : '') + '>Mis Reservas</a>';
        }

        if (API.esProveedor() || API.esAdmin()) {
            enlaces += '<a href="proveedor.html"' +
                (paginaActual === 'proveedor' ? ' class="activo"' : '') + '>Inventario</a>';
            enlaces += '<a href="gestion_reservas.html"' +
                (paginaActual === 'gestion_reservas' ? ' class="activo"' : '') + '>Devoluciones y Despacho</a>';
            enlaces += '<a href="dashboard.html"' +
                (paginaActual === 'dashboard' ? ' class="activo"' : '') + '>Dashboard</a>';
        }

        if (API.esAdmin()) {
            enlaces += '<a href="admin.html"' +
                (paginaActual === 'admin' ? ' class="activo"' : '') + '>Usuarios</a>';
        }

        contenedor.innerHTML =
            enlaces +
            '<span class="usuario-sesion">' +
            '<span class="etiqueta-rol">' + esc(API.rolPrincipal()) + '</span>' +
            esc(usuario.nombreCompleto) +
            '</span>' +
            '<button class="boton boton-secundario boton-pequeno" id="btn-salir">Salir</button>';

        const btn = document.getElementById('btn-salir');
        if (btn) btn.addEventListener('click', () => API.auth.logout());
    }

    /** Redirige al login si no hay sesion, o si el rol no es el esperado. */
    function exigirSesion(rolRequerido) {
        if (!API.haySesion()) {
            window.location.href = 'login.html';
            return false;
        }
        if (rolRequerido && !API.tieneRol(rolRequerido) && !API.esAdmin()) {
            window.location.href = 'index.html';
            return false;
        }
        return true;
    }

    function abrirModal(id) {
        const m = document.getElementById(id);
        if (m) m.classList.add('visible');
    }

    function cerrarModal(id) {
        const m = document.getElementById(id);
        if (m) m.classList.remove('visible');
    }

    /** Lee un formulario y devuelve un objeto plano, sin los campos vacios. */
    function leerFormulario(formulario) {
        const datos = {};
        new FormData(formulario).forEach((valor, clave) => {
            const v = typeof valor === 'string' ? valor.trim() : valor;
            if (v !== '') datos[clave] = v;
        });
        return datos;
    }

    return {
        mensaje, ocultarMensaje, pesos, fecha, esc, insignia,
        cargando, vacio, pintarBarra, exigirSesion,
        abrirModal, cerrarModal, leerFormulario
    };

})();
