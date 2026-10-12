package com.rentaherramientas.modules.identidad.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos que solo aplican a un usuario con rol CLIENTE.
 *
 * Igual que el perfil de proveedor, va en su propia tabla (perfiles_cliente)
 * para no dejar columnas vacias en usuarios.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilCliente {

    private Long idPerfilCliente;
    private Long idUsuario;
    private String tipoCliente;      // PARTICULAR o EMPRESARIAL
    private String ciudadResidencia;
    private BigDecimal cupoCredito;
    private LocalDate fechaRegistro;
    private Integer alquileresRealizados;

    public static final String PARTICULAR = "PARTICULAR";
    public static final String EMPRESARIAL = "EMPRESARIAL";

    /**
     * Antes de aprobar una reserva se revisa que el monto no supere el cupo
     * aprobado del cliente. Un cupo nulo o cero significa "solo pago anticipado".
     */
    public boolean tieneCupoDisponible(BigDecimal montoReserva) {
        if (montoReserva == null) {
            throw new BusinessRuleException("El monto de la reserva es obligatorio para validar el cupo");
        }
        if (cupoCredito == null) {
            return false;
        }
        return cupoCredito.compareTo(montoReserva) >= 0;
    }

    public void registrarAlquiler() {
        this.alquileresRealizados = (this.alquileresRealizados == null ? 0 : this.alquileresRealizados) + 1;
    }

    public boolean esEmpresarial() {
        return EMPRESARIAL.equalsIgnoreCase(this.tipoCliente);
    }
}
