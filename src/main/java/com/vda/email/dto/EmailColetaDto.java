package com.vda.email.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailColetaDto {
    String corpo;
    String assunto;
    String[] para;
    String filial;
    String numeroColetaEOrcamento;
}
