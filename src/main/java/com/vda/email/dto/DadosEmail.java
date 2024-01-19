package com.vda.email.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DadosEmail {
    private DadosRps dadosRps;
    private String entidade;
    private String assunto;
    private String[] para;
    private String cc;
    private String cco;
    private String usuario;
    private String[] anexos;
}
