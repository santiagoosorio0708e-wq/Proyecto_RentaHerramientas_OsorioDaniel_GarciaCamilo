/* ==========================================================================
   api.js - Capa dual de comunicacion: Backend Spring Boot + Fallback Resiliente
   Autores: Camilo Garcia & Daniel Osorio

   Proporciona comunicacion fluida con el backend REST (/api/v1). Si el backend
   esta en proceso de inicio o apagado, conmuta automaticamente al motor de
   datos local MockStore para que la aplicacion JAMAS se rompa o quede congelada.
   ========================================================================== */

const API = (() => {

    'use strict';

    const URL_BASE = (() => {
        if (typeof window !== 'undefined' && window.location && window.location.origin) {
            if (window.location.port === '8080' || window.location.pathname.startsWith('/api/v1')) {
                return `${window.location.origin}/api/v1`;
            }
        }
        return 'http://localhost:8080/api/v1';
    })();

    const CLAVE_ACCESS  = 'rh_access_token';
    const CLAVE_REFRESH = 'rh_refresh_token';
    const CLAVE_USUARIO = 'rh_usuario';

    let _backendDisponible = null; // null: no verificado, true: online, false: offline

    // ------------------------------------------------------------------
    // Almacenamiento de la sesion
    // ------------------------------------------------------------------

    function guardarSesion(datos) {
        localStorage.setItem(CLAVE_ACCESS, datos.accessToken || ('token_' + Date.now()));
        localStorage.setItem(CLAVE_REFRESH, datos.refreshToken || ('refresh_' + Date.now()));
        localStorage.setItem(CLAVE_USUARIO, JSON.stringify({
            idUsuario: datos.idUsuario,
            nombreCompleto: datos.nombreCompleto,
            email: datos.email,
            roles: datos.roles || []
        }));
        notificarCambioSesion();
    }

    function borrarSesion() {
        localStorage.removeItem(CLAVE_ACCESS);
        localStorage.removeItem(CLAVE_REFRESH);
        localStorage.removeItem(CLAVE_USUARIO);
        notificarCambioSesion();
    }

    function notificarCambioSesion() {
        window.dispatchEvent(new CustomEvent('sesion-cambiada', { detail: obtenerUsuario() }));
    }

    function obtenerToken()        { return localStorage.getItem(CLAVE_ACCESS); }
    function obtenerRefreshToken() { return localStorage.getItem(CLAVE_REFRESH); }

    function obtenerUsuario() {
        const bruto = localStorage.getItem(CLAVE_USUARIO);
        try {
            return bruto ? JSON.parse(bruto) : null;
        } catch (e) {
            return null;
        }
    }

    function haySesion() { return !!obtenerToken(); }

    function tieneRol(rol) {
        const usuario = obtenerUsuario();
        if (!usuario || !usuario.roles) return false;
        const buscado = rol.startsWith('ROLE_') ? rol : 'ROLE_' + rol;
        return usuario.roles.includes(buscado);
    }

    function esAdmin()     { return tieneRol('ROLE_ADMIN'); }
    function esProveedor() { return tieneRol('ROLE_PROVEEDOR'); }
    function esCliente()   { return tieneRol('ROLE_CLIENTE'); }

    function rolPrincipal() {
        if (esAdmin())     return 'ADMIN';
        if (esProveedor()) return 'PROVEEDOR';
        if (esCliente())   return 'CLIENTE';
        return 'INVITADO';
    }

    // ------------------------------------------------------------------
    // Diagnostico y Estado de Conexion
    // ------------------------------------------------------------------

    async function verificarBackend() {
        try {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 1500);
            let res = await fetch(URL_BASE + '/catalogo', {
                method: 'GET',
                signal: controller.signal
            }).catch(() => null);

            if (!res || (!res.ok && res.status !== 401 && res.status !== 403)) {
                res = await fetch(URL_BASE + '/actuator/health', {
                    method: 'GET',
                    signal: controller.signal
                }).catch(() => null);
            }

            clearTimeout(timeoutId);

            _backendDisponible = (res && (res.ok || res.status === 401 || res.status === 403));
        } catch (e) {
            _backendDisponible = false;
        }
        notificarEstadoApi();
        return _backendDisponible;
    }

    function notificarEstadoApi() {
        window.dispatchEvent(new CustomEvent('api-status-changed', {
            detail: { online: _backendDisponible === true }
        }));
    }

    function esOnline() {
        return _backendDisponible === true;
    }

    class ErrorApi extends Error {
        constructor(mensaje, estado, datos) {
            super(mensaje);
            this.name = 'ErrorApi';
            this.estado = estado;
            this.datos = datos;
        }
    }

    // ------------------------------------------------------------------
    // Peticion con auto-fallback resiliente
    // ------------------------------------------------------------------

    async function peticion(ruta, opciones = {}) {
        const {
            metodo = 'GET',
            cuerpo = null,
            publica = false,
            reintentado = false,
            fallback = null
        } = opciones;

        // Si ya sabemos que el backend esta offline y hay fallback, usarlo de inmediato
        if (_backendDisponible === false && fallback) {
            try {
                return await fallback();
            } catch (err) {
                throw new ErrorApi(err.message, 500, null);
            }
        }

        const cabeceras = { 'Accept': 'application/json' };
        if (cuerpo !== null) {
            cabeceras['Content-Type'] = 'application/json';
        }

        const token = obtenerToken();
        if (!publica && token) {
            cabeceras['Authorization'] = 'Bearer ' + token;
        }

        let respuesta;
        try {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 2000);

            respuesta = await fetch(URL_BASE + ruta, {
                method: metodo,
                headers: cabeceras,
                body: cuerpo !== null ? JSON.stringify(cuerpo) : undefined,
                signal: controller.signal
            });
            clearTimeout(timeoutId);
            _backendDisponible = true;
            notificarEstadoApi();
        } catch (e) {
            // El backend no respondio -> activar modo fallback
            _backendDisponible = false;
            notificarEstadoApi();

            if (fallback) {
                console.info(`[API Fallback] Servidor offline para ${ruta}. Atendiendo con MockStore local.`);
                try {
                    return await fallback();
                } catch (err) {
                    throw new ErrorApi(err.message, 500, null);
                }
            }

            throw new ErrorApi(
                'No se pudo conectar con el servidor en ' + URL_BASE,
                0, null
            );
        }

        if (respuesta.status === 204) {
            return null;
        }

        let datos = null;
        const tipo = respuesta.headers.get('content-type') || '';
        if (tipo.includes('application/json')) {
            try {
                datos = await respuesta.json();
            } catch (e) {
                datos = null;
            }
        }

        if (respuesta.status === 401 && !publica && !reintentado && obtenerRefreshToken()) {
            const renovado = await intentarRefrescar();
            if (renovado) {
                return peticion(ruta, { ...opciones, reintentado: true });
            }
            borrarSesion();
            throw new ErrorApi('Su sesion expiro. Vuelva a iniciar sesion.', 401, datos);
        }

        if (!respuesta.ok) {
            const mensaje = (datos && datos.message)
                ? datos.message
                : 'Error ' + respuesta.status + ' al comunicarse con el servidor';
            throw new ErrorApi(mensaje, respuesta.status, datos);
        }

        return (datos && Object.prototype.hasOwnProperty.call(datos, 'data'))
            ? datos.data
            : datos;
    }

    async function intentarRefrescar() {
        try {
            const respuesta = await fetch(URL_BASE + '/auth/refresh', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ refreshToken: obtenerRefreshToken() })
            });
            if (!respuesta.ok) return false;
            const json = await respuesta.json();
            if (json && json.data && json.data.accessToken) {
                guardarSesion(json.data);
                return true;
            }
            return false;
        } catch (e) {
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Modulos de Negocio
    // ------------------------------------------------------------------

    const auth = {
        login: async (email, password) => {
            return peticion('/auth/login', {
                metodo: 'POST',
                cuerpo: { email, password },
                publica: true,
                fallback: () => MockStore.login(email, password)
            }).then(datos => {
                guardarSesion(datos);
                return datos;
            });
        },
        registrarCliente: (cuerpo) => peticion('/auth/registro/cliente', {
            metodo: 'POST',
            cuerpo,
            publica: true,
            fallback: () => {
                const nuevo = {
                    idUsuario: Date.now(),
                    nombreCompleto: cuerpo.nombreCompleto || 'Nuevo Cliente',
                    email: cuerpo.email,
                    roles: ['ROLE_CLIENTE']
                };
                guardarSesion(nuevo);
                return nuevo;
            }
        }),
        registrarProveedor: (cuerpo) => peticion('/auth/registro/proveedor', {
            metodo: 'POST',
            cuerpo,
            publica: true,
            fallback: () => {
                const nuevo = {
                    idUsuario: Date.now(),
                    nombreCompleto: cuerpo.razonSocial || 'Nuevo Proveedor',
                    email: cuerpo.email,
                    roles: ['ROLE_PROVEEDOR']
                };
                guardarSesion(nuevo);
                return nuevo;
            }
        }),
        logout: () => {
            borrarSesion();
            window.location.href = 'login.html';
        },
        iniciarComoDemo: (rol) => {
            let datos;
            if (rol === 'ADMIN') {
                datos = {
                    idUsuario: 1,
                    nombreCompleto: 'Carlos Andrés Pérez (Admin)',
                    email: 'admin.perez@rentatool.com',
                    roles: ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN'],
                    accessToken: 'demo_admin_' + Date.now(),
                    refreshToken: 'demo_refresh_' + Date.now()
                };
            } else if (rol === 'PROVEEDOR') {
                datos = {
                    idUsuario: 4,
                    nombreCompleto: 'MaquiAntioquia SAS (Proveedor)',
                    email: 'ventas@maquiantioquia.com',
                    roles: ['ROLE_PROVEEDOR'],
                    accessToken: 'demo_prov_' + Date.now(),
                    refreshToken: 'demo_refresh_' + Date.now()
                };
            } else {
                datos = {
                    idUsuario: 8,
                    nombreCompleto: 'Daniel Felipe Osorio (Cliente)',
                    email: 'daniel.osorio@gmail.com',
                    roles: ['ROLE_CLIENTE'],
                    accessToken: 'demo_cli_' + Date.now(),
                    refreshToken: 'demo_refresh_' + Date.now()
                };
            }
            guardarSesion(datos);
            return datos;
        }
    };

    const usuarios = {
        miPerfil: () => peticion('/usuarios/me', {
            fallback: () => obtenerUsuario()
        }),
        listar: (rol) => peticion('/usuarios' + (rol ? '?rol=' + encodeURIComponent(rol) : ''), {
            fallback: () => MockStore.listarUsuarios(rol)
        }),
        bloquear: (id) => peticion('/usuarios/' + id + '/bloquear', {
            metodo: 'PATCH',
            fallback: () => MockStore.cambiarEstadoUsuario(id, false)
        }),
        activar: (id) => peticion('/usuarios/' + id + '/activar', {
            metodo: 'PATCH',
            fallback: () => MockStore.cambiarEstadoUsuario(id, true)
        }),
        verificarProveedor: (id) => peticion('/usuarios/' + id + '/verificar-proveedor', {
            metodo: 'PATCH',
            fallback: () => MockStore.verificarProveedor(id)
        })
    };

    const catalogo = {
        buscar: (filtros = {}) => {
            const params = new URLSearchParams();
            Object.entries(filtros).forEach(([k, v]) => {
                if (v !== null && v !== undefined && v !== '') params.append(k, v);
            });
            const q = params.toString();
            return peticion('/catalogo' + (q ? '?' + q : ''), {
                publica: true,
                fallback: () => MockStore.buscarCatalogo(filtros)
            });
        },
        detalle: (id) => peticion('/catalogo/' + id, {
            publica: true,
            fallback: () => MockStore.obtenerHerramienta(id)
        }),
        porCategoria: (idCategoria) => peticion('/catalogo/categoria/' + idCategoria, {
            publica: true,
            fallback: () => MockStore.buscarCatalogo({ idCategoria })
        })
    };

    const herramientas = {
        crear: (cuerpo) => peticion('/herramientas', {
            metodo: 'POST',
            cuerpo,
            fallback: () => MockStore.guardarHerramienta(cuerpo)
        }),
        miInventario: () => peticion('/herramientas/mi-inventario', {
            fallback: () => MockStore.buscarCatalogo()
        }),
        detalle: (id) => peticion('/herramientas/' + id, {
            fallback: () => MockStore.obtenerHerramienta(id)
        }),
        actualizar: (id, cuerpo) => peticion('/herramientas/' + id, {
            metodo: 'PUT',
            cuerpo,
            fallback: () => MockStore.guardarHerramienta({ ...cuerpo, idHerramienta: id })
        }),
        eliminar: (id) => peticion('/herramientas/' + id, {
            metodo: 'DELETE',
            fallback: () => MockStore.eliminarHerramienta(id)
        }),
        cambiarEstado: (id, cuerpo) => peticion('/herramientas/' + id + '/estado', {
            metodo: 'PATCH',
            cuerpo,
            fallback: () => MockStore.guardarHerramienta({ idHerramienta: id, estadoOperativo: cuerpo.estadoOperativo })
        })
    };

    const categorias = {
        listar: () => peticion('/categorias', {
            publica: true,
            fallback: () => MockStore.listarCategorias()
        }),
        obtener: (id) => peticion('/categorias/' + id, {
            publica: true,
            fallback: () => MockStore.listarCategorias().find(c => c.idCategoria === Number(id))
        })
    };

    const reservas = {
        crear: (cuerpo) => peticion('/reservas', {
            metodo: 'POST',
            cuerpo,
            fallback: () => MockStore.crearReserva(cuerpo)
        }),
        misReservas: () => peticion('/reservas/mis-reservas', {
            fallback: () => MockStore.listarReservas()
        }),
        listar: () => peticion('/reservas', {
            fallback: () => MockStore.listarReservas()
        }),
        obtener: (id) => peticion('/reservas/' + id, {
            fallback: () => MockStore.listarReservas().find(r => r.idReserva === Number(id))
        }),
        aprobar: (id) => peticion('/reservas/' + id + '/aprobar', {
            metodo: 'PATCH',
            fallback: () => MockStore.actualizarEstadoReserva(id, 'APROBADA')
        }),
        rechazar: (id, mot) => peticion('/reservas/' + id + '/rechazar?motivo=' + encodeURIComponent(mot || 'Rechazada'), {
            metodo: 'PATCH',
            fallback: () => MockStore.actualizarEstadoReserva(id, 'RECHAZADA')
        }),
        iniciar: (id) => peticion('/reservas/' + id + '/iniciar', {
            metodo: 'PATCH',
            fallback: () => MockStore.actualizarEstadoReserva(id, 'EN_CURSO')
        }),
        cancelar: (id) => peticion('/reservas/' + id + '/cancelar', {
            metodo: 'PATCH',
            fallback: () => MockStore.actualizarEstadoReserva(id, 'CANCELADA')
        }),
        finalizar: (id) => peticion('/reservas/' + id + '/finalizar', {
            metodo: 'PATCH',
            fallback: () => MockStore.actualizarEstadoReserva(id, 'FINALIZADA')
        })
    };

    const pagos = {
        procesar: (cuerpo) => peticion('/pagos/procesar', {
            metodo: 'POST',
            cuerpo,
            fallback: () => MockStore.procesarPago(cuerpo)
        }),
        obtener: (id) => peticion('/pagos/' + id, {
            fallback: () => MockStore.obtenerFacturaPorPago(id)
        }),
        porReserva: (idReserva) => peticion('/pagos/reserva/' + idReserva, {
            fallback: () => MockStore.obtenerPagosPorReserva(idReserva)
        })
    };

    const facturas = {
        obtener: (id) => peticion('/facturas/' + id, {
            fallback: () => MockStore.obtenerFacturaPorReserva(id)
        }),
        porReserva: (idReserva) => peticion('/facturas/reserva/' + idReserva, {
            fallback: () => MockStore.obtenerFacturaPorReserva(idReserva)
        }),
        porPago: (idPago) => peticion('/facturas/pago/' + idPago, {
            fallback: () => MockStore.obtenerFacturaPorPago(idPago)
        }),
        misFacturas: () => peticion('/facturas/mis-facturas', {
            fallback: () => [MockStore.obtenerFacturaPorReserva(101)]
        })
    };

    const devoluciones = {
        registrar: (cuerpo) => peticion('/devoluciones', {
            metodo: 'POST',
            cuerpo,
            fallback: () => MockStore.registrarDevolucion(cuerpo)
        }),
        listar: () => peticion('/devoluciones', {
            fallback: () => MockStore.listarDevoluciones()
        }),
        porReserva: (idReserva) => peticion('/devoluciones/reserva/' + idReserva, {
            fallback: () => MockStore.listarDevoluciones().find(d => d.idReserva === Number(idReserva))
        }),
        agregarDano: (id, cuerpo) => peticion('/devoluciones/' + id + '/danos', {
            metodo: 'POST',
            cuerpo,
            fallback: () => MockStore.agregarDano(id, cuerpo)
        })
    };

    const reportes = {
        dashboard: () => peticion('/reportes/dashboard', {
            fallback: () => MockStore.obtenerDashboard()
        }),
        financiero: () => peticion('/reportes/financiero', {
            fallback: () => MockStore.obtenerDashboard().resumenFinanciero
        }),
        metricas: () => peticion('/reportes/metricas-operativas', {
            fallback: () => MockStore.obtenerDashboard().metricasOperativas
        }),
        populares: (limite = 10) => peticion('/reportes/herramientas-populares?limite=' + limite, {
            fallback: () => MockStore.obtenerDashboard().topHerramientasPopulares
        }),
        clientes: (limite = 10) => peticion('/reportes/clientes-frecuentes?limite=' + limite, {
            fallback: () => MockStore.obtenerDashboard().topClientesFrecuentes
        }),
        proveedores: () => peticion('/reportes/proveedores', {
            fallback: () => MockStore.obtenerDashboard().ingresosPorProveedor
        })
    };

    // Diagnosticar disponibilidad en background
    verificarBackend();

    return {
        URL_BASE,
        ErrorApi,
        verificarBackend, esOnline,
        haySesion, obtenerUsuario, obtenerToken, borrarSesion, guardarSesion,
        tieneRol, esAdmin, esProveedor, esCliente, rolPrincipal,
        auth, usuarios, catalogo, herramientas, categorias,
        reservas, pagos, facturas, devoluciones, reportes
    };

})();
