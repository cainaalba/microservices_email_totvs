package com.vda.email.uteis;

import com.vda.email.dto.DadosRps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UteisLayoutHtml {
    public static String montaHtmlNfse(DadosRps dadosRps) throws IOException {
        String htmlContent = lerArquivoHtml("vcknfse.htm");
        return personalizarLayoutNfse(htmlContent, dadosRps);
    }

    private static String lerArquivoHtml(String nomeArquivo) throws IOException {
        Path path = Paths.get(Uteis.pathRaiz + Uteis.pathLayouts + nomeArquivo);
        byte[] fileBytes = Files.readAllBytes(path);
        return new String(fileBytes, StandardCharsets.ISO_8859_1);
    }

    private static String personalizarLayoutNfse(String htmlContent, DadosRps dadosRps) {
        return htmlContent
                .replace("%cSerie%", dadosRps.serie())
                .replace("%cNumDoc%", dadosRps.rps())
                .replace("%cNFse%", dadosRps.nfse())
                .replace("%cCodNfse%", dadosRps.codNfse())
                .replace("%cRazao%", dadosRps.nomeCli())
                .replace("%cCnpj%", dadosRps.docCli())
                .replace("%cNmEmp%", dadosRps.nomeFilial())
                .replace("%motivo%", dadosRps.motivo())
                .replace("%cMsgAdic%", dadosRps.msgAdic());
    }
}
