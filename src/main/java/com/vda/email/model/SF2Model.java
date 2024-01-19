package com.vda.email.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SF2010")
public class SF2Model {
    @Id
    @Column(name = "R_E_C_N_O_")
    private String RECNO;

    @Column(name = "F2_FILIAL")
    private String filial;

    @Column(name = "F2_DOC")
    private String doc;

    @Column(name = "F2_SERIE")
    private String serie;

    @Column(name = "F2_ZENVRPS")
    private String envRps;

    @Column(name = "F2_ZDTMAIL")
    private String dtMail;

    public void atualizaStatusMail(String recno, String nomeUsuario, String[] para) {
        this.envRps = "S";
        this.dtMail = new Date() + "-" + nomeUsuario.trim() + " | " + Arrays.toString(para)
                .replace("[", "")
                .replace("]", "");
    }
}
