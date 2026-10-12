package com.rentaherramientas.modules.inventario.domain.ports.out;

/**
 * Puerto hacia el bounded context de Identidad.
 *
 * El contexto de Inventario necesita saber quien es el proveedor que esta
 * pidiendo una operacion, pero NO debe importar las clases de Identidad
 * directamente: eso acoplaria los dos contextos.
 *
 * Esta interfaz actua como capa anticorrupcion (ACL en DDD): Inventario define
 * en su propio lenguaje lo poco que necesita saber, y un adaptador se encarga
 * de traducirlo consultando el otro modulo.
 */
public interface ProveedorPort {

    /** Id del usuario que corresponde a ese correo. */
    Long obtenerIdUsuarioPorEmail(String email);

    boolean esAdministrador(String email);

    boolean esProveedor(String email);

    /** Un proveedor sin verificar no puede publicar herramientas en el catalogo. */
    boolean estaVerificado(String email);

    /** Ciudad de operacion del proveedor, usada para filtrar el catalogo por ubicacion. */
    String ciudadDeOperacion(Long idProveedor);
}
