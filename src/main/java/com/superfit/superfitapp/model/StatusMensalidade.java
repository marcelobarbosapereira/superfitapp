package com.superfit.superfitapp.model;

public enum StatusMensalidade {
    PAGA("Paga"),
    PENDENTE("Pendente");

    private final String descricao;

    StatusMensalidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
