package com.vda.email.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContasEmailModel {
    String filial;
    String endereco;
    String servidor;
    String porta;
    String usuario;
    String senha;
    String ssl;
    String metodo;
}
