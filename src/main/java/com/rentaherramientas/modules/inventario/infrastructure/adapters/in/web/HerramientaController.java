package com.rentaherramientas.modules.inventario.infrastructure.adapters.in.web;

import com.rentaherramientas.common.dto.ApiResponseDto;
import com.rentaherramientas.modules.inventario.application.dto.ActualizarHerramientaRequest;
import com.rentaherramientas.modules.inventario.application.dto.CambiarEstadoHerramientaRequest;
import com.rentaherramientas.modules.inventario.application.dto.CrearHerramientaCommand;
import com.rentaherramientas.modules.inventario.application.dto.EspecificacionTecnicaRequest;
import com.rentaherramientas.modules.inventario.application.dto.HerramientaDtoMapper;
import com.rentaherramientas.modules.inventario.application.dto.HerramientaResponseDto;
import com.rentaherramientas.modules.inventario.application.dto.HerramientaResumenDto;
import com.rentaherramientas.modules.inventario.application.dto.ImagenHerramientaRequest;
import com.rentaherramientas.modules.inventario.application.dto.TarifaAlquilerRequest;
import com.rentaherramientas.modules.inventario.domain.model.EstadoHerramienta;
import com.rentaherramientas.modules.inventario.domain.model.Herramienta;
import com.rentaherramientas.modules.inventario.domain.ports.in.CambiarEstadoInventarioUseCase;
import com.rentaherramientas.modules.inventario.domain.ports.in.ConsultarCatalogoUseCase;
import com.rentaherramientas.modules.inventario.domain.ports.in.GestionarHerramientaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Gestion del inventario por parte del proveedor y del administrador.
 *
 * Todos los endpoints exigen token. Ademas, el servicio de aplicacion verifica
 * que un proveedor solo pueda tocar sus propias herramientas.
 */
@RestController
@RequestMapping("/herramientas")
@RequiredArgsConstructor
@Tag(name = "4. Inventario del Proveedor",
        description = "Alta, edicion, galeria, ficha tecnica, tarifas y control de estados")
public class HerramientaController {

