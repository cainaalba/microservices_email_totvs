package com.vda.email.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DadosEmailDto {
    private InformacoesDto informacoesDto;
    private String entidade;
    private String assunto;
    private String[] para;
    private String cc;
    private String cco;
    private String[] anexos;
    private String recnoF2;
    private String recno051;
    private String corpo;
}
