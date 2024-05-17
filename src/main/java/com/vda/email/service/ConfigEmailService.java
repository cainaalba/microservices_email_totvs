package com.vda.email.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vda.email.model.ContasEmailMapper;
import com.vda.email.model.ContasEmailModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConfigEmailService {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public ConfigEmailService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ContasEmailModel buscaConfigEmail(String filial) {
        String query = "SELECT LTRIM(RTRIM(WF7_FILIAL)) FILIAL,\n" +
                "              LTRIM(RTRIM(WF7_ENDERE)) ENDERECO,\n" +
                "              LTRIM(RTRIM(WF7_SMTPSR)) SERVIDOR,\n" +
                "              LTRIM(RTRIM(WF7_SMTPPR)) PORTA,\n" +
                "              LTRIM(RTRIM(WF7_AUTUSU)) USUARIO,\n" +
                "              LTRIM(RTRIM(WF7_AUTSEN)) SENHA, \n" +
                "              LTRIM(RTRIM(WF7_SSL)) SSL,\n" +
                "              LTRIM(RTRIM(WF7_SMTPSE)) METODO " +
                "FROM WF7010\n" +
                "WHERE 1 = 1\n" +
                "  AND D_E_L_E_T_ = ''\n" +
                "  AND WF7_FILIAL = SUBSTRING('" + filial + "',0,5)\n" +
                "GROUP BY WF7_FILIAL,\n" +
                "         WF7_ENDERE,\n" +
                "         WF7_SMTPSR,\n" +
                "         WF7_SMTPPR,\n" +
                "         WF7_AUTUSU,\n" +
                "         WF7_AUTSEN, \n" +
                "         WF7_SSL,\n" +
                "         WF7_SMTPSE";
        return executar(query);
    }

    private ContasEmailModel executar(String query) {
        var result = jdbcTemplate.query(query, new ContasEmailMapper());
        try {
            JsonNode jsonNode = new ObjectMapper().valueToTree(result);
            return new ObjectMapper().readValue(jsonNode.get(0).toString(), ContasEmailModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
