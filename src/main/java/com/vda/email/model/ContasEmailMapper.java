package com.vda.email.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContasEmailMapper implements RowMapper<ContasEmailModel> {
    @Override
    public ContasEmailModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new ContasEmailModel(resultSet.getString("FILIAL"),
                resultSet.getString("ENDERECO"),
                resultSet.getString("SERVIDOR"),
                resultSet.getString("PORTA"),
                resultSet.getString("USUARIO"),
                resultSet.getString("SENHA"),
                resultSet.getString("SSL"),
                resultSet.getString("METODO"));
    }
}
