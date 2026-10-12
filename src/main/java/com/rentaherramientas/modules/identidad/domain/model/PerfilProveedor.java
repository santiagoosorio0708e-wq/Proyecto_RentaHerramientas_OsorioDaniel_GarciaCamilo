package com.rentaherramientas.modules.identidad.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Datos que solo tienen sentido para un usuario con rol PROVEEDOR.
 *
 * Se separa en su propia tabla (perfiles_proveedor) porque si estos campos
 * vivieran dentro de usuarios quedarian en NULL para todos los clientes, que
 * son la mayoria. Es una relacion 1 a 1 opcional.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilProveedor {

    private Long idPerfilProveedor;
    private Long idUsuario;
    private String razonSocial;
    private String nombreComercial;
    private String ciudadOperacion;
    private String sitioWeb;
    private LocalDate fechaVinculacion;
    private boolean verificado;
    private Integer calificacionPromedio;

    /**
     * Un proveedor solo puede publicar herramientas en el catalogo despues de
     * que el administrador haya verificado su documentacion legal.
     */
    public void verificar() {
        if (this.verificado) {
            throw new BusinessRuleException("El proveedor ya se encuentra verificado");
        }
        this.verificado = true;
        if (this.fechaVinculacion == null) {
            this.fechaVinculacion = LocalDate.now();
        }
    }

    public void revocarVerificacion() {
        this.verificado = false;
    }

    public boolean puedePublicarHerramientas() {
        return this.verificado;
    }
}
