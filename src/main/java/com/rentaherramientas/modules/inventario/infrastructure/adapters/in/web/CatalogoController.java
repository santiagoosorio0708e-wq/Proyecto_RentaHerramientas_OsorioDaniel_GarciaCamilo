package com.rentaherramientas.modules.inventario.infrastructure.adapters.in.web;

import com.rentaherramientas.common.dto.ApiResponseDto;
import com.rentaherramientas.modules.inventario.application.dto.FiltroCatalogoQuery;
import com.rentaherramientas.modules.inventario.application.dto.HerramientaDtoMapper;
import com.rentaherramientas.modules.inventario.application.dto.HerramientaResponseDto;
import com.rentaherramientas.modules.inventario.application.dto.HerramientaResumenDto;
import com.rentaherramientas.modules.inventario.domain.model.Herramienta;
import com.rentaherramientas.modules.inventario.domain.ports.in.ConsultarCatalogoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Catalogo publico de herramientas.
 *
 * Es el unico controlador del modulo que no exige token: un visitante debe
 * poder explorar las herramientas y sus precios antes de registrarse.
 */
@RestController
@RequestMapping("/catalogo")
@RequiredArgsConstructor
@SecurityRequirements
@Tag(name = "3. Catalogo Publico",
        description = "Exploracion de herramientas disponibles. No requiere autenticacion.")
public class CatalogoController {

    private final ConsultarCatalogoUseCase consultarCatalogoUseCase;

    @GetMapping
    @Operation(summary = "Buscar herramientas en el catalogo",
            description = "Todos los filtros son opcionales y se combinan entre si. "
                    + "Sin filtros devuelve todas las herramientas disponibles.")
    @ApiResponse(responseCode = "200", description = "Busqueda realizada correctamente")
    public ResponseEntity<ApiResponseDto<List<HerramientaResumenDto>>> buscar(

            @Parameter(description = "Texto en nombre, marca o modelo", example = "martillo")
            @RequestParam(required = false) String texto,

            @Parameter(description = "Identificador de la categoria", example = "1")
            @RequestParam(required = false) Long idCategoria,

            @Parameter(description = "Precio diario minimo", example = "50000")
            @RequestParam(required = false) BigDecimal precioMinimo,

            @Parameter(description = "Precio diario maximo", example = "300000")
            @RequestParam(required = false) BigDecimal precioMaximo,

            @Parameter(description = "Ciudad de operacion del proveedor", example = "Bucaramanga")
            @RequestParam(required = false) String ubicacion,

            @Parameter(description = "Mostrar solo las disponibles", example = "true")
            @RequestParam(required = false, defaultValue = "true") Boolean soloDisponibles) {

        FiltroCatalogoQuery filtro = FiltroCatalogoQuery.builder()
                .texto(texto)
                .idCategoria(idCategoria)
                .precioMinimo(precioMinimo)
                .precioMaximo(precioMaximo)
                .ubicacion(ubicacion)
                .soloDisponibles(soloDisponibles)
                .build();

        List<Herramienta> herramientas = consultarCatalogoUseCase.buscar(filtro);
        List<HerramientaResumenDto> dtos = herramientas.stream()
                .map(HerramientaDtoMapper::toResumenDto)
                .toList();

        return ResponseEntity.ok(ApiResponseDto.ok(
                "Se encontraron " + dtos.size() + " herramienta(s)", dtos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver el detalle de una herramienta",
            description = "Devuelve la ficha completa: galeria, especificaciones tecnicas y tarifas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Herramienta encontrada"),
            @ApiResponse(responseCode = "404", description = "La herramienta no existe")
    })
    public ResponseEntity<ApiResponseDto<HerramientaResponseDto>> detalle(
            @Parameter(description = "Identificador de la herramienta", example = "1")
            @PathVariable Long id) {

        Herramienta herramienta = consultarCatalogoUseCase.obtenerPorId(id);
        return ResponseEntity.ok(
                ApiResponseDto.ok(HerramientaDtoMapper.toResponseDto(herramienta)));
    }

    @GetMapping("/categoria/{idCategoria}")
    @Operation(summary = "Listar herramientas de una categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "404", description = "La categoria no existe")
    })
    public ResponseEntity<ApiResponseDto<List<HerramientaResumenDto>>> porCategoria(
            @Parameter(description = "Identificador de la categoria", example = "1")
            @PathVariable Long idCategoria) {

        List<HerramientaResumenDto> dtos = consultarCatalogoUseCase.listarPorCategoria(idCategoria)
                .stream().map(HerramientaDtoMapper::toResumenDto).toList();

        return ResponseEntity.ok(ApiResponseDto.ok("Listado obtenido correctamente", dtos));
    }
}
