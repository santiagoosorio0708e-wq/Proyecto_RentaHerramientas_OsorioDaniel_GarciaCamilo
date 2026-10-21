/* ==========================================================================
   perfil.js - Gestión de perfil, carrito de compras, historial y facturas
   Autores: Camilo Garcia & Daniel Osorio
   ========================================================================== */

document.addEventListener('DOMContentLoaded', async () => {

    'use strict';

    if (!UI.exigirSesion()) return;
    UI.pintarBarra('perfil');

    let usuarioActual = API.obtenerUsuario() || {};
    let reservasCache = [];

    // ------------------------------------------------------------------
    // Inicialización de la Interfaz y Pestañas
    // ------------------------------------------------------------------

    const tabs = document.querySelectorAll('.pestana-perfil');
    const paneles = document.querySelectorAll('.panel-tab-perfil');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const tabId = tab.dataset.tab;
            activarPestana(tabId);
        });
    });

    function activarPestana(tabId) {
        tabs.forEach(t => t.classList.toggle('activa', t.dataset.tab === tabId));
        paneles.forEach(p => p.classList.toggle('activo', p.id === `panel-${tabId}`));

        // Actualizar URL sin recargar
        const url = new URL(window.location.href);
        url.searchParams.set('tab', tabId);
        window.history.replaceState({}, '', url);

        if (tabId === 'carrito') renderizarCarrito();
        if (tabId === 'historial') cargarHistorialAlquileres();
        if (tabId === 'facturas') cargarFacturasDian();
    }

    // Detectar tab en URL o abrir 'datos' por defecto
    const paramTab = new URLSearchParams(window.location.search).get('tab');
    if (paramTab && ['datos', 'carrito', 'historial', 'facturas'].includes(paramTab)) {
        activarPestana(paramTab);
    } else {
        activarPestana('datos');
    }

    // ------------------------------------------------------------------
    // Carga de Datos del Usuario
    // ------------------------------------------------------------------

    async function cargarDatosPerfil() {
        try {
            const perfil = await API.usuarios.miPerfil();
            usuarioActual = { ...usuarioActual, ...perfil };
        } catch (e) {
            // Se mantiene con usuarioActual local
        }
        refrescarVistaUsuario();
    }

    function refrescarVistaUsuario() {
        const u = usuarioActual;
        const nombre = u.nombreCompleto || 'Usuario RentaTools';
        const email = u.email || 'usuario@rentatool.com';
        const rol = (u.roles && u.roles[0]) ? u.roles[0].replace('ROLE_', '') : (API.rolPrincipal() || 'CLIENTE');

        // Letras iniciales para el avatar
        const iniciales = nombre.split(' ')
            .filter(Boolean)
            .slice(0, 2)
            .map(p => p[0].toUpperCase())
            .join('') || 'U';

        document.getElementById('perfil-avatar-letras').textContent = iniciales;
        document.getElementById('perfil-nombre-cabecera').textContent = nombre;
        document.getElementById('perfil-email-cabecera').textContent = email;
        document.getElementById('perfil-rol-badge').textContent = rol;

        // Formulario
        document.getElementById('perfil-nombre').value = nombre;
        document.getElementById('perfil-email').value = email;
        document.getElementById('perfil-telefono').value = u.telefono || '+57 318 555 0109';
        document.getElementById('perfil-direccion').value = u.direccion || 'Carrera 33 # 48-19, Bucaramanga';

        const docNum = u.numeroDocumento || '1098765432';
        document.getElementById('perfil-doc-badge').textContent = `ID / CC: ${docNum}`;

        const cupo = u.cupoCredito || 15000000;
        document.getElementById('perfil-cupo-badge').textContent = `Cupo Crédito: ${UI.pesos(cupo)}`;
    }

    await cargarDatosPerfil();

    // ------------------------------------------------------------------
    // Formulario: Actualizar Datos Personales
    // ------------------------------------------------------------------

    const formDatos = document.getElementById('form-actualizar-datos');
    formDatos.addEventListener('submit', async (e) => {
        e.preventDefault();
        UI.ocultarMensaje('mensaje-datos');

        const btn = document.getElementById('btn-guardar-perfil');
        const txt = btn.textContent;
        btn.disabled = true;
        btn.textContent = '⏳ Guardando cambios...';

        try {
            const nuevoNombre = document.getElementById('perfil-nombre').value.trim();
            const nuevoEmail  = document.getElementById('perfil-email').value.trim();
            const nuevoTel    = document.getElementById('perfil-telefono').value.trim();
            const nuevaDir    = document.getElementById('perfil-direccion').value.trim();

            const resultado = await API.usuarios.actualizarPerfil({
                nombreCompleto: nuevoNombre,
                email: nuevoEmail,
                telefono: nuevoTel,
                direccion: nuevaDir
            });

            usuarioActual = { ...usuarioActual, ...resultado };
            refrescarVistaUsuario();
            UI.pintarBarra('perfil');

            UI.toast('¡Datos de perfil y correo actualizados correctamente!', 'exito');
            UI.mensaje('mensaje-datos', 'Sus datos personales se han guardado exitosamente en el sistema.', 'exito');

        } catch (error) {
            UI.mensaje('mensaje-datos', 'Error al actualizar perfil: ' + error.message, 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = txt;
        }
    });

    // ------------------------------------------------------------------
    // Formulario: Cambiar Contraseña
    // ------------------------------------------------------------------

    const formPwd = document.getElementById('form-cambiar-password');
    formPwd.addEventListener('submit', async (e) => {
        e.preventDefault();
        UI.ocultarMensaje('mensaje-password');

        const pwdActual = document.getElementById('pwd-actual').value;
        const pwdNueva = document.getElementById('pwd-nueva').value;
        const pwdConf = document.getElementById('pwd-confirmar').value;

        if (pwdNueva !== pwdConf) {
            UI.mensaje('mensaje-password', 'La nueva contraseña y su confirmación no coinciden.', 'error');
            return;
        }

        const btn = document.getElementById('btn-cambiar-pwd');
        const txt = btn.textContent;
        btn.disabled = true;
        btn.textContent = '⏳ Validando...';

        try {
            await API.usuarios.cambiarPassword({
                passwordActual: pwdActual,
                passwordNueva: pwdNueva
            });

            formPwd.reset();
            UI.toast('¡Contraseña actualizada exitosamente!', 'exito');
            UI.mensaje('mensaje-password', 'Su clave de acceso ha sido actualizada. Puede usarla en su próximo ingreso.', 'exito');
        } catch (error) {
            UI.mensaje('mensaje-password', error.message, 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = txt;
        }
    });

    // Botón Cerrar Sesión del Hero
    document.getElementById('btn-cerrar-sesion-hero').addEventListener('click', () => {
        API.auth.logout();
    });

    // ------------------------------------------------------------------
    // CARRITO DE COMPRAS & COTIZADOR
    // ------------------------------------------------------------------

    function renderizarCarrito() {
        const contenedor = document.getElementById('contenedor-carrito-items');
        const cajaTotales = document.getElementById('caja-totales-carrito');
        const items = UI.carrito.obtener();

        actualizarContadores();

        if (!items || items.length === 0) {
            cajaTotales.style.display = 'none';
            contenedor.innerHTML = `
                <div class="estado-vacio" style="padding: 50px 20px;">
                    <div class="icono" style="font-size:3.5rem;">🛒</div>
                    <h3 style="margin-top:12px;">Tu carrito de equipos está vacío</h3>
                    <p style="color:var(--color-texto-suave); max-width:480px; margin: 8px auto 20px;">
                        Aún no has agregado herramientas ni maquinaria. Explora el catálogo para agregar equipos a tu orden de alquiler.
                    </p>
                    <a href="index.html" class="boton" style="display:inline-flex; align-items:center; gap:8px;">
                        🚜 Ver Catálogo de Maquinaria
                    </a>
                </div>
            `;
            return;
        }

        cajaTotales.style.display = 'block';

        let subtotalAcumulado = 0;
        let depositosAcumulados = 0;
        let diasTotales = 0;

        const filasHtml = items.map((item, index) => {
            const f1 = new Date(item.fechaInicio);
            const f2 = new Date(item.fechaFin);
            let dias = Math.ceil((f2 - f1) / (1000 * 60 * 60 * 24));
            if (isNaN(dias) || dias <= 0) dias = 1;

            const sub = item.tarifaDiaria * dias * (item.cantidad || 1);
            const dep = Math.max(100000, Math.round(sub * 0.2));

            subtotalAcumulado += sub;
            depositosAcumulados += dep;
            diasTotales += dias;

            return `
            <tr>
                <td>
                    <div class="carrito-item-info">
                        <div class="carrito-item-icono">🚜</div>
                        <div>
                            <strong>${UI.esc(item.nombre)}</strong>
                            <div style="font-size:0.78rem; color:var(--color-texto-suave);">
                                ${UI.esc(item.marca)} &middot; Mod. ${UI.esc(item.modelo)} &middot; ${UI.esc(item.categoria)}
                            </div>
                        </div>
                    </div>
                </td>
                <td>
                    <strong style="color:var(--color-acento-claro);">${UI.pesos(item.tarifaDiaria)}</strong>
                    <div style="font-size:0.75rem; color:var(--color-texto-suave);">/ día</div>
                </td>
                <td>
                    <div style="display:flex; flex-direction:column; gap:6px;">
                        <input type="date" class="input-fecha-inicio-cart" data-id="${item.idHerramienta}" value="${item.fechaInicio}" style="padding:6px 10px; font-size:0.85rem;">
                        <input type="date" class="input-fecha-fin-cart" data-id="${item.idHerramienta}" value="${item.fechaFin}" style="padding:6px 10px; font-size:0.85rem;">
                    </div>
                </td>
                <td style="text-align:center;">
                    <span class="insignia insignia-en_curso">${dias} ${dias === 1 ? 'día' : 'días'}</span>
                </td>
                <td>
                    <strong style="font-size:1.05rem;">${UI.pesos(sub)}</strong>
                    <div style="font-size:0.75rem; color:var(--color-acento-claro);">Garantía: ${UI.pesos(dep)}</div>
                </td>
                <td style="text-align:center;">
                    <button class="boton-secundario boton-pequeno btn-eliminar-item-cart" data-id="${item.idHerramienta}" title="Eliminar equipo del carrito" style="color:var(--color-error); padding:6px 10px;">
                        🗑️
                    </button>
                </td>
            </tr>
            `;
        }).join('');

        contenedor.innerHTML = `
            <div style="overflow-x:auto;">
                <table class="tabla tabla-carrito">
                    <thead>
                        <tr>
                            <th>Equipo / Herramienta</th>
                            <th>Tarifa Diaria</th>
                            <th>Periodo (Inicio & Retorno)</th>
                            <th style="text-align:center;">Días</th>
                            <th>Subtotal & Depósito</th>
                            <th style="text-align:center;">Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${filasHtml}
                    </tbody>
                </table>
            </div>
        `;

        // Actualizar resumen numérico
        const iva = Math.round(subtotalAcumulado * 0.19);
        const granTotal = subtotalAcumulado + depositosAcumulados + iva;

        document.getElementById('resumen-cant-equipos').textContent = `${items.length} ${items.length === 1 ? 'máquina' : 'máquinas'}`;
        document.getElementById('resumen-subtotal-alquiler').textContent = UI.pesos(subtotalAcumulado);
        document.getElementById('resumen-total-depositos').textContent = UI.pesos(depositosAcumulados);
        document.getElementById('resumen-iva-alquiler').textContent = UI.pesos(iva);
        document.getElementById('resumen-gran-total').textContent = UI.pesos(granTotal);

        // Listeners para inputs de fechas en carrito
        contenedor.querySelectorAll('.input-fecha-inicio-cart').forEach(input => {
            input.addEventListener('change', (e) => {
                const id = e.target.dataset.id;
                const item = items.find(i => Number(i.idHerramienta) === Number(id));
                if (item) {
                    UI.carrito.actualizarItem(id, { fechaInicio: e.target.value });
                    renderizarCarrito();
                }
            });
        });

        contenedor.querySelectorAll('.input-fecha-fin-cart').forEach(input => {
            input.addEventListener('change', (e) => {
                const id = e.target.dataset.id;
                const item = items.find(i => Number(i.idHerramienta) === Number(id));
                if (item) {
                    UI.carrito.actualizarItem(id, { fechaFin: e.target.value });
                    renderizarCarrito();
                }
            });
        });

        // Listener para eliminar ítem
        contenedor.querySelectorAll('.btn-eliminar-item-cart').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = btn.dataset.id;
                UI.carrito.eliminar(id);
                UI.toast('Equipo eliminado del carrito', 'info');
                renderizarCarrito();
            });
        });
    }

    // Botón Vaciar Carrito
    document.getElementById('btn-vaciar-carrito').addEventListener('click', () => {
        if (!confirm('¿Está seguro de que desea vaciar todos los equipos del carrito?')) return;
        UI.carrito.vaciar();
        UI.toast('El carrito ha sido vaciado.', 'info');
        renderizarCarrito();
    });

    // Botón Confirmar y Crear Todas las Solicitudes
    document.getElementById('btn-confirmar-todo-carrito').addEventListener('click', async () => {
        const items = UI.carrito.obtener();
        if (!items || items.length === 0) return;

        const btn = document.getElementById('btn-confirmar-todo-carrito');
        btn.disabled = true;
        btn.textContent = '⏳ Procesando solicitudes de alquiler...';

        try {
            const usuario = API.obtenerUsuario();
            for (const item of items) {
                const comando = {
                    idCliente: usuario ? usuario.idUsuario : 8,
                    fechaInicio: item.fechaInicio,
                    fechaFin: item.fechaFin,
                    notasCliente: `Alquiler procesado desde Carrito de Compras: ${item.nombre}`,
                    items: [
                        {
                            idHerramienta: item.idHerramienta,
                            cantidad: item.cantidad || 1
                        }
                    ]
                };
                await API.reservas.crear(comando);
            }

            UI.carrito.vaciar();
            UI.toast(`¡Éxito! Se han creado ${items.length} solicitudes de alquiler para tu obra.`, 'exito');

            setTimeout(() => {
                activarPestana('historial');
            }, 800);

        } catch (error) {
            UI.toast('Error al confirmar reservas: ' + error.message, 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = '🚀 Confirmar y Crear Solicitudes de Alquiler';
        }
    });

    // Escuchar eventos globales de carrito
    window.addEventListener('carrito-actualizado', () => {
        actualizarContadores();
        if (document.getElementById('panel-carrito').classList.contains('activo')) {
            renderizarCarrito();
        }
    });

    // ------------------------------------------------------------------
    // HISTORIAL DE ALQUILERES & ALQUILERES PASADOS
    // ------------------------------------------------------------------

    async function cargarHistorialAlquileres() {
        const contenedor = document.getElementById('contenedor-historial-alquileres');
        UI.cargando(contenedor, 'Consultando historial de maquinaria y alquileres...');

        try {
            reservasCache = await API.reservas.misReservas();
            actualizarContadores();
            renderizarHistorial('todos');
        } catch (error) {
            contenedor.innerHTML = `<div class="mensaje mensaje-error visible">${UI.esc(error.message)}</div>`;
        }
    }

    function renderizarHistorial(filtro = 'todos') {
        const contenedor = document.getElementById('contenedor-historial-alquileres');
        let lista = [...reservasCache];

        if (filtro === 'finalizados') {
            lista = lista.filter(r => ['DEVUELTO', 'FINALIZADA', 'COMPLETADA'].includes(r.estado) || (r.estadoDevolucion && r.estadoDevolucion === 'CONCILIADA'));
        } else if (filtro === 'activos') {
            lista = lista.filter(r => ['EN_CURSO', 'APROBADA', 'PENDIENTE'].includes(r.estado));
        } else if (filtro === 'cancelados') {
            lista = lista.filter(r => ['CANCELADA', 'RECHAZADA'].includes(r.estado));
        }

        if (lista.length === 0) {
            contenedor.innerHTML = `
                <div class="estado-vacio">
                    <div class="icono">📦</div>
                    <p>No se encontraron alquileres en esta categoría de filtro.</p>
                </div>
            `;
            return;
        }

        const filasHtml = lista.map(r => {
            const h = r.items && r.items[0] ? r.items[0] : {};
            const idRes = r.idReserva || r.id || 101;
            const nombreEquipo = h.nombreHerramienta || r.nombreHerramienta || 'Maquinaria Industrial';
            const dias = Number(h.dias) || Number(r.diasAlquiler) || Math.max(1, Math.ceil((new Date(r.fechaFin) - new Date(r.fechaInicio)) / (1000 * 60 * 60 * 24))) || 3;
            const monto = Number(r.montoTotal) || Number(r.montoTotalEstimado) || (h.subtotal ? Number(h.subtotal) : 0);
            const tarifa = Number(h.tarifaDiaria) || Number(h.tarifaPactada) || (dias > 0 && monto > 0 ? Math.round(monto / dias) : 75000);
            const tieneFactura = Boolean(r.codigoFactura || r.idPago);

            return `
            <tr>
                <td>
                    <div style="display:flex; align-items:center; gap:8px;">
                        <span style="font-size:1.3rem;">🏗️</span>
                        <div>
                            <strong>#${UI.esc(r.codigoReserva || ('RES-' + idRes))}</strong>
                            <div style="font-size:0.78rem; color:var(--color-texto-suave);">${UI.fecha(r.fechaCreacion || r.fechaInicio)}</div>
                        </div>
                    </div>
                </td>
                <td>
                    <strong>${UI.esc(nombreEquipo)}</strong>
                    <div style="font-size:0.75rem; color:var(--color-texto-suave);">Tarifa: ${UI.pesos(tarifa)} / día</div>
                </td>
                <td>
                    <div style="font-size:0.85rem;">${UI.fecha(r.fechaInicio)} &rarr; ${UI.fecha(r.fechaFin)}</div>
                    <div style="font-size:0.75rem; color:var(--color-texto-suave);">${dias} días de labor</div>
                </td>
                <td>
                    <strong>${UI.pesos(monto)}</strong>
                    <div style="font-size:0.75rem; color:var(--color-acento-claro);">Depósito: ${UI.pesos(r.depositoGarantia || 100000)}</div>
                </td>
                <td>
                    ${UI.insignia(r.estado)}
                </td>
                <td>
                    <div style="display:flex; gap:6px; flex-wrap:wrap;">
                        <button class="boton boton-secundario boton-pequeno btn-ver-factura-historial" data-id="${idRes}" title="Ver Comprobante Oficial DIAN (PDF)">
                            📄 Factura PDF
                        </button>
                        <button class="boton boton-pequeno btn-volver-alquilar" data-idherramienta="${h.idHerramienta || 1}" data-nombre="${UI.esc(nombreEquipo)}" data-tarifa="${tarifa}" title="Volver a agregar al carrito">
                            🔄 Re-alquilar
                        </button>
                    </div>
                </td>
            </tr>
            `;
        }).join('');

        contenedor.innerHTML = `
            <div style="overflow-x:auto;">
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>Orden / Código</th>
                            <th>Maquinaria & Equipo</th>
                            <th>Periodo en Obra</th>
                            <th>Monto Total</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${filasHtml}
                    </tbody>
                </table>
            </div>
        `;

        // Listeners para factura
        contenedor.querySelectorAll('.btn-ver-factura-historial').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = Number(btn.dataset.id);
                const r = reservasCache.find(x => (x.idReserva === id || x.id === id));
                if (r) abrirVisorFactura(r);
            });
        });

        // Listeners para volver a alquilar
        contenedor.querySelectorAll('.btn-volver-alquilar').forEach(btn => {
            btn.addEventListener('click', () => {
                const idH = Number(btn.dataset.idherramienta);
                const nombre = btn.dataset.nombre;
                const tarifa = Number(btn.dataset.tarifa);

                UI.carrito.agregar({
                    idHerramienta: idH,
                    nombre,
                    marca: 'CAT / Industrial',
                    modelo: '2026',
                    tarifaDiaria: tarifa
                });

                UI.toast(`¡"${nombre}" re-agregada al carrito de alquileres! 🛒`, 'exito');
                activarPestana('carrito');
            });
        });
    }

    // Filtros de historial
    document.querySelectorAll('#filtros-historial .pildora-cat').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('#filtros-historial .pildora-cat').forEach(b => b.classList.remove('activa'));
            btn.classList.add('activa');
            renderizarHistorial(btn.dataset.filtro);
        });
    });

    // ------------------------------------------------------------------
    // TAB FACTURAS DIAN
    // ------------------------------------------------------------------

    function cargarFacturasDian() {
        const contenedor = document.getElementById('contenedor-facturas-dian');
        if (!reservasCache || reservasCache.length === 0) {
            contenedor.innerHTML = `
                <div class="estado-vacio">
                    <div class="icono">💳</div>
                    <p>No se encontraron facturas emitidas aún. Al procesar el pago de un alquiler se genera automáticamente.</p>
                </div>
            `;
            return;
        }

        const facturas = reservasCache.map(r => {
            const idRes = r.idReserva || r.id || 101;
            const monto = Number(r.montoTotal) || Number(r.montoTotalEstimado) || 250000;
            const sub = Math.round(monto / 1.19);
            const iva = monto - sub;
            const numFactura = r.codigoFactura || `SETP-99000${String(idRes).padStart(4, '0')}`;
            const cufe = `fe00${idRes}a98b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0`;

            return `
            <tr>
                <td>
                    <strong>#${numFactura}</strong>
                    <div style="font-size:0.75rem; color:var(--color-texto-suave);">Orden: #${r.codigoReserva || ('RES-' + idRes)}</div>
                </td>
                <td>
                    <div style="font-size:0.85rem;">${UI.fecha(r.fechaCreacion || r.fechaInicio)}</div>
                    <div style="font-size:0.72rem; color:#94a3b8; font-family:monospace;">CUFE: ${cufe.substring(0, 16)}...</div>
                </td>
                <td>
                    <div>Subtotal: ${UI.pesos(sub)}</div>
                    <div style="font-size:0.78rem; color:var(--color-acento-claro);">IVA (19%): ${UI.pesos(iva)}</div>
                </td>
                <td>
                    <strong style="font-size:1.05rem;">${UI.pesos(monto)}</strong>
                </td>
                <td>
                    <span class="insignia insignia-aprobada">DIAN Emitida</span>
                </td>
                <td>
                    <button class="boton boton-pequeno btn-ver-pdf-factura-tab" data-id="${idRes}">
                        📄 Ver PDF
                    </button>
                </td>
            </tr>
            `;
        }).join('');

        contenedor.innerHTML = `
            <div style="overflow-x:auto;">
                <table class="tabla">
                    <thead>
                        <tr>
                            <th>Factura Electrónica</th>
                            <th>Fecha & CUFE</th>
                            <th>Desglose DIAN</th>
                            <th>Total Pagado</th>
                            <th>Estado Fiscal</th>
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${facturas}
                    </tbody>
                </table>
            </div>
        `;

        contenedor.querySelectorAll('.btn-ver-pdf-factura-tab').forEach(btn => {
            btn.addEventListener('click', () => {
                const id = Number(btn.dataset.id);
                const r = reservasCache.find(x => (x.idReserva === id || x.id === id));
                if (r) abrirVisorFactura(r);
            });
        });
    }

    // ------------------------------------------------------------------
    // VISOR DE FACTURA ELECTRÓNICA (PDF DIAN)
    // ------------------------------------------------------------------

    function abrirVisorFactura(r) {
        const cuerpo = document.getElementById('factura-cuerpo');
        const idRes = r.idReserva || r.id || 101;
        const monto = Number(r.montoTotal) || Number(r.montoTotalEstimado) || 250000;
        const subtotal = Math.round(monto / 1.19);
        const iva = monto - subtotal;
        const numFactura = r.codigoFactura || `SETP-99000${String(idRes).padStart(4, '0')}`;
        const cufe = `fe00${idRes}a98b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0`;
        const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=110x110&data=NumFac:${numFactura};FecFac:${r.fechaCreacion || r.fechaInicio};ValFac:${monto};CUFE:${cufe}`;

        const h = r.items && r.items[0] ? r.items[0] : {};
        const nombreEquipo = h.nombreHerramienta || r.nombreHerramienta || 'Equipo de Construcción Industrial';
        const dias = Number(h.dias) || Number(r.diasAlquiler) || Math.max(1, Math.ceil((new Date(r.fechaFin) - new Date(r.fechaInicio)) / (1000 * 60 * 60 * 24))) || 3;
        const tarifa = Number(h.tarifaDiaria) || Number(h.tarifaPactada) || (dias > 0 && monto > 0 ? Math.round(monto / dias) : 75000);
        const deposito = r.depositoGarantia || 100000;

        cuerpo.innerHTML = `
            <div class="hoja-factura-pdf" id="hoja-factura-imprimible">
                <!-- CABECERA FISCAL -->
                <div class="factura-header-oficial">
                    <div class="factura-emisor-col">
                        <div class="factura-logo-marca">
                            <span style="font-size: 2rem;">🏗️</span>
                            <div>
                                <span class="marca-texto">RENTA<span style="color:#d97706;">TOOLS</span> S.A.S.</span>
                                <div style="font-size:0.75rem; color:#64748b; font-weight:600;">NIT 901.458.789-3 &middot; Régimen Común</div>
                            </div>
                        </div>
                        <div class="factura-datos-emisor">
                            <p><strong>Sede Principal:</strong> Av. Industrial No. 45 - 28, Bucaramanga / Medellín</p>
                            <p><strong>PBX:</strong> +57 (607) 698-0012 &middot; <strong>Email:</strong> facturacion@rentatool.com</p>
                            <p><strong>Resolución DIAN:</strong> No. 1876400012 &middot; Vigencia 24 meses &middot; Rango SETP-1 a SETP-50000</p>
                        </div>
                    </div>

                    <div class="factura-numero-caja">
                        <div class="factura-tipo-titulo">FACTURA ELECTRÓNICA DE VENTA</div>
                        <div class="factura-num-grande">${numFactura}</div>
                        <div class="factura-fechas-grid">
                            <div><span>Fecha de Emisión:</span><strong>${UI.fechaConHora(r.fechaCreacion)}</strong></div>
                            <div><span>Medio de Pago:</span><strong>Transferencia Bancaria / PSE</strong></div>
                            <div><span>Forma de Pago:</span><strong>Contado Inmediato</strong></div>
                        </div>
                    </div>
                </div>

                <!-- DATOS DEL ADQUIRENTE -->
                <div class="factura-adquirente-box">
                    <div class="titulo-seccion-factura">DATOS DEL CLIENTE / ADQUIRENTE</div>
                    <div class="adquirente-grid">
                        <div><span>Nombre / Razón Social:</span> <strong>${UI.esc(usuarioActual.nombreCompleto || 'Daniel Felipe Osorio')}</strong></div>
                        <div><span>NIT / Documento:</span> <strong>${UI.esc(usuarioActual.numeroDocumento || '1098765432')}</strong></div>
                        <div><span>Correo Electrónico:</span> <strong>${UI.esc(usuarioActual.email || 'daniel.osorio@gmail.com')}</strong></div>
                        <div><span>Dirección / Sede:</span> <strong>${UI.esc(usuarioActual.direccion || 'Carrera 33 # 48-19, Bucaramanga')}</strong></div>
                    </div>
                </div>

                <!-- TABLA DE ITEMS FACTURADOS -->
                <table class="factura-tabla-items">
                    <thead>
                        <tr>
                            <th style="width: 50px;">Ítem</th>
                            <th>Descripción del Servicio / Maquinaria</th>
                            <th style="text-align: center; width: 70px;">Días</th>
                            <th style="text-align: right; width: 120px;">Tarifa Diaria</th>
                            <th style="text-align: right; width: 130px;">Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td style="text-align: center;">01</td>
                            <td>
                                <strong>Alquiler de ${UI.esc(nombreEquipo)}</strong>
                                <div style="font-size: 0.76rem; color: #64748b;">
                                    Periodo operativo en obra: ${UI.fecha(r.fechaInicio)} hasta ${UI.fecha(r.fechaFin)}.
                                </div>
                            </td>
                            <td style="text-align: center;">${dias}</td>
                            <td style="text-align: right;">${UI.pesos(tarifa)}</td>
                            <td style="text-align: right;"><strong>${UI.pesos(subtotal)}</strong></td>
                        </tr>
                    </tbody>
                </table>

                <!-- RESUMEN DE TOTALES Y DIAN -->
                <div class="factura-totales-bloque">
                    <div class="factura-cufe-qr-area">
                        <img src="${qrUrl}" alt="Código QR DIAN" class="factura-qr-img" width="105" height="105">
                        <div class="factura-cufe-textos">
                            <span class="dian-validada-tag">✅ VALIDADA POR LA DIAN</span>
                            <div class="cufe-titulo">CUFE (Código Único de Factura Electrónica):</div>
                            <div class="cufe-hash">${cufe}</div>
                        </div>
                    </div>

                    <div class="factura-totales-col">
                        <div class="totales-fila"><span>Subtotal Gravable:</span><strong>${UI.pesos(subtotal)}</strong></div>
                        <div class="totales-fila"><span>IVA 19% Generado:</span><strong>${UI.pesos(iva)}</strong></div>
                        <div class="totales-fila gran-total"><span>Total Pagado:</span><strong>${UI.pesos(monto)}</strong></div>
                    </div>
                </div>

                <div class="factura-pie-legal">
                    <p>Esta factura electrónica de venta constituye título valor según los términos del artículo 774 del Código de Comercio. Depósito de garantía en custodia temporal reembolsable de ${UI.pesos(deposito)} según liquidación de acta de devolución.</p>
                </div>
            </div>
        `;

        UI.abrirModal('modal-factura');
    }

    // Acciones de visor PDF
    const btnImprimir = document.getElementById('btn-imprimir-factura');
    if (btnImprimir) {
        btnImprimir.addEventListener('click', () => {
            window.print();
        });
    }

    const btnDescargar = document.getElementById('btn-descargar-pdf');
    if (btnDescargar) {
        btnDescargar.addEventListener('click', () => {
            const elemento = document.getElementById('hoja-factura-imprimible');
            if (!elemento) return;

            const opt = {
                margin:       [10, 10, 10, 10],
                filename:     `Factura_Dian_${Date.now()}.pdf`,
                image:        { type: 'jpeg', quality: 0.98 },
                html2canvas:  { scale: 2, useCORS: true },
                jsPDF:        { unit: 'mm', format: 'a4', orientation: 'portrait' }
            };

            btnDescargar.disabled = true;
            btnDescargar.textContent = '⏳ Generando PDF...';

            html2pdf().set(opt).from(elemento).save().then(() => {
                btnDescargar.disabled = false;
                btnDescargar.textContent = '📥 Descargar PDF';
                UI.toast('Factura PDF descargada exitosamente.', 'exito');
            }).catch(err => {
                btnDescargar.disabled = false;
                btnDescargar.textContent = '📥 Descargar PDF';
                UI.toast('Error al generar PDF: ' + err.message, 'error');
            });
        });
    }

    // ------------------------------------------------------------------
    // Métricas y Contadores Globales
    // ------------------------------------------------------------------

    function actualizarContadores() {
        const cantCart = UI.carrito.contar();
        const kpiCart = document.getElementById('kpi-total-carrito');
        if (kpiCart) kpiCart.textContent = cantCart;

        const tabCart = document.getElementById('tab-contador-carrito');
        if (tabCart) {
            tabCart.textContent = cantCart;
            tabCart.style.display = cantCart > 0 ? 'inline-flex' : 'none';
        }

        const countHist = reservasCache.length;
        const kpiAlquileres = document.getElementById('kpi-total-alquileres');
        if (kpiAlquileres) kpiAlquileres.textContent = countHist;

        const tabHist = document.getElementById('tab-contador-historial');
        if (tabHist) tabHist.textContent = countHist;

        // Inversión acumulada
        const totalInv = reservasCache.reduce((sum, r) => sum + (Number(r.montoTotal) || Number(r.montoTotalEstimado) || 0), 0);
        const kpiInv = document.getElementById('kpi-total-inversion');
        if (kpiInv) kpiInv.textContent = UI.pesos(totalInv);
    }

    // Cargar historial en background para tener métricas de inmediato
    try {
        reservasCache = await API.reservas.misReservas();
        actualizarContadores();
    } catch (e) {
        // En caso de que aún no cargue
    }

});
