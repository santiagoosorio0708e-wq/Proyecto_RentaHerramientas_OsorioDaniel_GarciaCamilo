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

    // Los accesos rapidos hacen un login real contra el backend con las
    // credenciales del usuario de prueba. Si el servidor esta caido o las
    // credenciales no existen, falla igual que el formulario manual.
    async function accesoRapido(boton, rol, etiqueta) {
        if (!boton) return;
        boton.addEventListener('click', async () => {
            const textoOriginal = boton.textContent;
            boton.disabled = true;
            boton.textContent = '⏳ Entrando...';
            UI.ocultarMensaje('mensaje');
            try {
                await API.auth.iniciarComoDemo(rol);
                UI.toast('Sesión iniciada como ' + etiqueta, 'exito');
                setTimeout(redirigirSegunRol, 400);
            } catch (error) {
                UI.mensaje('mensaje', error.message, 'error');
                boton.disabled = false;
                boton.textContent = textoOriginal;
            }
        });
    }

    accesoRapido(btnCli, 'CLIENTE', 'Cliente');
    accesoRapido(btnPrv, 'PROVEEDOR', 'Proveedor');
    accesoRapido(btnAdm, 'ADMIN', 'Administrador');

    function redirigirSegunRol() {
        if (API.esAdmin())          window.location.href = 'admin.html';
        else if (API.esProveedor()) window.location.href = 'proveedor.html';
        else                        window.location.href = 'index.html';
    }

});