    private final GestionarHerramientaUseCase gestionarHerramientaUseCase;
    private final ConsultarCatalogoUseCase consultarCatalogoUseCase;
    private final CambiarEstadoInventarioUseCase cambiarEstadoInventarioUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROVEEDOR','ADMIN')")
    @Operation(summary = "Registrar una herramienta",
            description = "Da de alta la herramienta con su galeria, ficha tecnica y tarifas. "
                    + "El proveedor debe estar verificado por el administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Herramienta registrada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente o invalido"),
            @ApiResponse(responseCode = "403", description = "El rol no permite esta operacion"),
            @ApiResponse(responseCode = "422", description = "Numero de serie duplicado o proveedor sin verificar")
    })
    public ResponseEntity<ApiResponseDto<HerramientaResponseDto>> crear(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CrearHerramientaCommand command) {

        Herramienta herramienta = gestionarHerramientaUseCase.crear(userDetails.getUsername(), command);
        HerramientaResponseDto dto = HerramientaDtoMapper.toResponseDto(herramienta);

        return ResponseEntity
                .created(URI.create("/api/v1/herramientas/" + herramienta.getIdHerramienta()))
                .body(ApiResponseDto.ok("Herramienta registrada con exito", dto));
    }

    @GetMapping("/mi-inventario")
    @PreAuthorize("hasAnyRole('PROVEEDOR','ADMIN')")
    @Operation(summary = "Listar mi inventario",
            description = "Devuelve las herramientas del proveedor autenticado.")
    @ApiResponse(responseCode = "200", description = "Inventario obtenido")
    public ResponseEntity<ApiResponseDto<List<HerramientaResumenDto>>> miInventario(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<HerramientaResumenDto> dtos =
                consultarCatalogoUseCase.listarMiInventario(userDetails.getUsername())
                        .stream().map(HerramientaDtoMapper::toResumenDto).toList();

        return ResponseEntity.ok(ApiResponseDto.ok(
                "Inventario obtenido: " + dtos.size() + " herramienta(s)", dtos));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVEEDOR','ADMIN')")
    @Operation(summary = "Consultar el detalle de una herramienta del inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Herramienta encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe")
    })
    public ResponseEntity<ApiResponseDto<HerramientaResponseDto>> detalle(@PathVariable Long id) {
        Herramienta herramienta = consultarCatalogoUseCase.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponseDto.ok(HerramientaDtoMapper.toResponseDto(herramienta)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVEEDOR','ADMIN')")
    @Operation(summary = "Actualizar los datos de una herramienta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Herramienta actualizada"),
            @ApiResponse(responseCode = "404", description = "No existe"),
            @ApiResponse(responseCode = "422", description = "La herramienta pertenece a otro proveedor")
    })
    public ResponseEntity<ApiResponseDto<HerramientaResponseDto>> actualizar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ActualizarHerramientaRequest request) {

        Herramienta herramienta =
                gestionarHerramientaUseCase.actualizar(userDetails.getUsername(), id, request);

        return ResponseEntity.ok(ApiResponseDto.ok(
                "Herramienta actualizada correctamente", HerramientaDtoMapper.toResponseDto(herramienta)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVEEDOR','ADMIN')")
    @Operation(summary = "Eliminar una herramienta",
            description = "No se permite eliminar una herramienta que este ALQUILADA.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Herramienta eliminada"),
            @ApiResponse(responseCode = "422", description = "La herramienta esta alquilada o es de otro proveedor")
    })
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        gestionarHerramientaUseCase.eliminar(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('PROVEEDOR','ADMIN')")
    @Operation(summary = "Cambiar el estado operativo",
            description = "Valida que la transicion sea permitida. Por ejemplo, de "
                    + "FUERA_DE_SERVICIO solo se puede pasar a EN_MANTENIMIENTO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "422", description = "Transicion de estado no permitida")
    })
    public ResponseEntity<ApiResponseDto<HerramientaResponseDto>> cambiarEstado(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoHerramientaRequest request) {

        EstadoHerramienta nuevoEstado = EstadoHerramienta.valueOf(request.nuevoEstado().toUpperCase());

        Herramienta herramienta = cambiarEstadoInventarioUseCase.cambiarEstado(
                userDetails.getUsername(), id, nuevoEstado, request.motivo());

        return ResponseEntity.ok(ApiResponseDto.ok(
                "Estado actualizado a " + nuevoEstado.name(),
                HerramientaDtoMapper.toResponseDto(herramienta)));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('PROVEEDOR','ADMIN')")
    @Operation(summary = "Listar herramientas por estado",
            description = "Util para el tablero de control: ver que esta en mantenimiento o fuera de servicio.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<ApiResponseDto<List<HerramientaResumenDto>>> porEstado(
            @Parameter(description = "DISPONIBLE, ALQUILADA, EN_MANTENIMIENTO o FUERA_DE_SERVICIO",
                    example = "EN_MANTENIMIENTO")
            @PathVariable String estado) {

        EstadoHerramienta estadoEnum = EstadoHerramienta.valueOf(estado.toUpperCase());
        List<HerramientaResumenDto> dtos = cambiarEstadoInventarioUseCase.listarPorEstado(estadoEnum)
                .stream().map(HerramientaDtoMapper::toResumenDto).toList();

        return ResponseEntity.ok(ApiResponseDto.ok("Listado obtenido correctamente", dtos));
    }

    // ------------------------------------------------------------------
    // Colecciones del agregado (galeria, ficha tecnica, tarifas)
    // ------------------------------------------------------------------

    @PostMapping("/{id}/imagenes")
    @PreAuthorize("hasAnyRole('PROVEEDOR','ADMIN')")
    @Operation(summary = "Agregar una foto a la galeria",
            description = "Si se marca como principal, la portada anterior deja de serlo.")
    @ApiResponse(responseCode = "200", description = "Imagen agregada")
    public ResponseEntity<ApiResponseDto<HerramientaResponseDto>> agregarImagen(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ImagenHerramientaRequest request) {

        Herramienta herramienta =
                gestionarHerramientaUseCase.agregarImagen(userDetails.getUsername(), id, request);

        return ResponseEntity.ok(ApiResponseDto.ok(
                "Imagen agregada correctamente", HerramientaDtoMapper.toResponseDto(herramienta)));
    }

    @PostMapping("/{id}/especificaciones")
    @PreAuthorize("hasAnyRole('PROVEEDOR','ADMIN')")
    @Operation(summary = "Agregar una especificacion tecnica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especificacion agregada"),
            @ApiResponse(responseCode = "422", description = "Ese atributo ya estaba registrado")
    })
    public ResponseEntity<ApiResponseDto<HerramientaResponseDto>> agregarEspecificacion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody EspecificacionTecnicaRequest request) {

        Herramienta herramienta =
                gestionarHerramientaUseCase.agregarEspecificacion(userDetails.getUsername(), id, request);

        return ResponseEntity.ok(ApiResponseDto.ok(
                "Especificacion agregada correctamente", HerramientaDtoMapper.toResponseDto(herramienta)));
    }

    @PostMapping("/{id}/tarifas")
    @PreAuthorize("hasAnyRole('PROVEEDOR','ADMIN')")
    @Operation(summary = "Agregar una tarifa",
            description = "Permite cobrar distinto por dia, fin de semana, semana o mes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarifa agregada"),
            @ApiResponse(responseCode = "422", description = "Ya existe una tarifa de ese tipo")
    })
    public ResponseEntity<ApiResponseDto<HerramientaResponseDto>> agregarTarifa(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TarifaAlquilerRequest request) {

        Herramienta herramienta =
                gestionarHerramientaUseCase.agregarTarifa(userDetails.getUsername(), id, request);

        return ResponseEntity.ok(ApiResponseDto.ok(
                "Tarifa agregada correctamente", HerramientaDtoMapper.toResponseDto(herramienta)));
    }

    @PostMapping("/{id}/categorias/{idCategoria}")
    @PreAuthorize("hasAnyRole('PROVEEDOR','ADMIN')")
    @Operation(summary = "Asociar una categoria adicional",
            description = "Una herramienta puede pertenecer a varias categorias (relacion 4FN).")
    @ApiResponse(responseCode = "200", description = "Categoria asociada")
    public ResponseEntity<ApiResponseDto<HerramientaResponseDto>> agregarCategoria(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @PathVariable Long idCategoria) {

        Herramienta herramienta =
                gestionarHerramientaUseCase.agregarCategoria(userDetails.getUsername(), id, idCategoria);

        return ResponseEntity.ok(ApiResponseDto.ok(
                "Categoria asociada correctamente", HerramientaDtoMapper.toResponseDto(herramienta)));
    }
}
