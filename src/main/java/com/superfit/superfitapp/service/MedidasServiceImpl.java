package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.medidas.HistoricoEvolucaoDTO;
import com.superfit.superfitapp.dto.medidas.MedidasCreateDTO;
import com.superfit.superfitapp.dto.medidas.MedidasResponseDTO;
import com.superfit.superfitapp.dto.medidas.MedidasUpdateDTO;
import com.superfit.superfitapp.model.Aluno;
import com.superfit.superfitapp.model.Medidas;
import com.superfit.superfitapp.model.Professor;
import com.superfit.superfitapp.repository.AlunoRepository;
import com.superfit.superfitapp.repository.MedidasRepository;
import com.superfit.superfitapp.repository.ProfessorRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de gerenciamento de Medidas corporais.
 * Gerencia operações de CRUD, cálculo de IMC e histórico de evolução física dos alunos.
 */
@Service("medidasService")
public class MedidasServiceImpl implements MedidasService {

    private final MedidasRepository medidasRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public MedidasServiceImpl(MedidasRepository medidasRepository, AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.medidasRepository = medidasRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    /**
     * Verifica se a medida pertence ao aluno autenticado.
     * Extrai o email do SecurityContext e valida no repositório.
     * 
     * @param medidasId ID da medida a ser verificada
     * @return true se a medida pertence ao aluno autenticado
     */
    @Override
    public boolean isMedidaDoAluno(Long medidasId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return medidasRepository.existsByIdAndAlunoUserEmail(medidasId, email);
    }

    /**
     * Verifica se a medida pertence a um aluno do professor autenticado.
     * Verifica a relação aluno.professor.user.email.
     * 
     * @param medidasId ID da medida a ser verificada
     * @return true se a medida é de um aluno supervisionado pelo professor
     */
    @Override
    public boolean isMedidaDoProfessor(Long medidasId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return medidasRepository.existsByIdAndAlunoProfessorUserEmail(medidasId, email);
    }

    /**
     * Cria uma nova medição para um aluno.
     * Busca o professor autenticado e o aluno pelo ID.
     * Valida se o aluno pertence ao professor.
     * Calcula automaticamente o IMC usando o método calcularImc() da entidade Medidas.
     * 
     * @param dto Dados da medição (data, peso, peito, cintura, quadril, alunoId)
     * @return DTO com a medida criada incluindo IMC calculado
     * @throws RuntimeException se professor não encontrado, aluno não encontrado ou aluno não pertence ao professor
     */
    @Override
    @Transactional
    public MedidasResponseDTO criar(MedidasCreateDTO dto) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Professor professor = professorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Aluno aluno = alunoRepository.findById(
                Objects.requireNonNull(dto.getAlunoId(), "alunoId")
        ).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        // Verificar se o aluno pertence ao professor
        if (!aluno.getProfessor().getId().equals(professor.getId())) {
            throw new RuntimeException("Este aluno não pertence a você");
        }

        Medidas medidas = new Medidas();
        medidas.setData(dto.getData());
        medidas.setPeso(dto.getPeso());
        medidas.setPeito(dto.getPeito());
        medidas.setCintura(dto.getCintura());
        medidas.setQuadril(dto.getQuadril());
        medidas.setAluno(aluno);

        // Calcular IMC automaticamente
        medidas.calcularImc();

        medidas = medidasRepository.save(medidas);
        return toResponseDTO(medidas);
    }

    /**
     * Lista medidas de acordo com o tipo de usuário autenticado.
     * Professor: retorna medidas de todos os alunos sob sua supervisão.
     * Aluno: retorna apenas suas próprias medidas em ordem decrescente de data.
     * 
     * @return Lista de DTOs com as medidas do usuário
     */
    @Override
    public List<MedidasResponseDTO> listarTodas() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Buscar professor pelo email
        Professor professor = professorRepository.findByEmail(email).orElse(null);

        if (professor != null) {
            // Se for professor, retorna medidas de todos os seus alunos
            return medidasRepository.findByProfessorEmail(email)
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        }

