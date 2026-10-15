/* ==========================================================================
   login.js - Control de autenticación e inicio rápido
   Autores: Camilo Garcia & Daniel Osorio
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {

    'use strict';

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
        boton.textContent = '⏳ Verificando credenciales...';

        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;

        try {
            await API.auth.login(email, password);
            UI.mensaje('mensaje', '¡Bienvenido! Redirigiendo a su panel de control...', 'exito');
            setTimeout(redirigirSegunRol, 500);
        } catch (error) {
            UI.mensaje('mensaje', error.message, 'error');
            boton.disabled = false;
            boton.textContent = 'Iniciar Sesión';
        }
    });

    // Botones de acceso rápido (1 clic)
    const btnCli = document.getElementById('btn-rapido-cliente');
    const btnPrv = document.getElementById('btn-rapido-proveedor');
    const btnAdm = document.getElementById('btn-rapido-admin');

    if (btnCli) {
        btnCli.addEventListener('click', () => {
            API.auth.iniciarComoDemo('CLIENTE');
            UI.toast('Sesión iniciada como Cliente: Daniel Osorio (daniel.osorio@gmail.com)', 'exito');
            setTimeout(redirigirSegunRol, 400);
        });
    }

    if (btnPrv) {
        btnPrv.addEventListener('click', () => {
            API.auth.iniciarComoDemo('PROVEEDOR');
            UI.toast('Sesión iniciada como Proveedor', 'exito');
            setTimeout(redirigirSegunRol, 400);
        });
    }

    if (btnAdm) {
        btnAdm.addEventListener('click', () => {
            API.auth.iniciarComoDemo('ADMIN');
            UI.toast('Sesión iniciada como Administrador', 'exito');
            setTimeout(redirigirSegunRol, 400);
        });
    }

    function redirigirSegunRol() {
        if (API.esAdmin())          window.location.href = 'admin.html';
        else if (API.esProveedor()) window.location.href = 'proveedor.html';
        else                        window.location.href = 'index.html';
    }

});
