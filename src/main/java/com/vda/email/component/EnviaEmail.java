package com.vda.email.component;

import com.sun.mail.smtp.SMTPAddressFailedException;
import com.vda.email.dto.DadosEmailDto;
import com.vda.email.dto.EmailColetaDto;
import com.vda.email.dto.InformacoesDto;
import com.vda.email.exceptionhandler.ValidacaoException;
import com.vda.email.model.ContasEmailModel;
import com.vda.email.service.ConfigEmailService;
import com.vda.email.uteis.Uteis;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
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

    private void configuracoesContaEmail(InformacoesDto informacoesDto) {
        logger.info("{} {} / {} | Dados requisicao: {}", informacoesDto.filial(), informacoesDto.rps(), informacoesDto.serie(), informacoesDto);

        setFilial(informacoesDto.filial());

        ContasEmailModel model = configEmailService.buscaConfigEmail(getFilial());
        if (model == null) {
            logger.error("{} {} / {} | Configuração do servidor de e-mails não localizadas.", informacoesDto.filial(), informacoesDto.rps(), informacoesDto.serie());
            throw new RuntimeException("Configuração do servidor de e-mails não localizadas.");
        }

        if (!model.getUsuario().isEmpty()) {
            setServidor(model.getServidor().trim());

            if (model.getServidor().contains("smtp2go")) {
                setUsaSSL(false);
            } else {
                setUsaSSL(getFilial().contains("1101")
                        || model.getMetodo().trim().equals("TLS"));
            }

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
            setAutentica(model.getMetodo().trim().equals("TLS") ||
                    model.getServidor().contains("smtp2go"));
            logger.info("{} {} / {} | Config. Email: {}", informacoesDto.filial(), informacoesDto.rps(), informacoesDto.serie(), model);
        }
    }

    public void send(DadosEmailDto dados, String html, boolean enviaRps) throws MessagingException, IOException {
        configuracoesContaEmail(dados.getInformacoesDto());
        setAssunto(dados.getAssunto());
        setPara(dados.getPara());
        setCorpoEmail(html);

        logger.info("{} {} / {} | Assunto: {}", dados.getInformacoesDto().filial(), dados.getInformacoesDto().rps(), dados.getInformacoesDto().serie(), dados.getAssunto());
        logger.info("{} {} / {} | Para: {}", dados.getInformacoesDto().filial(), dados.getInformacoesDto().rps(), dados.getInformacoesDto().serie(), dados.getPara());
        logger.info("{} {} / {} | Anexos: {}", dados.getInformacoesDto().filial(), dados.getInformacoesDto().rps(), dados.getInformacoesDto().serie(), dados.getAnexos());

        if (!getUsuario().isEmpty()
                && !getSenha().isEmpty()
                && getPara().length > 0
                && !getRemetente().isEmpty()
                && !getAssunto().isEmpty()) {
            MailcapCommandMap mc = getMailcapCommandMap();
            CommandMap.setDefaultCommandMap(mc);
            MimeMultipart multipart = new MimeMultipart();

            Session session = getSession();
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

            if (messageBodyFiles == null && enviaRps) {
                logger.error("{} {} / {} | Nenhum anexo encontrado para enviar!", dados.getInformacoesDto().filial(), dados.getInformacoesDto().rps(), dados.getInformacoesDto().serie());
                throw new IOException("Nenhum anexo encontrado para enviar!");
            }

            BodyPart messageBodyPart = getBodyPart();
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            session.getTransport("smtp");

            processaEnvio(message);

            multipart.removeBodyPart(messageBodyPart);
            if (messageBodyFiles != null) {
                multipart.removeBodyPart(messageBodyFiles);
            }
            logger.info("{} {} / {} | Envio finalizado", dados.getInformacoesDto().filial(), dados.getInformacoesDto().rps(), dados.getInformacoesDto().serie());
        } else {
            throw new RuntimeException("Usuário, senha, remetente ou assunto inválidos. Tente novamente!");
        }
    }

    private void processaEnvio(MimeMessage message) {
        //TRATA DO ENVIO PARA CADA DESTINATÁRIO EXCLUSIVAMENTE
        try {
            Address[] addressTo = new InternetAddress[1];
            for (int i = 0; i < getPara().length; i++) {
                addressTo[0] = new InternetAddress(getPara()[i]);
                message.setRecipients(MimeMessage.RecipientType.TO, addressTo);
                Transport.send(message);
                logger.info("Envio finalizado com sucesso para {}", getPara()[i]);
            }
        } catch (AuthenticationFailedException e) {
            logger.error("Falha de autenticação no servidor de e-mails. Entre em contato com o provedor de e-mails! {}", e.getMessage());
            throw new ValidacaoException("Falha de autenticação no servidor de e-mails. Entre em contato com o provedor de e-mails! " + e.getMessage());
        } catch (SMTPAddressFailedException e) {
            logger.error("Erro de SMTP: {} ", e.getMessage());
            throw new ValidacaoException("Erro de SMTP: " + e.getMessage());
        } catch (SendFailedException e) {
            logger.error("Erro ao enviar {} ", e.getNextException().getMessage());
            throw new ValidacaoException("Erro ao enviar: " + e.getMessage());
        } catch (MessagingException e) {
            logger.error("Endereço de e-mail ou domínio inválido: {} ", e.getMessage());
            throw new ValidacaoException("Endereço de e-mail ou domínio inválido: " + e.getMessage());
        }
    }

    private BodyPart getBodyPart() throws MessagingException {
        // corpo da mensagem
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(getCorpoEmail());
        if (isHtml()) {
            messageBodyPart.setHeader("lang", "pt-br");
            messageBodyPart.setHeader("charset", "UTF-8");
            messageBodyPart.setHeader("content-type", "text/html");
        }
        return messageBodyPart;
    }

    private Session getSession() {
        return Session.getInstance(setProperties(), new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(getRemetente(), getSenha());
            }
        });
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

    public void enviaColeta(EmailColetaDto dados, MultipartFile arquivo) throws MessagingException, IOException {
        configuracoesContaEmail(new InformacoesDto(dados.getFilial()));
        setAssunto(dados.getAssunto());
        setPara(dados.getPara());
        setCorpoEmail(dados.getCorpo());

        if (!getUsuario().isEmpty()
                && !getSenha().isEmpty()
                && getPara().length > 0
                && !getRemetente().isEmpty()
                && !getAssunto().isEmpty()) {
            MailcapCommandMap mc = getMailcapCommandMap();
            CommandMap.setDefaultCommandMap(mc);
            MimeMultipart multipart = new MimeMultipart();

            Session session = getSession();
            session.setDebug(isDebug());

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(getRemetente()));

            message.setSubject(getAssunto());
            message.setSentDate(new Date());

            if (arquivo == null) {
                throw new IOException("Nenhum anexo encontrado para enviar!");
            }

            DataSource source = new ByteArrayDataSource(arquivo.getBytes(), arquivo.getContentType());
            MimeBodyPart messageBodyFiles = new MimeBodyPart();
            messageBodyFiles.setDataHandler(new DataHandler(source));
            messageBodyFiles.setFileName(arquivo.getOriginalFilename());
            multipart.addBodyPart(messageBodyFiles);

            BodyPart messageBodyPart = getBodyPart();
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            session.getTransport("smtp");

            processaEnvio(message);

            multipart.removeBodyPart(messageBodyPart);
            multipart.removeBodyPart(messageBodyFiles);
            logger.info("Envio da coleta/orcamento {} finalizado!", dados.getNumeroColetaEOrcamento());
        } else {
            throw new RuntimeException("Usuario, senha, remetente ou assunto invalidos. Tente novamente!");
        }
    }
}