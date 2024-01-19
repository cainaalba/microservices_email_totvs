package com.vda.email.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConfigEmailService {

    @Autowired
    private final EntityManager eManager;

    public ConfigEmailService(EntityManager eManager) {
        this.eManager = eManager;
    }

    public List<Map<String, Object>> buscaConfigEmail(String filial) {
        String query = "SELECT WF7_FILIAL FILIAL,\n" +
                "       WF7_ENDERE ENDERECO,\n" +
                "       WF7_SMTPSR SERVIDOR,\n" +
                "       WF7_SMTPPR PORTA,\n" +
                "       WF7_AUTUSU USUARIO,\n" +
                "       WF7_AUTSEN SENHA, \n" +
                "       WF7_SSL SSL,\n" +
                "       WF7_SMTPSE METODO " +
                "FROM WF7010\n" +
                "WHERE 1 = 1\n" +
                "AND D_E_L_E_T_ = ''\n" +
                "AND WF7_FILIAL = SUBSTRING('" + filial + "',0,5)\n" +
                "GROUP BY WF7_FILIAL,\n" +
                "       WF7_ENDERE,\n" +
                "       WF7_SMTPSR,\n" +
                "       WF7_SMTPPR,\n" +
                "       WF7_AUTUSU,\n" +
                "       WF7_AUTSEN, \n" +
                "       WF7_SSL,\n" +
                "       WF7_SMTPSE";
        return executar(query);
    }

    public List<Map<String, Object>> executar(String query) {
        Query qry = eManager.createNativeQuery(query);
        NativeQueryImpl<Map<String, Object>> nativeQuery = (NativeQueryImpl<Map<String, Object>>) qry;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return nativeQuery.getResultList();
    }
}
