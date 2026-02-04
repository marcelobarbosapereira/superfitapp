package com.superfit.superfitapp;


import com.superfit.superfitapp.dto.despesa.DespesaCreateDTO;
import com.superfit.superfitapp.dto.despesa.DespesaResponseDTO;
import com.superfit.superfitapp.model.CategoriaDespesa;
import com.superfit.superfitapp.repository.DespesaRepository;
import com.superfit.superfitapp.service.DespesaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DespesaServiceImplTest {

    @Autowired
    private DespesaService despesaService;

    @Autowired
    private DespesaRepository despesaRepository;

    @Test
    void criarEDepoisListarDespesa() {
        // Arrange: cria um DTO com dados mínimos de uma despesa.
        DespesaCreateDTO dto = new DespesaCreateDTO();
        dto.setDescricao("Conta de energia");
        dto.setValor(320.50);
        dto.setCategoria(CategoriaDespesa.ENERGIA);
        dto.setDataDespesa(LocalDate.of(2026, 2, 1));
        dto.setPaga(false);

        // Act: cria a despesa e consulta a listagem completa.
        DespesaResponseDTO criada = despesaService.criar(dto);
        assertNotNull(criada.getId());
        assertEquals("Conta de energia", criada.getDescricao());
        assertEquals(CategoriaDespesa.ENERGIA, criada.getCategoria());

        // Assert: garante que a despesa criada aparece na listagem.
        List<DespesaResponseDTO> lista = despesaService.listar();
        assertTrue(lista.stream().anyMatch(d -> d.getId().equals(criada.getId())));
    }

    @Test
    void marcarDespesaComoPaga() {
        // Arrange: cria uma despesa inicialmente pendente.
        DespesaCreateDTO dto = new DespesaCreateDTO();
        dto.setDescricao("Aluguel");
        dto.setValor(1500.00);
        dto.setCategoria(CategoriaDespesa.ALUGUEL);
        dto.setDataDespesa(LocalDate.of(2026, 2, 3));
        dto.setPaga(false);

        // Act: cria a despesa e marca como paga via serviço.
        DespesaResponseDTO criada = despesaService.criar(dto);
        despesaService.marcarComoPaga(criada.getId());

        // Assert: valida que o status e a data de pagamento foram persistidos.
        assertTrue(despesaRepository.findById(criada.getId()).orElseThrow().getPaga());
        assertNotNull(despesaRepository.findById(criada.getId()).orElseThrow().getDataPagamento());
    }
}
