package com.vda.email.dto;

public record DadosRps(
        String filial,
        String nomeFilial,
        String rps,
        String serie,
        String nfse,
        String codNfse,
        String cliente,
        String loja,
        String nomeCli,
        String docCli,
        String msgAdic,
        String motivo,
        boolean isCanc) {
}
