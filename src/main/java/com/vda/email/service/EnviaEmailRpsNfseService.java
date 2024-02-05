package com.vda.email.service;

import com.vda.email.dto.DadosEmail;
import com.vda.email.component.EnviaEmail;
import com.vda.email.repo.SF2Repo;
import com.vda.email.repo.SPED051Repo;
import com.vda.email.uteis.Uteis;
import com.vda.email.uteis.UteisLayoutHtml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EnviaEmailRpsNfseService {
    @Autowired
    private final EnviaEmail enviaEmail;

    @Autowired
    private final SF2Repo repoSf2;

    @Autowired
    private final SPED051Repo repoSped051;

    public EnviaEmailRpsNfseService(EnviaEmail enviaEmail,
                                    SF2Repo repoSf2,
                                    SPED051Repo repoSped051) {
        this.enviaEmail = enviaEmail;
        this.repoSf2 = repoSf2;
        this.repoSped051 = repoSped051;
    }

    public ResponseEntity<?> enviarRpsNfse(DadosEmail dados) {
        try {
            String html = UteisLayoutHtml.montaHtmlNfse(dados.getDadosRps());
            enviaEmail.send(dados, html);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!dados.getDadosRps().isCanc()) {
            //PUT F2_ZENVRPS;
            atualizarSf2(dados.getRecnoF2(), dados.getPara());

            //PUT SPED12;
            atualizarSped051(dados.getRecno051(), dados.getPara());
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private void atualizarSf2(String recno, String[] para) {
        var doc = repoSf2.getReferenceById(recno);
        doc.atualizaStatusMail(recno, "JOB Email", para);
    }

    private void atualizarSped051(String recno, String[] para) {
        var doc = repoSped051.getReferenceById(recno);
        doc.atualizaStatusMail(para);
    }
}
