package com.superfit.superfitapp.model;

public enum CategoriaDespesa {
    SALARIO("Salário"),
    ALUGUEL("Aluguel"),
    SERVICOS("Serviços"),
    MANUTENCAO("Manutenção"),
    MATERIAL("Material"),
    UTENSÍLIOS("Utensílios"),
    ENERGIA("Energia"),
    AGUA("Água"),
    INTERNET("Internet"),
    TELEFONE("Telefone"),
    LIMPEZA("Limpeza"),
    SEGURANCA("Segurança"),
    ALIMENTOS("Alimentos"),
    MARKETING("Marketing"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaDespesa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
