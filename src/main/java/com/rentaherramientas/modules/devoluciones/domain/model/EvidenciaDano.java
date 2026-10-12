package com.rentaherramientas.modules.devoluciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenciaDano {
    private Long id;
    private Long idReporteDano;
    private String urlFoto;
    private String descripcionFoto;
    private LocalDateTime fechaSubida;
}
