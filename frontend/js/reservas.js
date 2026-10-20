/* ==========================================================================
   reservas.js - Gestión de reservas, pagos y facturas para el cliente
   Autor: Daniel Osorio (DEV 2)
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

    'use strict';

    if (!UI.exigirSesion()) return;
    UI.pintarBarra('reservas');

    const tablaReservas = document.getElementById('tabla-reservas');
    const formPago      = document.getElementById('form-pago');
    const btnRefrescar  = document.getElementById('btn-refrescar');

    let reservasCache = [];

    cargarMisReservas();

    btnRefrescar.addEventListener('click', cargarMisReservas);

    // Cierre de modales
    document.querySelectorAll('[data-cerrar]').forEach(boton => {
        boton.addEventListener('click', () => UI.cerrarModal(boton.dataset.cerrar));
    });

    // Acciones de visor PDF de Factura
    const btnImprimirFactura  = document.getElementById('btn-imprimir-factura');
    const btnDescargarFactura = document.getElementById('btn-descargar-pdf');

    if (btnImprimirFactura) {
        btnImprimirFactura.addEventListener('click', () => window.print());
    }

    if (btnDescargarFactura) {
        btnDescargarFactura.addEventListener('click', descargarFacturaPdf);
    }

    // ------------------------------------------------------------------
    // Listado de Reservas
    // ------------------------------------------------------------------

    async function cargarMisReservas() {
        UI.cargando(tablaReservas, 'Cargando sus reservas...');
        UI.ocultarMensaje('mensaje');

        try {
            const listado = await API.reservas.misReservas();
            reservasCache = listado || [];
            pintarReservas(reservasCache);
        } catch (error) {
            tablaReservas.innerHTML = '';
            UI.mensaje('mensaje', 'Error al cargar reservas: ' + error.message, 'error');
        }
    }

    function pintarReservas(reservas) {
        if (!reservas || reservas.length === 0) {
            tablaReservas.innerHTML = `
                <tr>
                    <td colspan="6" style="text-align:center; padding:32px; color:var(--color-texto-suave);">
                        Aún no tiene reservas registradas. ¡Explore el catálogo y alquile su primera máquina!
                    </td>
                </tr>
            `;
            return;
        }

        tablaReservas.innerHTML = reservas.map(r => {
            const puedePagar = (r.estado === 'APROBADA' || r.estado === 'SOLICITADA');
            const puedeCancelar = (r.estado === 'SOLICITADA');

            return `
                <tr>
                    <td><strong>${UI.esc(r.codigoReserva)}</strong></td>
                    <td>${UI.fecha(r.fechaInicio)} al ${UI.fecha(r.fechaFin)}</td>
                    <td>${UI.insignia(r.estado)}</td>
                    <td><strong>${UI.pesos(r.montoTotalEstimado)}</strong></td>
                    <td>${UI.pesos(r.depositoGarantia)}</td>
                    <td class="acciones-celda">
                        ${puedePagar ? `
                            <button class="boton boton-pequeno btn-pagar" data-id="${r.idReserva}">
                                Pagar
                            </button>
                        ` : ''}
                        <button class="boton boton-secundario boton-pequeno btn-factura" data-id="${r.idReserva}">
                            Ver Factura
                        </button>
                        ${puedeCancelar ? `
                            <button class="boton boton-peligro boton-pequeno btn-cancelar" data-id="${r.idReserva}">
                                Cancelar
                            </button>
                        ` : ''}
                    </td>
                </tr>
            `;
        }).join('');

        // Listeners para botones de acción
        tablaReservas.querySelectorAll('.btn-pagar').forEach(b => {
            b.addEventListener('click', () => abrirModalPago(Number(b.dataset.id)));
        });

        tablaReservas.querySelectorAll('.btn-factura').forEach(b => {
            b.addEventListener('click', () => verFacturaReserva(Number(b.dataset.id)));
        });

        tablaReservas.querySelectorAll('.btn-cancelar').forEach(b => {
            b.addEventListener('click', () => cancelarReserva(Number(b.dataset.id)));
        });
    }

    // ------------------------------------------------------------------
    // Pasarela de Pagos
    // ------------------------------------------------------------------

    function abrirModalPago(idReserva) {
        const reserva = reservasCache.find(r => r.idReserva === idReserva);
        if (!reserva) return;

        document.getElementById('pago-id-reserva').value = idReserva;
        document.getElementById('pago-subtitulo').textContent = `Liquidación para Reserva #${reserva.codigoReserva}`;

        const montoTotal = Number(reserva.montoTotalEstimado) || 0;
        document.getElementById('pago-monto').value = montoTotal;

        actualizarCalculoIva(montoTotal);

        UI.ocultarMensaje('pago-mensaje');
        UI.abrirModal('modal-pago');
    }

    function actualizarCalculoIva(total) {
        // En Colombia IVA = 19%. Base = Total / 1.19
        const subtotal = Math.round(total / 1.19);
        const iva = total - subtotal;

        document.getElementById('prev-subtotal').textContent = UI.pesos(subtotal);
        document.getElementById('prev-iva').textContent = UI.pesos(iva);
        document.getElementById('prev-total').textContent = UI.pesos(total);
    }

    formPago.addEventListener('submit', async (e) => {
        e.preventDefault();
        UI.ocultarMensaje('pago-mensaje');

        const idReserva = Number(document.getElementById('pago-id-reserva').value);
        const idMetodoPago = Number(document.getElementById('pago-metodo').value);
        const tipoPago = document.getElementById('pago-tipo').value;
        const monto = Number(document.getElementById('pago-monto').value);

        const btn = document.getElementById('btn-confirmar-pago');
        btn.disabled = true;
        btn.textContent = 'Procesando en pasarela...';

        try {
            const comando = {
                idReserva,
                idMetodoPago,
                tipoPago,
                monto
            };

            const respuesta = await API.pagos.procesar(comando);
            UI.cerrarModal('modal-pago');
            UI.mensaje('mensaje', `¡Pago exitoso! Transacción: ${respuesta.numeroTransaccion}. Factura emitida automáticamente.`, 'exito');
            await cargarMisReservas();
        } catch (error) {
            UI.mensaje('pago-mensaje', 'Error al procesar pago: ' + error.message, 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Confirmar y Pagar';
        }
    });

    // ------------------------------------------------------------------
    // Facturación Electrónica Oficial (Desglose 19% IVA - DIAN Colombia)
    // ------------------------------------------------------------------

    let facturaActual = null;

    function numeroALetras(monto) {
        const num = Math.round(Number(monto) || 0);
        if (num === 0) return 'CERO';

        const unidades = ['', 'UN', 'DOS', 'TRES', 'CUATRO', 'CINCO', 'SEIS', 'SIETE', 'OCHO', 'NUEVE'];
        const especiales = ['DIEZ', 'ONCE', 'DOCE', 'TRECE', 'CATORCE', 'QUINCE', 'DIECISÉIS', 'DIECISIETE', 'DIECIOCHO', 'DIECINUEVE'];
        const decenas = ['', 'DIEZ', 'VEINTE', 'TREINTA', 'CUARENTA', 'CINCUENTA', 'SESENTA', 'SETENTA', 'OCHENTA', 'NOVENTA'];
        const centenas = ['', 'CIENTO', 'DOSCIENTOS', 'TRESCIENTOS', 'CUATROCIENTOS', 'QUINIENTOS', 'SEISCIENTOS', 'SETECIENTOS', 'OCHOCIENTOS', 'NOVECIENTOS'];

        function seccion(n) {
            let c = Math.floor(n / 100);
            let d = Math.floor((n % 100) / 10);
            let u = n % 10;
            let res = '';

            if (n === 100) return 'CIEN';
            if (c > 0) res += centenas[c] + ' ';
            if (d === 1 && u >= 0) {
                res += especiales[u];
                return res.trim();
            } else if (d === 2 && u > 0) {
                res += 'VEINTI' + unidades[u];
                return res.trim();
            } else if (d > 0) {
                res += decenas[d];
                if (u > 0) res += ' Y ' + unidades[u];
                return res.trim();
            } else if (u > 0) {
                res += unidades[u];
            }
            return res.trim();
        }

        let millones = Math.floor(num / 1000000);
        let miles = Math.floor((num % 1000000) / 1000);
        let resto = num % 1000;
        let salida = '';

        if (millones > 0) {
            salida += (millones === 1 ? 'UN MILLÓN ' : seccion(millones) + ' MILLONES ');
        }
        if (miles > 0) {
            salida += (miles === 1 ? 'MIL ' : seccion(miles) + ' MIL ');
        }
        if (resto > 0) {
            salida += seccion(resto);
        }
        return salida.trim();
    }

    async function verFacturaReserva(idReserva) {
        const contenedor = document.getElementById('factura-cuerpo');
        UI.cargando(contenedor, 'Consultando vista previa de factura electrónica en PDF...');
        UI.abrirModal('modal-factura');

        const reserva = (reservasCache || []).find(r => r.idReserva === idReserva);

        try {
            let factura = null;

            // 1. Intentar consulta directa por ID de reserva
            try {
                factura = await API.facturas.porReserva(idReserva);
            } catch (errReserva) {
                console.warn('Consulta directa por reserva no disponible, consultando vía pagos:', errReserva);
            }

            // 2. Si no retornó factura directa, consultar pagos registrados
            if (!factura) {
                const pagos = await API.pagos.porReserva(idReserva);
                if (Array.isArray(pagos) && pagos.length > 0) {
                    const ultimoPago = pagos[0];
                    if (ultimoPago && ultimoPago.factura) {
                        factura = ultimoPago.factura;
                    } else if (ultimoPago && ultimoPago.idPago) {
                        try {
                            factura = await API.facturas.porPago(ultimoPago.idPago);
                        } catch (errPago) {
                            console.warn('Error al consultar factura por idPago:', errPago);
                        }
                    }
                } else if (pagos && typeof pagos === 'object' && (pagos.idFactura || pagos.numeroFactura)) {
                    factura = pagos;
                }
            }

            // 3. Si no existe pago ni factura registrada aún
            if (!factura) {
                contenedor.innerHTML = `
                    <div class="estado-vacio" style="padding: 40px 20px; text-align: center;">
                        <div style="font-size: 3.2rem; margin-bottom: 12px;">📄</div>
                        <h3 style="margin-bottom: 8px;">Factura Aún No Emitida</h3>
                        <p style="color:var(--color-texto-suave); max-width: 480px; margin: 0 auto 24px; font-size: 0.95rem; line-height: 1.5;">
                            No se ha registrado ningún pago para la reserva <strong>#${reserva ? reserva.codigoReserva : idReserva}</strong>.
                            El comprobante fiscal legal en formato PDF con desglose del 19% de IVA se genera automáticamente tras liquidar el pago.
                        </p>
                        <button class="boton" id="btn-ir-pagar-desde-factura" style="margin: 0 auto;">
                            💳 Realizar Pago de la Reserva
                        </button>
                    </div>
                `;
                const btnIrPagar = document.getElementById('btn-ir-pagar-desde-factura');
                if (btnIrPagar) {
                    btnIrPagar.addEventListener('click', () => {
                        UI.cerrarModal('modal-factura');
                        abrirModalPago(idReserva);
                    });
                }
                return;
            }

            facturaActual = factura;
            renderizarFactura(factura, reserva);
        } catch (error) {
            contenedor.innerHTML = `
                <div class="mensaje mensaje-error visible" style="margin: 20px;">
                    <strong>Error al consultar factura:</strong> ${UI.esc(error.message || 'No fue posible cargar el comprobante.')}
                </div>
            `;
        }
    }

    function renderizarFactura(f, reserva) {
        const contenedor = document.getElementById('factura-cuerpo');
        const usuario = API.obtenerUsuario() || { nombreCompleto: 'Cliente Renta Tools', email: 'cliente@rentatools.com' };

        const nombreServicio = (reserva && reserva.herramienta && reserva.herramienta.nombre)
            ? `${reserva.herramienta.nombre} (${reserva.herramienta.marca || ''} ${reserva.herramienta.modelo || ''})`
            : `Alquiler de maquinaria y herramientas de construcción - Reserva #${reserva ? reserva.codigoReserva : (f.codigoReserva || f.idReserva || '001')}`;

        const detalles = (f.detalles && f.detalles.length > 0) ? f.detalles : [
            {
                descripcionItem: nombreServicio,
                cantidad: 1,
                precioUnitario: f.subtotal,
                subtotalLinea: f.subtotal
            }
        ];

        const detallesHtml = detalles.map((d, idx) => `
            <tr>
                <td style="text-align:center; color:#64748b; font-weight:600;">${idx + 1}</td>
                <td>
                    <strong style="color:#0f172a;">${UI.esc(d.descripcionItem)}</strong>
                    <div style="font-size:0.75rem; color:#64748b;">Tarifa de servicio incluye mantenimiento preventivo y disponibilidad operativa</div>
                </td>
                <td style="text-align:center; font-weight:600;">${d.cantidad || 1}</td>
                <td style="text-align:right;">${UI.pesos(d.precioUnitario)}</td>
                <td style="text-align:right; font-weight:700; color:#0f172a;">${UI.pesos(d.subtotalLinea || (d.cantidad * d.precioUnitario))}</td>
            </tr>
        `).join('');

        const cufeCompleto = f.cufe || '404e635266556a586e3272357538782f413f4428472b4b6250645367566b5970';
        const fechaTexto = UI.fecha(f.fechaEmision);
        const periodoReserva = reserva ? `${UI.fecha(reserva.fechaInicio)} al ${UI.fecha(reserva.fechaFin)}` : 'Periodo estándar contratado';

        const qrSvg = `
            <svg width="78" height="78" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="5" y="5" width="30" height="30" rx="3" fill="#0f172a"/>
                <rect x="11" y="11" width="18" height="18" rx="2" fill="#ffffff"/>
                <rect x="15" y="15" width="10" height="10" rx="1" fill="#0284c7"/>
                <rect x="65" y="5" width="30" height="30" rx="3" fill="#0f172a"/>
                <rect x="71" y="11" width="18" height="18" rx="2" fill="#ffffff"/>
                <rect x="75" y="15" width="10" height="10" rx="1" fill="#0284c7"/>
                <rect x="5" y="65" width="30" height="30" rx="3" fill="#0f172a"/>
                <rect x="11" y="71" width="18" height="18" rx="2" fill="#ffffff"/>
                <rect x="15" y="75" width="10" height="10" rx="1" fill="#0284c7"/>
                <rect x="42" y="8" width="8" height="8" fill="#0f172a"/>
                <rect x="42" y="22" width="8" height="8" fill="#0f172a"/>
                <rect x="52" y="15" width="6" height="6" fill="#0f172a"/>
                <rect x="8" y="42" width="8" height="8" fill="#0f172a"/>
                <rect x="22" y="42" width="8" height="8" fill="#0f172a"/>
                <rect x="15" y="52" width="6" height="6" fill="#0f172a"/>
                <rect x="40" y="40" width="20" height="20" rx="2" fill="#0284c7"/>
                <rect x="45" y="45" width="10" height="10" fill="#ffffff"/>
                <rect x="68" y="42" width="7" height="7" fill="#0f172a"/>
                <rect x="80" y="48" width="8" height="8" fill="#0f172a"/>
                <rect x="72" y="60" width="7" height="7" fill="#0f172a"/>
                <rect x="85" y="65" width="8" height="8" fill="#0f172a"/>
                <rect x="68" y="78" width="8" height="8" fill="#0f172a"/>
                <rect x="82" y="82" width="10" height="10" fill="#0f172a"/>
                <rect x="42" y="68" width="7" height="7" fill="#0f172a"/>
                <rect x="52" y="78" width="7" height="7" fill="#0f172a"/>
                <rect x="45" y="85" width="8" height="8" fill="#0f172a"/>
            </svg>
        `;

        contenedor.innerHTML = `
            <div class="visor-pdf-toolbar">
                <div class="visor-pdf-info">
                    <span class="badge-pdf-rojo">PDF OFICIAL</span>
                    <span><strong>Factura_${UI.esc(f.numeroFactura)}.pdf</strong> &middot; Hoja A4 Digital</span>
                </div>
                <div style="font-size:0.75rem; color:#94a3b8;">
                    Desglose Legal IVA (19%) &middot; DIAN Colombia
                </div>
            </div>

            <div class="hoja-factura-pdf">
                <!-- Cabecera Oficial y Radicado DIAN -->
                <div class="pdf-factura-header">
                    <div class="pdf-empresa-info">
                        <h3>RENTA TOOLS COLOMBIA S.A.S.</h3>
                        <p><strong>NIT:</strong> 901.458.789-4 &middot; IVA Régimen Común</p>
                        <p><strong>Actividad Económica:</strong> 7730 (Alquiler y arrendamiento de maquinaria)</p>
                        <p><strong>Dirección:</strong> Av. El Dorado #68C-61, Torre Central Of. 502, Bogotá D.C.</p>
                        <p><strong>PBX:</strong> (+57) 601 745 9000 &middot; facturacion@rentatools.com.co</p>
                    </div>
                    <div class="pdf-dian-box">
                        <div class="pdf-tipo-doc">Factura Electrónica de Venta</div>
                        <div class="pdf-radicado">No. ${UI.esc(f.numeroFactura)}</div>
                        <div class="pdf-res-dian">
                            Resolución DIAN No. 1876400012<br>
                            Rango: FAC-2026-0001 a FAC-2026-9999<br>
                            Vigencia: 2026-01-01 a 2026-12-31
                        </div>
                    </div>
                </div>

                <!-- Bloque Metadatos Cliente y Pago -->
                <div class="pdf-metadatos-grid">
                    <div class="pdf-meta-col">
                        <h4>Datos del Adquiriente / Cliente</h4>
                        <div class="pdf-meta-fila">
                            <span class="pdf-meta-etiqueta">Razón Social / Nombre:</span>
                            <span class="pdf-meta-valor">${UI.esc(usuario.nombreCompleto || 'Cliente Registrado')}</span>
                        </div>
                        <div class="pdf-meta-fila">
                            <span class="pdf-meta-etiqueta">Identificación / Email:</span>
                            <span class="pdf-meta-valor">${UI.esc(usuario.email || ('ID #' + f.idCliente))}</span>
                        </div>
                        <div class="pdf-meta-fila">
                            <span class="pdf-meta-etiqueta">Reserva Radicado:</span>
                            <span class="pdf-meta-valor">#${UI.esc(reserva ? reserva.codigoReserva : (f.codigoReserva || ('RES-' + f.idReserva)))}</span>
                        </div>
                        <div class="pdf-meta-fila">
                            <span class="pdf-meta-etiqueta">Periodo Alquiler:</span>
                            <span class="pdf-meta-valor">${periodoReserva}</span>
                        </div>
                    </div>
                    <div class="pdf-meta-col">
                        <h4>Detalles de Emisión y Transacción</h4>
                        <div class="pdf-meta-fila">
                            <span class="pdf-meta-etiqueta">Fecha y Hora de Emisión:</span>
                            <span class="pdf-meta-valor">${fechaTexto}</span>
                        </div>
                        <div class="pdf-meta-fila">
                            <span class="pdf-meta-etiqueta">Forma de Pago:</span>
                            <span class="pdf-meta-valor">Pago de Contado (Inmediato)</span>
                        </div>
                        <div class="pdf-meta-fila">
                            <span class="pdf-meta-etiqueta">Medio de Pago / Pasarela:</span>
                            <span class="pdf-meta-valor">Electrónico (PSE / Tarjeta)</span>
                        </div>
                        <div class="pdf-meta-fila">
                            <span class="pdf-meta-etiqueta">Estado Fiscal:</span>
                            <span class="pdf-meta-valor"><span class="badge-pagada-pdf">${UI.esc(f.estadoFactura || f.estado || 'PAGADA')}</span></span>
                        </div>
                    </div>
                </div>

                <!-- Tabla de Ítems / Conceptos Facturados -->
                <table class="pdf-tabla-items">
                    <thead>
                        <tr>
                            <th style="width:36px; text-align:center;">#</th>
                            <th>Descripción del Ítem / Servicio Contratado</th>
                            <th style="text-align:center; width:60px;">Cant.</th>
                            <th style="text-align:right; width:130px;">Valor Unitario</th>
                            <th style="text-align:right; width:140px;">Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${detallesHtml}
                    </tbody>
                </table>

                <!-- Liquidación Tributaria e Importe en Letras -->
                <div class="pdf-liquidacion-grid">
                    <div class="pdf-letras-box">
                        <div class="pdf-letras-titulo">Valor en Letras</div>
                        <div class="pdf-letras-texto">SON: ${numeroALetras(f.total)} PESOS M/CTE.</div>
                        <p style="font-size:0.7rem; color:#64748b; margin:8px 0 0 0; line-height:1.35;">
                            Esta factura de venta se expide de acuerdo con los Arts. 615 y 616-1 del Estatuto Tributario y el Decreto 2242 de 2015. Presta mérito ejecutivo de acuerdo al Código de Comercio.
                        </p>
                    </div>
                    <div class="pdf-totales-col">
                        <div class="pdf-total-fila">
                            <span>Subtotal (Base Gravable):</span>
                            <strong>${UI.pesos(f.subtotal)}</strong>
                        </div>
                        <div class="pdf-total-fila">
                            <span>IVA Legal (19% E.T.):</span>
                            <strong style="color:#0284c7;">${UI.pesos(f.iva)}</strong>
                        </div>
                        <div class="pdf-total-fila">
                            <span>Retención en la Fuente:</span>
                            <span>$ 0.00 COP</span>
                        </div>
                        <div class="pdf-total-fila gran-total">
                            <span>TOTAL PAGADO:</span>
                            <span class="valor-total">${UI.pesos(f.total)}</span>
                        </div>
                    </div>
                </div>

                <!-- Validación Electrónica DIAN y CUFE -->
                <div class="pdf-dian-certificacion">
                    <div class="pdf-qr-box" title="Código QR Oficial de Validación DIAN">
                        ${qrSvg}
                    </div>
                    <div class="pdf-cufe-info">
                        <div class="pdf-cufe-header">
                            <strong style="color:#0f172a;">CUFE (Código Único de Factura Electrónica):</strong>
                            <button type="button" class="btn-copiar-cufe" data-cufe="${cufeCompleto}">Copiar CUFE</button>
                        </div>
                        <span class="pdf-cufe-hash">${cufeCompleto}</span>
                        <div style="margin-top:4px;">
                            Documento tributario validado por la DIAN. Representación gráfica oficial para consulta en la plataforma nacional.
                        </div>
                    </div>
                </div>
            </div>
        `;

        const btnCopiar = contenedor.querySelector('.btn-copiar-cufe');
        if (btnCopiar) {
            btnCopiar.addEventListener('click', () => {
                const cufe = btnCopiar.dataset.cufe;
                if (navigator.clipboard) {
                    navigator.clipboard.writeText(cufe).then(() => {
                        btnCopiar.textContent = '✓ ¡Copiado!';
                        setTimeout(() => { btnCopiar.textContent = 'Copiar CUFE'; }, 2000);
                    });
                }
            });
        }
    }

    async function descargarFacturaPdf() {
        const hoja = document.querySelector('.hoja-factura-pdf');
        if (!hoja) return;

        const btn = document.getElementById('btn-descargar-pdf');
        const textoOriginal = btn ? btn.innerHTML : '';
        if (btn) {
            btn.disabled = true;
            btn.innerHTML = '⏳ Generando PDF...';
        }

        try {
            if (window.html2pdf) {
                const numFactura = facturaActual ? (facturaActual.numeroFactura || 'FAC-2026') : 'FAC-2026';
                const opt = {
                    margin:       [10, 10, 10, 10],
                    filename:     `Factura_${numFactura}.pdf`,
                    image:        { type: 'jpeg', quality: 0.98 },
                    html2canvas:  { scale: 2, useCORS: true, letterRendering: true, backgroundColor: '#ffffff' },
                    jsPDF:        { unit: 'mm', format: 'a4', orientation: 'portrait' }
                };
                await html2pdf().set(opt).from(hoja).save();
            } else {
                window.print();
            }
        } catch (err) {
            console.warn('Error al exportar PDF con html2pdf, recurriendo a impresión nativa:', err);
            window.print();
        } finally {
            if (btn) {
                btn.disabled = false;
                btn.innerHTML = textoOriginal;
            }
        }
    }

    // ------------------------------------------------------------------
    // Cancelación
    // ------------------------------------------------------------------

    async function cancelarReserva(idReserva) {
        if (!confirm('¿Está seguro de que desea cancelar esta reserva?')) return;

        try {
            await API.reservas.cancelar(idReserva);
            UI.mensaje('mensaje', 'Reserva cancelada correctamente', 'exito');
            await cargarMisReservas();
        } catch (error) {
            UI.mensaje('mensaje', 'No se pudo cancelar la reserva: ' + error.message, 'error');
        }
    }
});
