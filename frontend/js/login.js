/* ==========================================================================
   login.js - Pantalla de inicio de sesion
   Autor: Camilo Garcia
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

    'use strict';

    // Si ya hay sesion activa no tiene sentido mostrar el login.
    if (API.haySesion()) {
        redirigirSegunRol();
        return;
    }

    const formulario = document.getElementById('form-login');
    const boton = document.getElementById('btn-entrar');

    formulario.addEventListener('submit', async (e) => {
        e.preventDefault();
        UI.ocultarMensaje('mensaje');

        boton.disabled = true;
        boton.textContent = 'Verificando...';

        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;

        try {
            await API.auth.login(email, password);
            UI.mensaje('mensaje', 'Bienvenido. Redirigiendo...', 'exito');
            setTimeout(redirigirSegunRol, 600);
        } catch (error) {
            UI.mensaje('mensaje', error.message, 'error');
            boton.disabled = false;
            boton.textContent = 'Entrar';
        }
    });

    /** Cada rol aterriza en su propio panel. */
    function redirigirSegunRol() {
        if (API.esAdmin())          window.location.href = 'admin.html';
        else if (API.esProveedor()) window.location.href = 'proveedor.html';
        else                        window.location.href = 'index.html';
    }
});
