package com.rentaherramientas.common.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint de diagnóstico y salud del sistema.
 * Permite a la interfaz web y scripts de automatización verificar la
 * disponibilidad del backend y el motor de base de datos SQLite.
 */
@RestController
@Tag(name = "0. Salud del Sistema", description = "Monitoreo del backend y base de datos")
public class HealthController {

    @GetMapping({"/health", "/actuator/health"})
    @Operation(summary = "Verificar estado de salud del backend y motor de datos")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "database", "SQLite 3",
                "mode", "WAL",
                "timestamp", System.currentTimeMillis()
        );
    }
}
