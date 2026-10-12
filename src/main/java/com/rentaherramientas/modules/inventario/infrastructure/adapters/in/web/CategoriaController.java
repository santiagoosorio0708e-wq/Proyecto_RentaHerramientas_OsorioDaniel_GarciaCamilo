package com.rentaherramientas.modules.inventario.infrastructure.adapters.in.web;

import com.rentaherramientas.common.dto.ApiResponseDto;
import com.rentaherramientas.modules.inventario.application.dto.CategoriaRequest;
import com.rentaherramientas.modules.inventario.application.dto.CategoriaResponseDto;
import com.rentaherramientas.modules.inventario.application.dto.HerramientaDtoMapper;
import com.rentaherramientas.modules.inventario.domain.model.Categoria;
import com.rentaherramientas.modules.inventario.domain.ports.in.GestionarCategoriaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Administracion de las categorias del catalogo.
 *
 * La lectura es publica (el catalogo necesita mostrar el menu de categorias)
 * pero crear, editar y borrar queda reservado al administrador.
 */
@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Tag(name = "5. Categorias",
        description = "Consulta publica y administracion de las categorias del catalogo")
public class CategoriaController {

    private final GestionarCategoriaUseCase gestionarCategoriaUseCase;

    @GetMapping
    @Operation(summary = "Listar todas las categorias",
            description = "Endpoint publico, lo consume el filtro del catalogo.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<ApiResponseDto<List<CategoriaResponseDto>>> listar() {
        List<CategoriaResponseDto> dtos = gestionarCategoriaUseCase.listarTodas()
                .stream().map(HerramientaDtoMapper::toCategoriaDto).toList();
        return ResponseEntity.ok(ApiResponseDto.ok("Categorias obtenidas correctamente", dtos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una categoria por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "La categoria no existe")
    })
    public ResponseEntity<ApiResponseDto<CategoriaResponseDto>> obtener(@PathVariable Long id) {
        Categoria categoria = gestionarCategoriaUseCase.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponseDto.ok(HerramientaDtoMapper.toCategoriaDto(categoria)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria creada"),
            @ApiResponse(responseCode = "403", description = "Solo el administrador puede crear categorias"),
            @ApiResponse(responseCode = "422", description = "Ya existe una categoria con ese nombre")
    })
    public ResponseEntity<ApiResponseDto<CategoriaResponseDto>> crear(
            @Valid @RequestBody CategoriaRequest request) {

        Categoria categoria = gestionarCategoriaUseCase.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.ok(
                "Categoria creada correctamente", HerramientaDtoMapper.toCategoriaDto(categoria)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria actualizada"),
            @ApiResponse(responseCode = "404", description = "La categoria no existe")
    })
    public ResponseEntity<ApiResponseDto<CategoriaResponseDto>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request) {

        Categoria categoria = gestionarCategoriaUseCase.actualizar(id, request);
        return ResponseEntity.ok(ApiResponseDto.ok(
                "Categoria actualizada correctamente", HerramientaDtoMapper.toCategoriaDto(categoria)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una categoria",
            description = "Solo se permite si ninguna herramienta la esta usando.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria eliminada"),
            @ApiResponse(responseCode = "422", description = "La categoria tiene herramientas asociadas")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gestionarCategoriaUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