        // Se for aluno, retorna apenas suas medidas
        Aluno aluno = alunoRepository.findByEmail(email).orElse(null);
        if (aluno != null) {
            return medidasRepository.findByAlunoIdOrderByDataDesc(aluno.getId())
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    /**
     * Lista todas as medidas de um aluno específico.
     * Ordena em ordem decrescente de data (mais recente primeiro).
     * 
     * @param alunoId ID do aluno
     * @return Lista de DTOs com as medidas do aluno
     */
    @Override
    public List<MedidasResponseDTO> listarPorAluno(Long alunoId) {
        return medidasRepository.findByAlunoIdOrderByDataDesc(
                Objects.requireNonNull(alunoId, "alunoId")
        )
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtém o histórico completo de evolução física de um aluno.
     * Busca todas as medidas em ordem crescente de data.
     * Calcula evolução comparando primeira e última medição (peso e IMC).
     * Arredonda diferenças para 1 casa decimal.
     * 
     * @param alunoId ID do aluno
     * @return DTO com histórico de medidas, dados do aluno e resumo de evolução
     * @throws RuntimeException se o aluno não for encontrado
     */
    @Override
    public HistoricoEvolucaoDTO obterHistoricoEvolucao(Long alunoId) {
        Aluno aluno = alunoRepository.findById(
                Objects.requireNonNull(alunoId, "alunoId")
        ).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        List<Medidas> medidasList = medidasRepository.findByAlunoIdOrderByDataAsc(alunoId);

        List<MedidasResponseDTO> historico = medidasList.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        // Calcular evolução
        HistoricoEvolucaoDTO.EvolucaoResumoDTO evolucao = null;
        if (!medidasList.isEmpty()) {
            Medidas primeira = medidasList.get(0);
            Medidas ultima = medidasList.get(medidasList.size() - 1);

            double pesoInicial = primeira.getPeso();
            double pesoAtual = ultima.getPeso();
            double diferencaPeso = pesoAtual - pesoInicial;

            double imcInicial = primeira.getImc() != null ? primeira.getImc() : 0.0;
            double imcAtual = ultima.getImc() != null ? ultima.getImc() : 0.0;
            double diferencaImc = imcAtual - imcInicial;

            evolucao = new HistoricoEvolucaoDTO.EvolucaoResumoDTO(
                    pesoInicial,
                    pesoAtual,
                    Math.round(diferencaPeso * 10.0) / 10.0,
                    imcInicial,
                    imcAtual,
                    Math.round(diferencaImc * 10.0) / 10.0
            );
        }

        return new HistoricoEvolucaoDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getAltura(),
                aluno.getSexo(),
                historico,
                evolucao
        );
    }

    /**
     * Busca uma medida específica por ID.
     * Valida que o ID não seja nulo e lança exceção se não encontrar.
     * 
     * @param id ID da medida
     * @return DTO com os dados da medida incluindo IMC
     * @throws RuntimeException se a medida não for encontrada
     */
    @Override
    public MedidasResponseDTO buscarPorId(Long id) {
        Medidas medidas = medidasRepository.findById(
                Objects.requireNonNull(id, "id")
        ).orElseThrow(() -> new RuntimeException("Medidas não encontradas"));

        return toResponseDTO(medidas);
    }

    /**
     * Atualiza uma medição existente.
     * Atualiza todos os campos e recalcula automaticamente o IMC.
     * 
     * @param id ID da medida a ser atualizada
     * @param dto Novos dados (data, peso, peito, cintura, quadril)
     * @return DTO com os dados atualizados incluindo IMC recalculado
     * @throws RuntimeException se a medida não for encontrada
     */
    @Override
    @Transactional
    public MedidasResponseDTO atualizar(Long id, MedidasUpdateDTO dto) {
        Medidas medidas = medidasRepository.findById(
                Objects.requireNonNull(id, "id")
        ).orElseThrow(() -> new RuntimeException("Medidas não encontradas"));

        medidas.setData(dto.getData());
        medidas.setPeso(dto.getPeso());
        medidas.setPeito(dto.getPeito());
        medidas.setCintura(dto.getCintura());
        medidas.setQuadril(dto.getQuadril());

        // Recalcular IMC
        medidas.calcularImc();

        medidas = medidasRepository.save(medidas);
        return toResponseDTO(medidas);
    }

    /**
     * Remove uma medição do sistema.
     * Verifica existência antes de deletar.
     * 
     * @param id ID da medida a ser removida
     * @throws RuntimeException se a medida não for encontrada
     */
    @Override
    public void remover(Long id) {
        if (!medidasRepository.existsById(Objects.requireNonNull(id, "id"))) {
            throw new RuntimeException("Medidas não encontradas");
        }
        medidasRepository.deleteById(Objects.requireNonNull(id, "id"));
    }

    /**
     * Converte a entidade Medidas em DTO de resposta.
     * Inclui todos os dados da medição e informações do aluno vinculado.
     * 
     * @param medidas Entidade Medidas a ser convertida
     * @return DTO com medidas e dados do aluno
     */
    private MedidasResponseDTO toResponseDTO(Medidas medidas) {
        return new MedidasResponseDTO(
                medidas.getId(),
                medidas.getData(),
                medidas.getPeso(),
                medidas.getImc(),
                medidas.getPeito(),
                medidas.getCintura(),
                medidas.getQuadril(),
                medidas.getAluno().getId(),
                medidas.getAluno().getNome()
        );
    }
}
