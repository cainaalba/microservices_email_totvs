package com.vda.email.component;

import com.vda.email.dto.DadosEmail;
import com.vda.email.dto.DadosRps;
import com.vda.email.model.ContasEmailModel;
import com.vda.email.service.ConfigEmailService;
import com.vda.email.uteis.Uteis;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

@Service
@Getter
@Setter
@SuppressWarnings("SpellCheckingInspection")
public class EnviaEmail extends javax.mail.Authenticator {
    @Autowired
    private final ConfigEmailService configEmailService;

    Logger logger = LoggerFactory.getLogger(EnviaEmail.class);

    private String filial = "";
    private String usuario = "";
    private String senha = "";
    private String porta = "587"; //DEFAULT 587
    private String portaSegura = "";
    private String servidor = "";
    private String remetente;
    private String assunto = "";
    private String corpoEmail = "";
    private String[] para;
    private String[] comCopia;
    private boolean isAutentica = false;
    private boolean isUsaSSL = false;
    private boolean isHtml = true;
    private boolean isDebug = false;

    public EnviaEmail(ConfigEmailService configEmailService) {
        this.configEmailService = configEmailService;
    }

    public void setDadosEmail(DadosRps dadosRps) {
        logger.info("{} {} / {} | Dados requisicao: {}", dadosRps.filial(), dadosRps.rps(), dadosRps.serie(), dadosRps);

        setFilial(dadosRps.filial());

        ContasEmailModel model = configEmailService.buscaConfigEmail(dadosRps.filial());
        if (!model.getUsuario().isEmpty()) {
            setServidor(model.getServidor().trim());
            setUsaSSL(getFilial().contains("1101")
                    || model.getMetodo().trim().equals("TLS"));

            setPorta(model.getPorta().trim().replace(".0", ""));
            if ((getServidor().toLowerCase().contains("gmail")
                    || getServidor().toLowerCase().contains("lauxen"))
                    && isUsaSSL()) {
                setPorta("465");
            }
            setPortaSegura(getPorta());
            setRemetente(model.getUsuario().trim());
            setUsuario(getRemetente());
            setSenha(model.getSenha().trim());
            setAutentica(model.getMetodo().trim().equals("TLS"));
            logger.info("{} {} / {} | Config. Email: {}", dadosRps.filial(), dadosRps.rps(), dadosRps.serie(), model);
        } else {
            logger.error("{} {} / {} | Configuração do servidor de e-mails não localizadas.", dadosRps.filial(), dadosRps.rps(), dadosRps.serie());
            throw new RuntimeException("Configuração do servidor de e-mails não localizadas.");
        }
    }

    public void send(DadosEmail dados, String html) throws Exception {
        setDadosEmail(dados.getDadosRps());
        setAssunto(dados.getAssunto());
        setPara(dados.getPara());
        setCorpoEmail(html);

        logger.info("{} {} / {} | Assunto: {}", dados.getDadosRps().filial(), dados.getDadosRps().rps(), dados.getDadosRps().serie(), dados.getAssunto());
        logger.info("{} {} / {} | Para: {}", dados.getDadosRps().filial(), dados.getDadosRps().rps(), dados.getDadosRps().serie(), dados.getPara());
        logger.info("{} {} / {} | Anexos: {}", dados.getDadosRps().filial(), dados.getDadosRps().rps(), dados.getDadosRps().serie(), dados.getAnexos());

        if (!getUsuario().isEmpty()
                && !getSenha().isEmpty()
                && getPara().length > 0
                && !getRemetente().isEmpty()
                && !getAssunto().isEmpty()) {
            MailcapCommandMap mc = getMailcapCommandMap();
            CommandMap.setDefaultCommandMap(mc);
            MimeMultipart multipart = new MimeMultipart();

            Session session = Session.getInstance(setProperties(), new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(getRemetente(), getSenha());
                }
            });
            session.setDebug(isDebug());

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(getRemetente()));

            //se tem que enviar cópia oculta para alguém
            if (getComCopia() != null && getComCopia().length > 0) {
                Address[] addressCco = new InternetAddress[getComCopia().length];
                for (int i = 0; i < getComCopia().length; i++) {
                    addressCco[i] = new InternetAddress(getComCopia()[i]);
                }
                message.addRecipients(Message.RecipientType.BCC, addressCco);
            }

            message.setSubject(getAssunto());
            message.setSentDate(new Date());

            //anexos
            BodyPart messageBodyFiles = null;
            for (String anexo : dados.getAnexos()) {
                String[] fileName = anexo.split("/");
                anexo = anexo.replace("/", "\\");
                File file = new File(Uteis.pathRaiz + Uteis.pathArquivosNfse + anexo.toLowerCase());
                if (file.exists()) {
                    messageBodyFiles = new MimeBodyPart();
                    DataSource source = new FileDataSource(Uteis.pathRaiz + Uteis.pathArquivosNfse + anexo.toLowerCase());
                    messageBodyFiles.setDataHandler(new DataHandler(source));
                    messageBodyFiles.setFileName(fileName[fileName.length - 1]);
                    multipart.addBodyPart(messageBodyFiles);
                }
            }

            if (messageBodyFiles == null) {
                logger.error("{} {} / {} | Nenhum anexo encontrado para enviar!", dados.getDadosRps().filial(), dados.getDadosRps().rps(), dados.getDadosRps().serie());
                throw new IOException("Nenhum anexo encontrado para enviar!");
            }

            // corpo da mensagem
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(getCorpoEmail());
            if (isHtml()) {
                messageBodyPart.setHeader("lang", "pt-br");
                messageBodyPart.setHeader("charset", "UTF-8");
                messageBodyPart.setHeader("content-type", "text/html");
            }
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            session.getTransport("smtp");

            //TRATA DO ENVIO PARA CADA DESTINATÁRIO EXCLUSIVAMENTE
            Address[] addressTo = new InternetAddress[1];
            for (int i = 0; i < getPara().length; i++) {
                addressTo[0] = new InternetAddress(getPara()[i]);
                message.setRecipients(MimeMessage.RecipientType.TO, addressTo);
                try {
                    Transport.send(message);
                } catch (SendFailedException e) {
                    Address[] invalidAddressTo = e.getValidUnsentAddresses();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Address invalid : invalidAddressTo) {
                        stringBuilder.append(invalid.toString()).append("; ");
                    }
                    logger.error("Endereço de e-mail ou domínio inválido: {} - {}", stringBuilder, e.getNextException().toString());
                }
            }

            multipart.removeBodyPart(messageBodyPart);
            multipart.removeBodyPart(messageBodyFiles);

            logger.info("{} {} / {} | Envio finalizado com sucesso!", dados.getDadosRps().filial(), dados.getDadosRps().rps(), dados.getDadosRps().serie());
        } else {
            throw new RuntimeException("Usuário, senha, remetente ou assunto inválidos. Tente novamente!");
        }
    }

    private static MailcapCommandMap getMailcapCommandMap() {
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        return mc;
    }

    private Properties setProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", getServidor());
        props.put("mail.smtp.port", getPorta());
        props.put("mail.transport.protocol", "smtp");

        if (isDebug()) props.put("mail.debug", "true");
        if (isAutentica()) props.put("mail.smtp.auth", "true");

        if (isUsaSSL()) {
            if (Objects.equals(getPorta(), "465")) {
                props.put("mail.smtp.ssl.enable", "true");
            }
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        }
        return props;
    }
}