package com.superfit.superfitapp;

import com.superfit.superfitapp.model.Aluno;
import com.superfit.superfitapp.model.CategoriaDespesa;
import com.superfit.superfitapp.model.Despesa;
import com.superfit.superfitapp.model.Mensalidade;
import com.superfit.superfitapp.model.StatusMensalidade;
import com.superfit.superfitapp.repository.AlunoRepository;
import com.superfit.superfitapp.repository.DespesaRepository;
import com.superfit.superfitapp.repository.MensalidadeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private MensalidadeRepository mensalidadeRepository;

    @Autowired
    private DespesaRepository despesaRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void relatorioFinanceiroMensalDeveRetornarTotaisCorretos() throws Exception {
        // Arrange: cria aluno, mensalidade paga e despesa paga no mês alvo.
        Aluno aluno = new Aluno("João Teste", "joao.teste@exemplo.com", "11999999999");
        aluno = alunoRepository.save(aluno);

        Mensalidade paga = new Mensalidade();
        paga.setAluno(aluno);
        paga.setValor(200.00);
        paga.setStatus(StatusMensalidade.PAGA);
        paga.setDataVencimento(LocalDate.of(2026, 2, 10));
        paga.setDataPagamento(LocalDate.of(2026, 2, 5));
        paga.setMesReferencia("Fevereiro");
        paga.setAnoReferencia(2026);
        paga.setDataCriacao(LocalDate.of(2026, 2, 1));
        mensalidadeRepository.save(paga);

        Despesa despesa = new Despesa();
        despesa.setDescricao("Internet");
        despesa.setValor(50.00);
        despesa.setCategoria(CategoriaDespesa.INTERNET);
        despesa.setDataDespesa(LocalDate.of(2026, 2, 3));
        despesa.setPaga(true);
        despesa.setDataPagamento(LocalDate.of(2026, 2, 3));
        despesa.setDataCriacao(LocalDate.of(2026, 2, 1));
        despesaRepository.save(despesa);

        // Act & Assert: chama o endpoint e valida os totais consolidados.
        mockMvc.perform(get("/api/relatorios/financeiro/2026/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receitas", closeTo(200.0, 0.001)))
                .andExpect(jsonPath("$.despesasTotal", closeTo(50.0, 0.001)))
                .andExpect(jsonPath("$.despesasPagas", closeTo(50.0, 0.001)))
                .andExpect(jsonPath("$.lucro", closeTo(150.0, 0.001)))
                .andExpect(jsonPath("$.margem", greaterThanOrEqualTo(0.0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void relatorioInadimplenciaDeveListarPendentes() throws Exception {
        // Arrange: cria aluno e mensalidade pendente.
        Aluno aluno = new Aluno("Maria Teste", "maria.teste@exemplo.com", "11888888888");
        aluno = alunoRepository.save(aluno);

        Mensalidade pendente = new Mensalidade();
        pendente.setAluno(aluno);
        pendente.setValor(100.00);
        pendente.setStatus(StatusMensalidade.PENDENTE);
        pendente.setDataVencimento(LocalDate.of(2026, 1, 10));
        pendente.setMesReferencia("Janeiro");
        pendente.setAnoReferencia(2026);
        pendente.setDataCriacao(LocalDate.of(2026, 1, 1));
        mensalidadeRepository.save(pendente);

        // Act & Assert: valida que o relatório retorna pendências e totais.
        mockMvc.perform(get("/api/relatorios/inadimplencia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeMensalidades", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalInadimplencia", greaterThanOrEqualTo(100.0)))
                .andExpect(jsonPath("$.mensalidades").isArray());
    }
}
