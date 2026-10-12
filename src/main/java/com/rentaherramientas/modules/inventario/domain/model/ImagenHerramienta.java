package com.rentaherramientas.modules.inventario.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Foto de la galeria de una herramienta.
 *
 * Una herramienta puede tener muchas fotos y esa lista es independiente de sus
 * categorias o de sus tarifas: por eso en 4FN va en una tabla aparte
 * (herramientas_imagenes) y no como columnas foto1, foto2, foto3.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"idHerramienta", "urlFoto"})
public class ImagenHerramienta {

    private Long idImagen;
    private Long idHerramienta;
    private String urlFoto;
    private String descripcion;
    private boolean principal;
    private Integer orden;

    public static ImagenHerramienta de(String urlFoto, String descripcion, boolean principal, Integer orden) {
        if (urlFoto == null || urlFoto.isBlank()) {
            throw new BusinessRuleException("La URL de la imagen es obligatoria");
        }
        if (urlFoto.trim().length() > 255) {
            throw new BusinessRuleException("La URL de la imagen no puede superar 255 caracteres");
        }
        return ImagenHerramienta.builder()
                .urlFoto(urlFoto.trim())
                .descripcion(descripcion)
                .principal(principal)
                .orden(orden == null ? 1 : orden)
                .build();
    }

    public void marcarComoPrincipal() {
        this.principal = true;
    }

    public void quitarPrincipal() {
        this.principal = false;
    }
}
