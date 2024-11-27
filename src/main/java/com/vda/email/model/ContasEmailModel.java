package com.vda.email.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "WF7010")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("D_E_L_E_T_ = ''")
public class ContasEmailModel {
    @Id
    @Column(name = "R_E_C_N_O_")
    int recno;

    @Column(name = "WF7_FILIAL")
    String filial;

    @Column(name = "WF7_ENDERE")
    String endereco;

    @Column(name = "WF7_SMTPSR")
    String servidor;

    @Column(name = "WF7_SMTPPR")
    String porta;

    @Column(name = "WF7_AUTUSU")
    String usuario;

    @Column(name = "WF7_AUTSEN")
    String senha;

    @Column(name = "WF7_SSL")
    String ssl;

    @Column(name = "WF7_SMTPSE")
    String metodo;

    public String getFilial() {
        return filial.trim();
    }

    public String getEndereco() {
        return endereco.trim();
    }

    public String getServidor() {
        return servidor.trim();
    }

    public String getPorta() {
        return porta.trim();
    }

    public String getUsuario() {
        return usuario.trim();
    }

    public String getSenha() {
        return senha.trim();
    }

    public String getSsl() {
        return ssl.trim();
    }

    public String getMetodo() {
        return metodo.trim();
    }

    @Override
    public String toString() {
        return "ContasEmailModel{" +
                "recno=" + recno +
                ", filial='" + getFilial() + '\'' +
                ", endereco='" + getEndereco() + '\'' +
                ", servidor='" + getServidor() + '\'' +
                ", porta='" + getPorta() + '\'' +
                ", usuario='" + getUsuario() + '\'' +
                ", senha='" + getSenha() + '\'' +
                ", ssl='" + getSsl() + '\'' +
                ", metodo='" + getMetodo() + '\'' +
                '}';
    }
}
