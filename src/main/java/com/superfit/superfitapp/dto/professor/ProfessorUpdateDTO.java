package com.superfit.superfitapp.dto.professor;

public class ProfessorUpdateDTO {

    private String nome;
    private String telefone;
    private String crefi;
    private String senha;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCrefi() {
        return crefi;
    }

    public void setCrefi(String crefi) {
        this.crefi = crefi;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
