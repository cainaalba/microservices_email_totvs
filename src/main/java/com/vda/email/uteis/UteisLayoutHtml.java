package com.vda.email.uteis;

import com.vda.email.dto.DadosRps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UteisLayoutHtml {
    private static final String pathRaiz = "C:/Totvs/Protheus_Data/";
    private static final String pathLayouts = "/workflow/";

    public static String montaHtmlNfse(DadosRps dadosRps) throws IOException {
        String nomeLayoutMailRps = "vckcancnfse.htm";

        String htmlContent = lerArquivoHtml(pathRaiz + pathLayouts + nomeLayoutMailRps);
        return personalizarLayoutNfse(htmlContent, dadosRps);
    }

    private static String lerArquivoHtml(String path) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("/n");
            }
        }
        return stringBuilder.toString();
    }

    private static String personalizarLayoutNfse(String htmlContent, DadosRps dadosRps) {
        return htmlContent
                .replace("%cSerie%",dadosRps.serie())
                .replace("%cNumDoc%", dadosRps.rps())
                .replace("%cNFse%",dadosRps.nfse())
                .replace("%cCodNfse%",dadosRps.codNfse())
                .replace("%cRazao%",dadosRps.nomeCli())
                .replace("%cCnpj%",dadosRps.docCli())
                .replace("%cNmEmp%",dadosRps.nomeFilial())
                .replace("%motivo%", dadosRps.motivo())
                .replace("%cMsgAdic%", dadosRps.msgAdic());
    }
}
