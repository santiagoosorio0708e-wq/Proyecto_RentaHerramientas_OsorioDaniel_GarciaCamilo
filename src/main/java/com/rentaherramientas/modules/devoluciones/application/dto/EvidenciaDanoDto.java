package com.rentaherramientas.modules.devoluciones.application.dto;

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
public class EvidenciaDanoDto {
    private Long id;
    private String urlFoto;
    private String descripcionFoto;
    private LocalDateTime fechaSubida;
}
