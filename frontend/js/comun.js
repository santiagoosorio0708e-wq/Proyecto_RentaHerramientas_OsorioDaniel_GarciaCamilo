/* ==========================================================================
   comun.js - Utilidades compartidas, barra superior pro y control de sesion
   Autores: Camilo Garcia & Daniel Osorio
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
            setTimeout(() => ocultarMensaje(idContenedor), 5000);
        }
    }

    function ocultarMensaje(idContenedor) {
        const caja = document.getElementById(idContenedor);
        if (caja) caja.className = 'mensaje';
    }

    /** Formatea un numero como pesos colombianos (COP). */
    function pesos(valor) {
        if (valor === null || valor === undefined) return '$ 0';
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

    function fechaConHora(valor) {
        if (!valor) return '-';
        const d = new Date(valor);
        if (isNaN(d.getTime())) return valor;
        return d.toLocaleDateString('es-CO', { year: 'numeric', month: 'short', day: '2-digit', hour: '2-digit', minute: '2-digit' });
    }

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
        const clave = String(estado).toLowerCase().replace(/\s+/g, '_');
        const etiqueta = String(estado).replace(/_/g, ' ');
        return `<span class="insignia insignia-${clave}">${esc(etiqueta)}</span>`;
    }

    function cargando(contenedor, texto = 'Cargando información...') {
        contenedor.innerHTML = `<div class="cargando">⏳ ${esc(texto)}</div>`;
    }

    function vacio(contenedor, texto = 'No hay registros disponibles', icono = '📦') {
        contenedor.innerHTML = `
            <div class="estado-vacio">
                <div class="icono">${icono}</div>
                <p>${esc(texto)}</p>
            </div>
        `;
    }

    // ------------------------------------------------------------------
    // Gestión de Tema: Modo Día / Modo Noche
    // ------------------------------------------------------------------

    function obtenerTemaActual() {
        const guardado = localStorage.getItem('rh_tema');
        if (guardado) return guardado;
        if (typeof window !== 'undefined' && window.matchMedia && window.matchMedia('(prefers-color-scheme: light)').matches) {
            return 'light';
        }
        return 'dark';
    }

    function aplicarTema(tema) {
        document.documentElement.setAttribute('data-theme', tema);
        localStorage.setItem('rh_tema', tema);
        actualizarBotonesTema(tema);
    }

    function alternarTema() {
        const actual = obtenerTemaActual();
        const nuevo = actual === 'dark' ? 'light' : 'dark';
        localStorage.setItem('rh_tema_manual', 'true');
        aplicarTema(nuevo);
        toast(nuevo === 'light' ? '☀️ Modo Día activado' : '🌙 Modo Noche activado', 'info');
    }

    function actualizarBotonesTema(tema) {
        document.querySelectorAll('.boton-toggle-tema').forEach(btn => {
            const esNav = btn.classList.contains('boton-toggle-nav');
            if (tema === 'dark') {
                btn.innerHTML = esNav ? '☀️ Día' : '☀️ Modo Día';
                btn.setAttribute('title', 'Cambiar a Modo Día');
            } else {
                btn.innerHTML = esNav ? '🌙 Noche' : '🌙 Modo Noche';
                btn.setAttribute('title', 'Cambiar a Modo Noche');
            }
        });
    }

    // Sincronización en tiempo real con macOS / sistema operativo
    if (typeof window !== 'undefined' && window.matchMedia) {
        try {
            window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
                if (!localStorage.getItem('rh_tema_manual')) {
                    aplicarTema(e.matches ? 'dark' : 'light');
                }
            });
        } catch (e) {
            // Compatibilidad Safari legacy
        }
    }

    // Aplicar de inmediato el tema configurado
    aplicarTema(obtenerTemaActual());

    // ------------------------------------------------------------------
    // Carrito de Compras Local Resiliente
    // ------------------------------------------------------------------

    const Carrito = {
        CLAVE: 'rh_carrito_compras',
        obtener: () => {
            try {
                return JSON.parse(localStorage.getItem('rh_carrito_compras') || '[]');
            } catch (e) {
                return [];
            }
        },
        guardar: (items) => {
            localStorage.setItem('rh_carrito_compras', JSON.stringify(items));
            window.dispatchEvent(new CustomEvent('carrito-actualizado', { detail: items }));
            actualizarBadgesCarrito();
        },
        agregar: (herramienta, fechaInicio = null, fechaFin = null, cantidad = 1) => {
            const items = Carrito.obtener();
            const hoy = new Date();
            const hoyStr = hoy.toISOString().split('T')[0];
            const ret = new Date();
            ret.setDate(hoy.getDate() + 3);
            const retStr = ret.toISOString().split('T')[0];

            const fIni = fechaInicio || hoyStr;
            const fFin = fechaFin || retStr;

            const idx = items.findIndex(i => Number(i.idHerramienta) === Number(herramienta.idHerramienta));
            if (idx >= 0) {
                items[idx].cantidad = (items[idx].cantidad || 1) + (Number(cantidad) || 1);
                if (fechaInicio) items[idx].fechaInicio = fechaInicio;
                if (fechaFin) items[idx].fechaFin = fechaFin;
            } else {
                items.push({
                    idHerramienta: Number(herramienta.idHerramienta),
                    nombre: herramienta.nombre,
                    marca: herramienta.marca || 'CAT',
                    modelo: herramienta.modelo || 'Industrial',
                    categoria: herramienta.categoria || herramienta.categoriaPrincipal || 'Maquinaria',
                    tarifaDiaria: Number(herramienta.tarifaDiaria) || 75000,
                    fechaInicio: fIni,
                    fechaFin: fFin,
                    cantidad: Number(cantidad) || 1
                });
            }
            Carrito.guardar(items);
            return items;
        },
        eliminar: (idHerramienta) => {
            const items = Carrito.obtener().filter(i => Number(i.idHerramienta) !== Number(idHerramienta));
            Carrito.guardar(items);
            return items;
        },
        actualizarItem: (idHerramienta, updates) => {
            const items = Carrito.obtener();
            const item = items.find(i => Number(i.idHerramienta) === Number(idHerramienta));
            if (item) {
                Object.assign(item, updates);
                Carrito.guardar(items);
            }
            return items;
        },
        vaciar: () => {
            localStorage.removeItem('rh_carrito_compras');
            window.dispatchEvent(new CustomEvent('carrito-actualizado', { detail: [] }));
            actualizarBadgesCarrito();
        },
        contar: () => {
            const items = Carrito.obtener();
            return items.reduce((sum, it) => sum + (Number(it.cantidad) || 1), 0);
        }
    };

    function actualizarBadgesCarrito() {
        const cant = Carrito.contar();
        document.querySelectorAll('.badge-contador-carrito').forEach(el => {
            el.textContent = cant;
            el.style.display = cant > 0 ? 'inline-flex' : 'none';
        });
    }

    if (typeof window !== 'undefined') {
        window.Carrito = Carrito;
        window.addEventListener('storage', (e) => {
            if (e.key === 'rh_carrito_compras') actualizarBadgesCarrito();
        });
    }

    /**
     * Construye y sincroniza la barra superior y la barra de demo rápido.
     */
    function pintarBarra(paginaActual) {
        // Remover barra demo flotante si existiera en el DOM
        const barraDemo = document.getElementById('barra-demo-rapido');
        if (barraDemo) {
            barraDemo.remove();
        }

        // Menú de navegación principal
        const contenedor = document.getElementById('menu-sesion');
        if (!contenedor) return;

        const online = API.esOnline();
        const usuario = API.obtenerUsuario();
        const cantCarrito = Carrito.contar();
        const badgeCarritoHtml = `<span class="badge-contador-carrito" style="display:${cantCarrito > 0 ? 'inline-flex' : 'none'};">${cantCarrito}</span>`;

        let enlaces = `<a href="index.html"${paginaActual === 'catalogo' ? ' class="activo"' : ''}>🚜 Catálogo</a>`;

        if (usuario) {
            enlaces += `<a href="reservas.html"${paginaActual === 'reservas' ? ' class="activo"' : ''}>📋 Mis Reservas</a>`;
        }

        enlaces += `<a href="perfil.html?tab=carrito"${paginaActual === 'carrito' ? ' class="activo"' : ''}>🛒 Carrito ${badgeCarritoHtml}</a>`;

        if (usuario) {
            enlaces += `<a href="perfil.html"${paginaActual === 'perfil' ? ' class="activo"' : ''}>👤 Mi Perfil</a>`;
        }

        if (API.esProveedor() || API.esAdmin()) {
            enlaces += `<a href="proveedor.html"${paginaActual === 'proveedor' ? ' class="activo"' : ''}>📦 Mi Inventario</a>`;
            enlaces += `<a href="gestion_reservas.html"${paginaActual === 'gestion_reservas' ? ' class="activo"' : ''}>🚚 Despacho & Devolución</a>`;
            enlaces += `<a href="dashboard.html"${paginaActual === 'dashboard' ? ' class="activo"' : ''}>📊 Dashboard</a>`;
        }

        if (API.esAdmin()) {
            enlaces += `<a href="admin.html"${paginaActual === 'admin' ? ' class="activo"' : ''}>👥 Usuarios</a>`;
        }

        const temaActual = obtenerTemaActual();
        const textoTemaNav = temaActual === 'dark' ? '☀️ Día' : '🌙 Noche';

        const badgeConexion = `
            <span class="badge-estado-conexion ${online ? 'online' : 'offline'}" id="badge-api-status" title="${online ? 'Conectado a la API REST de Spring Boot' : 'Operando en Modo Local Resiliente'}">
                <span class="pulso-dot ${online ? 'verde' : 'amarillo'}"></span>
                ${online ? '🟢 Servidor Conectado' : '🟡 Modo Local'}
            </span>
        `;

        if (!usuario) {
            contenedor.innerHTML = `
                ${enlaces}
                <div style="display:flex; align-items:center; gap:10px; margin-left:12px;">
                    ${badgeConexion}
                    <button class="boton-toggle-tema boton-toggle-nav" id="btn-toggle-tema-nav" title="Cambiar tema Día / Noche">
                        ${textoTemaNav}
                    </button>
                    <a href="login.html" class="boton boton-pequeno">Iniciar sesión</a>
                    <a href="registro.html" class="boton boton-secundario boton-pequeno">Registrarse</a>
                </div>
            `;
            const btnTemaNav = document.getElementById('btn-toggle-tema-nav');
            if (btnTemaNav) btnTemaNav.addEventListener('click', alternarTema);
            return;
        }

        contenedor.innerHTML = `
            ${enlaces}
            <div style="display:flex; align-items:center; gap:10px; margin-left:12px;">
                ${badgeConexion}
                <button class="boton-toggle-tema boton-toggle-nav" id="btn-toggle-tema-nav" title="Cambiar tema Día / Noche">
                    ${textoTemaNav}
                </button>
                <a href="perfil.html" class="usuario-sesion" title="Ver y editar mi perfil" style="text-decoration:none; cursor:pointer;">
                    <span class="etiqueta-rol">${esc(API.rolPrincipal())}</span>
                    <span>${esc(usuario.nombreCompleto)}</span>
                </a>
                <button class="boton-secundario boton-pequeno" id="btn-salir" title="Cerrar sesión">Salir</button>
            </div>
        `;

        const btnTemaNav = document.getElementById('btn-toggle-tema-nav');
        if (btnTemaNav) btnTemaNav.addEventListener('click', alternarTema);

        const btnSalir = document.getElementById('btn-salir');
        if (btnSalir) {
            btnSalir.addEventListener('click', () => {
                API.auth.logout();
            });
        }
    }

    /**
     * Guardia de las paginas privadas.
     *
     * Sin sesion se devuelve al login: una pantalla protegida no puede abrir
     * una sesion por su cuenta, porque entonces el control de acceso no
     * controlaria nada. Con sesion pero sin el rol necesario se devuelve al
     * inicio. El backend vuelve a validar el token y el rol en cada peticion;
     * esta comprobacion solo evita mostrar una pantalla que no corresponde.
     */
    function exigirSesion(rolRequerido) {
        if (!API.haySesion()) {
            toast('Debe iniciar sesión para entrar a esta sección.', 'error');
            setTimeout(() => { window.location.href = 'login.html'; }, 600);
            return false;
        }
        if (rolRequerido && !API.tieneRol(rolRequerido) && !API.esAdmin()) {
            toast('Permisos insuficientes para esta sección.', 'error');
            setTimeout(() => { window.location.href = 'index.html'; }, 600);
            return false;
        }
        return true;
    }

    function abrirModal(id) {
        const m = document.getElementById(id);
        if (m) {
            m.classList.add('visible');
            document.body.style.overflow = 'hidden';
        }
    }

    function cerrarModal(id) {
        const m = document.getElementById(id);
        if (m) {
            m.classList.remove('visible');
            document.body.style.overflow = '';
        }
    }

    function leerFormulario(formulario) {
        const datos = {};
        new FormData(formulario).forEach((valor, clave) => {
            const v = typeof valor === 'string' ? valor.trim() : valor;
            if (v !== '') datos[clave] = v;
        });
        return datos;
    }

    function toast(texto, tipo = 'info') {
        let toastContainer = document.getElementById('toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.id = 'toast-container';
            toastContainer.style.cssText = 'position: fixed; bottom: 24px; right: 24px; z-index: 99999; display: flex; flex-direction: column; gap: 12px; pointer-events: none;';
            document.body.appendChild(toastContainer);
        }
        const item = document.createElement('div');
        item.className = 'mensaje mensaje-' + tipo + ' visible';
        item.style.cssText = 'margin: 0; box-shadow: 0 10px 25px rgba(0,0,0,0.5); pointer-events: auto; min-width: 280px; display: flex; align-items: center; gap: 10px; border-radius: 10px; padding: 14px 18px;';
        
        let icono = 'ℹ️';
        if (tipo === 'exito') icono = '✅';
        if (tipo === 'error') icono = '⚠️';

        item.innerHTML = `<span>${icono}</span><span>${esc(texto)}</span>`;
        toastContainer.appendChild(item);

        setTimeout(() => {
            item.style.opacity = '0';
            item.style.transform = 'translateY(10px)';
            item.style.transition = 'all 0.3s ease';
            setTimeout(() => item.remove(), 350);
        }, 3800);
    }

    // Cerrar modales con tecla Escape y clic en fondo
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            document.querySelectorAll('.fondo-modal.visible').forEach(m => m.classList.remove('visible'));
            document.body.style.overflow = '';
        }
    });

    document.addEventListener('click', (e) => {
        if (e.target.classList.contains('fondo-modal')) {
            e.target.classList.remove('visible');
            document.body.style.overflow = '';
        }
    });

    window.addEventListener('api-status-changed', () => {
        const badge = document.getElementById('badge-api-status');
        if (!badge) return;
        const online = API.esOnline();
        badge.className = 'badge-estado-conexion ' + (online ? 'online' : 'offline');
        badge.innerHTML = `
            <span class="pulso-dot ${online ? 'verde' : 'amarillo'}"></span>
            ${online ? '🟢 Servidor Conectado (API v1)' : '🟡 Modo Local Activo (100% Funcional)'}
        `;
    });

    return {
        mensaje, ocultarMensaje, pesos, fecha, fechaConHora, esc, insignia,
        cargando, vacio, pintarBarra, exigirSesion,
        abrirModal, cerrarModal, leerFormulario, toast,
        aplicarTema, alternarTema, obtenerTemaActual,
        carrito: Carrito, actualizarBadgesCarrito
    };

})();
