/* ==========================================================================
   api.js - Capa unica de comunicacion con el backend
   Autor: Camilo Garcia

   Aqui vive el "interceptor" de peticiones: una funcion que envuelve a fetch
   y que, antes de cada llamada, adjunta automaticamente el token JWT guardado
   en localStorage. Asi ninguna pantalla tiene que acordarse de poner el header
   Authorization a mano.

   Tambien centraliza:
     - el manejo del 401 (token vencido -> intenta refrescar una sola vez)
     - el manejo del 403 (sin permisos)
     - la lectura del formato de respuesta { success, message, data }
   ========================================================================== */

const API = (() => {

    'use strict';

    // El backend tiene context-path /api/v1 (ver application.properties).
    const URL_BASE = 'http://localhost:8080/api/v1';

    const CLAVE_ACCESS  = 'rh_access_token';
    const CLAVE_REFRESH = 'rh_refresh_token';
    const CLAVE_USUARIO = 'rh_usuario';

    // ------------------------------------------------------------------
    // Almacenamiento de la sesion
    // ------------------------------------------------------------------

    function guardarSesion(datos) {
        localStorage.setItem(CLAVE_ACCESS, datos.accessToken);
        localStorage.setItem(CLAVE_REFRESH, datos.refreshToken);
        localStorage.setItem(CLAVE_USUARIO, JSON.stringify({
            idUsuario: datos.idUsuario,
            nombreCompleto: datos.nombreCompleto,
            email: datos.email,
            roles: datos.roles || []
        }));
    }

    function borrarSesion() {
        localStorage.removeItem(CLAVE_ACCESS);
        localStorage.removeItem(CLAVE_REFRESH);
        localStorage.removeItem(CLAVE_USUARIO);
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

    /** Nombre del rol principal, para mostrarlo en la barra superior. */
    function rolPrincipal() {
        if (esAdmin())     return 'ADMIN';
        if (esProveedor()) return 'PROVEEDOR';
        if (esCliente())   return 'CLIENTE';
        return 'INVITADO';
    }

    // ------------------------------------------------------------------
    // Error de API
    // ------------------------------------------------------------------

    class ErrorApi extends Error {
        constructor(mensaje, estado, datos) {
            super(mensaje);
            this.name = 'ErrorApi';
            this.estado = estado;
            this.datos = datos;
        }
    }

    // ------------------------------------------------------------------
    // Interceptor principal
    // ------------------------------------------------------------------

    /**
     * Envia una peticion al backend adjuntando el token si existe.
     *
     * @param {string} ruta       ruta relativa, por ejemplo '/catalogo'
     * @param {object} opciones   { metodo, cuerpo, publica, reintentado }
     */
    async function peticion(ruta, opciones = {}) {

        const {
            metodo = 'GET',
            cuerpo = null,
            publica = false,
            reintentado = false
        } = opciones;

        const cabeceras = { 'Accept': 'application/json' };

        if (cuerpo !== null) {
            cabeceras['Content-Type'] = 'application/json';
        }

        // --- Interceptor: se adjunta el JWT automaticamente ---
        const token = obtenerToken();
        if (!publica && token) {
            cabeceras['Authorization'] = 'Bearer ' + token;
        }

        let respuesta;
        try {
            respuesta = await fetch(URL_BASE + ruta, {
                method: metodo,
                headers: cabeceras,
                body: cuerpo !== null ? JSON.stringify(cuerpo) : undefined
            });
        } catch (e) {
            // Falla de red: el backend esta apagado o CORS bloqueo la peticion.
            throw new ErrorApi(
                'No se pudo conectar con el servidor. Verifique que el backend este ejecutandose en ' + URL_BASE,
                0, null);
        }

        // 204 No Content no trae cuerpo que parsear.
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

        // --- 401: el token vencio. Se intenta refrescar UNA sola vez. ---
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

        // El backend responde siempre con { success, message, data }.
        return (datos && Object.prototype.hasOwnProperty.call(datos, 'data'))
            ? datos.data
            : datos;
    }

    /** Pide un nuevo access token usando el refresh token guardado. */
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
    // Atajos por verbo HTTP
    // ------------------------------------------------------------------

    const get    = (ruta, publica = false)         => peticion(ruta, { metodo: 'GET', publica });
    const post   = (ruta, cuerpo, publica = false) => peticion(ruta, { metodo: 'POST', cuerpo, publica });
    const put    = (ruta, cuerpo)                  => peticion(ruta, { metodo: 'PUT', cuerpo });
    const patch  = (ruta, cuerpo = {})             => peticion(ruta, { metodo: 'PATCH', cuerpo });
    const borrar = (ruta)                          => peticion(ruta, { metodo: 'DELETE' });

    // ------------------------------------------------------------------
    // Endpoints agrupados por modulo
    // ------------------------------------------------------------------

    const auth = {
        login: async (email, password) => {
            const datos = await post('/auth/login', { email, password }, true);
            guardarSesion(datos);
            return datos;
        },
        registrarCliente:   (cuerpo) => post('/auth/registro/cliente', cuerpo, true),
        registrarProveedor: (cuerpo) => post('/auth/registro/proveedor', cuerpo, true),
        logout: () => {
            borrarSesion();
            window.location.href = 'login.html';
        }
    };

    const usuarios = {
        miPerfil:          ()        => get('/usuarios/me'),
        actualizarPerfil:  (cuerpo)  => put('/usuarios/me', cuerpo),
        cambiarPassword:   (cuerpo)  => patch('/usuarios/me/password', cuerpo),
        listar:            (rol)     => get('/usuarios' + (rol ? '?rol=' + encodeURIComponent(rol) : '')),
        obtener:           (id)      => get('/usuarios/' + id),
        bloquear:          (id)      => patch('/usuarios/' + id + '/bloquear'),
        activar:           (id)      => patch('/usuarios/' + id + '/activar'),
        verificarProveedor:(id)      => patch('/usuarios/' + id + '/verificar-proveedor')
    };

    const catalogo = {
        buscar: (filtros = {}) => {
            const params = new URLSearchParams();
            Object.entries(filtros).forEach(([clave, valor]) => {
                if (valor !== null && valor !== undefined && valor !== '') {
                    params.append(clave, valor);
                }
            });
            const cadena = params.toString();
            return get('/catalogo' + (cadena ? '?' + cadena : ''), true);
        },
        detalle:     (id)           => get('/catalogo/' + id, true),
        porCategoria:(idCategoria)  => get('/catalogo/categoria/' + idCategoria, true)
    };

    const herramientas = {
        crear:        (cuerpo)      => post('/herramientas', cuerpo),
        miInventario: ()            => get('/herramientas/mi-inventario'),
        detalle:      (id)          => get('/herramientas/' + id),
        actualizar:   (id, cuerpo)  => put('/herramientas/' + id, cuerpo),
        eliminar:     (id)          => borrar('/herramientas/' + id),
        cambiarEstado:(id, cuerpo)  => patch('/herramientas/' + id + '/estado', cuerpo),
        porEstado:    (estado)      => get('/herramientas/estado/' + estado),
        agregarImagen:(id, cuerpo)  => post('/herramientas/' + id + '/imagenes', cuerpo),
        agregarEspecificacion: (id, cuerpo) => post('/herramientas/' + id + '/especificaciones', cuerpo),
        agregarTarifa:(id, cuerpo)  => post('/herramientas/' + id + '/tarifas', cuerpo)
    };

    const categorias = {
        listar:     ()          => get('/categorias', true),
        obtener:    (id)        => get('/categorias/' + id, true),
        crear:      (cuerpo)    => post('/categorias', cuerpo),
        actualizar: (id, cuerpo)=> put('/categorias/' + id, cuerpo),
        eliminar:   (id)        => borrar('/categorias/' + id)
    };

    return {
        URL_BASE,
        ErrorApi,
        haySesion, obtenerUsuario, obtenerToken, borrarSesion,
        tieneRol, esAdmin, esProveedor, esCliente, rolPrincipal,
        auth, usuarios, catalogo, herramientas, categorias
    };

})();
