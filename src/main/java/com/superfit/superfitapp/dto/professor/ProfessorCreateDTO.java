package com.superfit.superfitapp.dto.professor;

public class ProfessorCreateDTO {

    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String crefi;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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
}
