package com.vda.email.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vda.email.dto.DadosEmailDto;
import com.vda.email.component.EnviaEmail;
import com.vda.email.dto.EmailColetaDto;
import com.vda.email.exceptionhandler.ValidacaoException;
import com.vda.email.repo.SF2Repo;
import com.vda.email.repo.SPED051Repo;
import com.vda.email.uteis.UteisLayoutHtml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;

@Service
public class EnviaEmailService {
    @Autowired
    private final EnviaEmail enviaEmail;

    @Autowired
    private final SF2Repo repoSf2;

    @Autowired
    private final SPED051Repo repoSped051;

    public EnviaEmailService(EnviaEmail enviaEmail,
                             SF2Repo repoSf2,
                             SPED051Repo repoSped051) {
        this.enviaEmail = enviaEmail;
        this.repoSf2 = repoSf2;
        this.repoSped051 = repoSped051;
    }

    public void enviarRpsNfse(DadosEmailDto dados) {
        try {
            String html = UteisLayoutHtml.montaHtmlNfse(dados.getInformacoesDto());
            enviaEmail.send(dados, html, true);
        } catch (MessagingException | IOException e) {
            throw new ValidacaoException(e.getLocalizedMessage());
        }

        if (!dados.getInformacoesDto().isCanc()) {
            //PUT F2_ZENVRPS;
            atualizarSf2(dados.getRecnoF2(), dados.getPara());

            //PUT SPED12;
            atualizarSped051(dados.getRecno051(), dados.getPara());
        }
    }

    public void enviaEmail(DadosEmailDto dados) {
        try {
            enviaEmail.send(dados, dados.getCorpo(), false);
        } catch (MessagingException | IOException | RuntimeException e) {
            throw new ValidacaoException(e.getLocalizedMessage());
        }
    }

    private void atualizarSf2(String recno, String[] para) {
        var doc = repoSf2.getReferenceById(recno);
        doc.atualizaStatusMail(recno, "JOB Email", para);
    }

    private void atualizarSped051(String recno, String[] para) {
        var doc = repoSped051.getReferenceById(recno);
        doc.atualizaStatusMail(para);
    }

    public void enviaEmailColeta(String dados, MultipartFile arquivo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            var emailDto = objectMapper.readValue(dados, EmailColetaDto.class);
            enviaEmail.enviaColeta(emailDto, arquivo);
        } catch (MessagingException | IOException | RuntimeException e) {
            throw new ValidacaoException(e.getLocalizedMessage());
        }
    }
}
