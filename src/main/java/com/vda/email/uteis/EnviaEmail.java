package com.vda.email.uteis;

import com.vda.email.dto.DadosRps;
import com.vda.email.service.ConfigEmailService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
@Getter
@Setter
@SuppressWarnings("SpellCheckingInspection")
public class EnviaEmail extends javax.mail.Authenticator {
    @Autowired
    private final ConfigEmailService configEmailService;

    private String filial = "";
    private String usuario = "";
    private String senha = "";
    private String porta = "587";
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
    private MimeMultipart multipart = new MimeMultipart();

    public EnviaEmail(ConfigEmailService configEmailService) {
        this.configEmailService = configEmailService;
    }

    public void setDadosEmail(DadosRps dadosRps) {
        setFilial(dadosRps.filial());

        List<Map<String, Object>> list = configEmailService.buscaConfigEmail(dadosRps.filial());
        if (!list.isEmpty()) {
            for (Map<String, Object> stringObjectMap : list) {
                setServidor(stringObjectMap.get("SERVIDOR").toString().trim());
                setPorta(stringObjectMap.get("PORTA").toString().trim());
                setPortaSegura(getPorta());
                setRemetente(stringObjectMap.get("ENDERECO").toString().trim());
                setUsuario(getRemetente());
                setSenha(stringObjectMap.get("SENHA").toString().trim());
                setAutentica(true); //ver
                setUsaSSL(stringObjectMap.get("SSL").toString().trim().equals("T"));
            }
        } else {
            throw new RuntimeException("Configuração do servidor de e-mails não localizadas.");
        }
    }

    public void send() throws Exception {
        MailcapCommandMap mc = getMailcapCommandMap();
        CommandMap.setDefaultCommandMap(mc);

        if (!getUsuario().isEmpty()
                && !getSenha().isEmpty()
                && getPara().length > 0
                && !getRemetente().isEmpty()
                && !getAssunto().isEmpty()) {
            Session session = Session.getInstance(setProperties(), new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(getRemetente(), getSenha());
                }
            });

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(getRemetente()));

            Address[] addressTo = new InternetAddress[getPara().length];
            for (int i = 0; i < getPara().length; i++) {
                addressTo[i] = new InternetAddress(getPara()[i]);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

            //se tem de enviar cópia oculta para alguém
            if (getComCopia() != null && getComCopia().length > 0) {
                Address[] addressCco = new InternetAddress[getComCopia().length];
                for (int i = 0; i < getComCopia().length; i++) {
                    addressCco[i] = new InternetAddress(getComCopia()[i]);
                }
                msg.addRecipients(Message.RecipientType.BCC, addressCco);
            }

            msg.setSubject(getAssunto());
            msg.setSentDate(new Date());

            // corpo da mensagem
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(getCorpoEmail());
            if (isHtml()) {
                messageBodyPart.setHeader("lang", "pt-br");
                messageBodyPart.setHeader("charset", "UTF-8");
                messageBodyPart.setHeader("content-type", "text/html");
            }
            getMultipart().addBodyPart(messageBodyPart);
            msg.setContent(getMultipart());

            session.getTransport("smtp");
            Transport.send(msg);
        } else {
            throw new RuntimeException();
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

    public void adicionaAnexo(String[] filename) throws MessagingException {
        for (String anexo : filename) {
//            filename = filename.replace("file:", "").replace("//", "/");
            BodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(anexo);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(anexo);
            getMultipart().addBodyPart(messageBodyPart);
        }
    }

    private Properties setProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", getServidor());
        props.put("mail.smtp.port", getPorta());
        props.put("mail.transport.protocol", "smtp");

        if (isDebug()) props.put("mail.debug", "true");
        if (isAutentica()) props.put("mail.smtp.auth", "true");

        if (isUsaSSL()) {
            if (getFilial().contains("1101") || getFilial().contains("1201")) {
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2"); //ADICIONADO EM 09/03
            } else {
                props.put("mail.smtp.socketFactory.port", getPortaSegura());
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
            }
        }
        return props;
    }
}