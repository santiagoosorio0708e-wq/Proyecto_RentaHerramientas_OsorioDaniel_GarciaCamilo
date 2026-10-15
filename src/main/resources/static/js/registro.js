/* ==========================================================================
   registro.js - Registro diferenciado de cliente y proveedor
   Autor: Camilo Garcia
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

    'use strict';

    const formCliente   = document.getElementById('form-cliente');
    const formProveedor = document.getElementById('form-proveedor');
    const pestanas      = document.querySelectorAll('.pestana');

    // ----------------------------------------------------- Cambio de pestana
    pestanas.forEach(pestana => {
        pestana.addEventListener('click', () => {
            pestanas.forEach(p => p.classList.remove('activa'));
            pestana.classList.add('activa');
            UI.ocultarMensaje('mensaje');

            const esCliente = pestana.dataset.tipo === 'cliente';
            formCliente.classList.toggle('oculto', !esCliente);
            formProveedor.classList.toggle('oculto', esCliente);
        });
    });

    // ------------------------------------------------------ Registro cliente
    formCliente.addEventListener('submit', async (e) => {
        e.preventDefault();
        await enviar(formCliente, API.auth.registrarCliente, 'cliente');
    });

    // ---------------------------------------------------- Registro proveedor
    formProveedor.addEventListener('submit', async (e) => {
        e.preventDefault();
        await enviar(formProveedor, API.auth.registrarProveedor, 'proveedor');
    });

    /**
     * Envia el formulario al endpoint correspondiente.
     * Se comparte la logica porque la unica diferencia real es la funcion
     * de la API que se llama y el texto del mensaje.
     */
    async function enviar(formulario, funcionApi, tipo) {
        UI.ocultarMensaje('mensaje');

        const boton = formulario.querySelector('button[type="submit"]');
        const textoOriginal = boton.textContent;
        boton.disabled = true;
        boton.textContent = 'Registrando...';

        try {
            const datos = UI.leerFormulario(formulario);

            // El cupo de credito no se pide en el formulario: lo asigna
            // despues el administrador segun el estudio de riesgo.
            if (tipo === 'cliente') {
                datos.cupoCredito = 0;
            }

            await funcionApi(datos);

            const extra = tipo === 'proveedor'
                ? ' Su cuenta quedo pendiente de verificacion por el administrador.'
                : '';

            UI.mensaje('mensaje',
                'Cuenta creada correctamente.' + extra + ' Redirigiendo al inicio de sesion...',
                'exito');

            formulario.reset();
            setTimeout(() => { window.location.href = 'login.html'; }, 2500);

        } catch (error) {
            UI.mensaje('mensaje', error.message, 'error');
            boton.disabled = false;
            boton.textContent = textoOriginal;
        }
    }
});
