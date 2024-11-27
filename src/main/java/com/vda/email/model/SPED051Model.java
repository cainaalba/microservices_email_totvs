package com.vda.email.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Entity
@Table(name = "SPED051", schema = "dbo", catalog = "Sped12")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SPED051Model {
    @Id
    @Column(name = "R_E_C_N_O_")
    private String RECNO;

    @Column(name = "STATUSMAIL")
    private String statusMail;

    @Column(name = "EMAIL")
    private String email;

    public void atualizaStatusMail(String[] para) {
        this.statusMail = "2";
        this.email = Arrays.toString(para)
                .replace("[", "")
                .replace("]", "");
    }
}
