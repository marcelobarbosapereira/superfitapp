package com.superfit.superfitapp.dto.medidas;

import java.util.List;

public class HistoricoEvolucaoDTO {

    private Long alunoId;
    private String alunoNome;
    private Double altura;
    private String sexo;
    private List<MedidasResponseDTO> historico;
    private EvolucaoResumoDTO evolucao;

    public HistoricoEvolucaoDTO() {
    }

    public HistoricoEvolucaoDTO(Long alunoId, String alunoNome, Double altura, String sexo, List<MedidasResponseDTO> historico, EvolucaoResumoDTO evolucao) {
        this.alunoId = alunoId;
        this.alunoNome = alunoNome;
        this.altura = altura;
        this.sexo = sexo;
        this.historico = historico;
        this.evolucao = evolucao;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public void setAlunoNome(String alunoNome) {
        this.alunoNome = alunoNome;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public List<MedidasResponseDTO> getHistorico() {
        return historico;
    }

    public void setHistorico(List<MedidasResponseDTO> historico) {
        this.historico = historico;
    }

    public EvolucaoResumoDTO getEvolucao() {
        return evolucao;
    }

    public void setEvolucao(EvolucaoResumoDTO evolucao) {
        this.evolucao = evolucao;
    }

    public static class EvolucaoResumoDTO {
        private Double pesoInicial;
        private Double pesoAtual;
        private Double diferencaPeso;
        private Double imcInicial;
        private Double imcAtual;
        private Double diferencaImc;

        public EvolucaoResumoDTO() {
        }

        public EvolucaoResumoDTO(Double pesoInicial, Double pesoAtual, Double diferencaPeso, Double imcInicial, Double imcAtual, Double diferencaImc) {
            this.pesoInicial = pesoInicial;
            this.pesoAtual = pesoAtual;
            this.diferencaPeso = diferencaPeso;
            this.imcInicial = imcInicial;
            this.imcAtual = imcAtual;
            this.diferencaImc = diferencaImc;
        }

        public Double getPesoInicial() {
            return pesoInicial;
        }

        public void setPesoInicial(Double pesoInicial) {
            this.pesoInicial = pesoInicial;
        }

        public Double getPesoAtual() {
            return pesoAtual;
        }

        public void setPesoAtual(Double pesoAtual) {
            this.pesoAtual = pesoAtual;
        }

        public Double getDiferencaPeso() {
            return diferencaPeso;
        }

        public void setDiferencaPeso(Double diferencaPeso) {
            this.diferencaPeso = diferencaPeso;
        }

        public Double getImcInicial() {
            return imcInicial;
        }

        public void setImcInicial(Double imcInicial) {
            this.imcInicial = imcInicial;
        }

        public Double getImcAtual() {
            return imcAtual;
        }

        public void setImcAtual(Double imcAtual) {
            this.imcAtual = imcAtual;
        }

        public Double getDiferencaImc() {
            return diferencaImc;
        }

        public void setDiferencaImc(Double diferencaImc) {
            this.diferencaImc = diferencaImc;
        }
    }
}
