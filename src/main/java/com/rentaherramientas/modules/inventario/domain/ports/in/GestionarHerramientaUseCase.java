package com.rentaherramientas.modules.inventario.domain.ports.in;

import com.rentaherramientas.modules.inventario.application.dto.ActualizarHerramientaRequest;
import com.rentaherramientas.modules.inventario.application.dto.CrearHerramientaCommand;
import com.rentaherramientas.modules.inventario.application.dto.EspecificacionTecnicaRequest;
import com.rentaherramientas.modules.inventario.application.dto.ImagenHerramientaRequest;
import com.rentaherramientas.modules.inventario.application.dto.TarifaAlquilerRequest;
import com.rentaherramientas.modules.inventario.domain.model.Herramienta;

/**
 * Puerto de entrada: CRUD del inventario del proveedor.
 *
 * El parametro emailSolicitante viaja en todos los metodos de escritura para
 * poder verificar que un proveedor solo toque SUS propias herramientas.
 */
public interface GestionarHerramientaUseCase {

    Herramienta crear(String emailSolicitante, CrearHerramientaCommand command);

    Herramienta actualizar(String emailSolicitante, Long idHerramienta, ActualizarHerramientaRequest request);

    void eliminar(String emailSolicitante, Long idHerramienta);

    Herramienta agregarImagen(String emailSolicitante, Long idHerramienta, ImagenHerramientaRequest request);

    Herramienta agregarEspecificacion(String emailSolicitante, Long idHerramienta,
                                      EspecificacionTecnicaRequest request);

    Herramienta agregarTarifa(String emailSolicitante, Long idHerramienta, TarifaAlquilerRequest request);

    Herramienta agregarCategoria(String emailSolicitante, Long idHerramienta, Long idCategoria);
}
