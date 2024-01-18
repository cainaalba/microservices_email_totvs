package com.vda.email.service;

import com.vda.email.dto.DadosEmail;
import com.vda.email.uteis.EnviaEmail;
import com.vda.email.uteis.Uteis;
import com.vda.email.uteis.UteisLayoutHtml;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EnviaEmailService {
    private final String pathRaiz = "C:/Totvs/Protheus_Data/";
    private final String pathArquivosNfse = "/xmlnfse/";

    public ResponseEntity<?> enviar(DadosEmail dados) throws Exception {
        String html = UteisLayoutHtml.montaHtmlNfse(dados.getDadosRps());

        EnviaEmail enviaEmail = new EnviaEmail(dados.getDadosRps());
        enviaEmail.adicionaAnexo(dados.getAnexos());
        enviaEmail.setPara(dados.getPara());
        enviaEmail.setCorpoEmail(html);
        enviaEmail.send();

        //deleta arquivos XML e PDF após envio.
        for (String anexo : dados.getAnexos()) {
            new File(pathRaiz + pathArquivosNfse + anexo).delete();
        }

        //ATUALIZAR DADOS DA SF2 E SPED, SE SUCESSO
//        PUT SF2;
//        PUT SPED12;

        /*If SF2->(DbSeek(xFilial("SF2") + cRps + cSerie)) .and. !lCanc
					RecLock("SF2",.F.)
					SF2->F2_ZENVRPS := "S"
					SF2->F2_ZDTMAIL := cValToChar(date()) + " " + substr(time(),1,5) + "-" + Alltrim(UsrRetName(__CUSERID)) + " | " + Alltrim(cMailCli) //ADD 23/01/23
					SF2->(MsUnLock())
				endif

				// Seta status do e-mail como Enviado
				cUpd := "UPDATE "+ cStrSpd +".dbo.SPED051 "
				cUpd += "   SET STATUSMAIL = '2', "
				cUpd += "       EMAIL = '"+ cMailCli +"' "
				cUpd += " WHERE ID_ENT = '" + cIDEnt + "'"
				cUpd += "   AND NFSE_ID = '" + Padr(cSerie,TamSX3("F2_SERIE")[1]) + cRps + "'"
				TCSqlExec(cUpd)*/

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
