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
    DadosRps dadosRps;
    String entidade;
    String assunto;
    String[] para;
    String cc;
    String cco;
    String[] anexos;
    String usuario;
}
