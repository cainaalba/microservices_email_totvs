package com.vda.email.uteis;

import com.vda.email.dto.InformacoesDto;
import com.vda.email.exceptionhandler.ValidacaoException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UteisLayoutHtml {
    public static String montaHtmlNfse(InformacoesDto informacoesDto) throws IOException {
        String htmlContent = lerArquivoHtml("vcknfse.htm");

        if (informacoesDto == null) {
            throw new ValidacaoException("Informações do documento não localizadas!");
        }

        return personalizarLayoutNfse(htmlContent, informacoesDto);
    }

    private static String lerArquivoHtml(String nomeArquivo) throws IOException {
        Path path = Paths.get(Uteis.pathRaiz + Uteis.pathLayouts + nomeArquivo);
        byte[] fileBytes = Files.readAllBytes(path);
        return new String(fileBytes, StandardCharsets.ISO_8859_1);
    }

    private static String personalizarLayoutNfse(String htmlContent, InformacoesDto informacoesDto) {
        return htmlContent
                .replace("%cSerie%", informacoesDto.serie())
                .replace("%cNumDoc%", informacoesDto.rps())
                .replace("%cNFse%", informacoesDto.nfse())
                .replace("%cCodNfse%", informacoesDto.codNfse())
                .replace("%cRazao%", informacoesDto.nomeCli())
                .replace("%cCnpj%", informacoesDto.docCli())
                .replace("%cNmEmp%", informacoesDto.nomeFilial())
                .replace("%motivo%", informacoesDto.motivo())
                .replace("%cMsgAdic%", informacoesDto.msgAdic());
    }
}
