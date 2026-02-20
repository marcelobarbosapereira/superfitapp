package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.treino.ExercicioDTO;
import com.superfit.superfitapp.dto.treino.TreinoCreateDTO;
import com.superfit.superfitapp.dto.treino.TreinoResponseDTO;
import com.superfit.superfitapp.dto.treino.TreinoUpdateDTO;
import com.superfit.superfitapp.model.Aluno;
import com.superfit.superfitapp.model.Exercicio;
import com.superfit.superfitapp.model.Professor;
import com.superfit.superfitapp.model.Treino;
import com.superfit.superfitapp.repository.AlunoRepository;
import com.superfit.superfitapp.repository.ProfessorRepository;
import com.superfit.superfitapp.repository.TreinoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de gerenciamento de Treinos e Exercícios.
 * Gerencia operações CRUD, validações de segurança e relacionamento aluno-professor.
 */
@Service("treinoService")
public class TreinoServiceImpl implements TreinoService {

    private final TreinoRepository treinoRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;

    public TreinoServiceImpl(TreinoRepository treinoRepository, ProfessorRepository professorRepository, AlunoRepository alunoRepository) {
        this.treinoRepository = treinoRepository;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
    }

    /**
     * Verifica se o treino pertence ao professor autenticado.
     * Extrai o email do SecurityContext e valida no repositório.
     * 
     * @param treinoId ID do treino a ser verificado
     * @return true se o treino foi criado pelo professor autenticado
     */
    @Override
    public boolean isTreinoDoProfessor(Long treinoId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return treinoRepository.existsByIdAndProfessorUserEmail(treinoId, email);
    }

    /**
     * Verifica se o treino está vinculado ao aluno autenticado.
     * Extrai o email do SecurityContext e verifica se o aluno possui este treino.
     * 
     * @param treinoId ID do treino a ser verificado
     * @return true se o treino está atribuído ao aluno autenticado
     */
    @Override
    public boolean isTreinoDoAluno(Long treinoId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return treinoRepository.existsByIdAndAlunoUserEmail(treinoId, email);
    }

    /**
     * Cria um novo treino com lista de exercícios.
     * Busca o professor autenticado e o aluno pelo ID.
     * Valida se o aluno pertence ao professor antes de criar.
     * Utiliza o método addExercicio do Treino para manter a relação bidirecional.
     * 
     * @param dto Dados do treino (nome, alunoId, exercícios)
     * @return DTO com o treino criado e todos os exercícios
     * @throws RuntimeException se professor não encontrado, aluno não encontrado ou aluno não pertence ao professor
     */
    @Override
    @Transactional
    public TreinoResponseDTO criar(TreinoCreateDTO dto) {
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

        Treino treino = new Treino();
        treino.setNome(dto.getNome());
        treino.setTipo(dto.getTipo());
        treino.setDataInicio(dto.getDataInicio() != null ? dto.getDataInicio() : LocalDate.now());
        treino.setProfessor(professor);
        treino.setAluno(aluno);

        if (dto.getExercicios() != null) {
            for (ExercicioDTO exercicioDTO : dto.getExercicios()) {
                Exercicio exercicio = new Exercicio();
                exercicio.setNome(exercicioDTO.getNome());
                exercicio.setRepeticoes(exercicioDTO.getRepeticoes());
                exercicio.setCarga(exercicioDTO.getCarga());
                exercicio.setGrupoMuscular(exercicioDTO.getGrupoMuscular());
                exercicio.setDescansoIndicado(exercicioDTO.getDescansoIndicado());
                treino.addExercicio(exercicio);
            }
        }

        treino = treinoRepository.save(treino);
        return toResponseDTO(treino);
    }

    /**
     * Lista treinos de acordo com o tipo de usuário autenticado.
     * Professor: retorna treinos que ele criou.
     * Aluno: retorna treinos atribuídos a ele.
     * Busca o email do SecurityContext e identifica o tipo de usuário.
     * 
     * @return Lista de DTOs com os treinos do usuário
     */
    @Override
    public List<TreinoResponseDTO> listarTodos() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Buscar professor pelo email
        Professor professor = professorRepository.findByEmail(email).orElse(null);

        if (professor != null) {
            // Se for professor, retorna seus treinos
            return treinoRepository.findByProfessorId(professor.getId())
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        }

        // Se for aluno, retorna seus treinos
        Aluno aluno = alunoRepository.findByEmail(email).orElse(null);
        if (aluno != null) {
            return treinoRepository.findByAlunoId(aluno.getId())
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    /**
     * Busca um treino específico por ID com todos os seus exercícios.
     * Valida que o ID não seja nulo e lança exceção se não encontrar.
     * 
     * @param id ID do treino
     * @return DTO com o treino e lista de exercícios
     * @throws RuntimeException se o treino não for encontrado
     */
    @Override
    public TreinoResponseDTO buscarPorId(Long id) {
        Treino treino = treinoRepository.findById(
                Objects.requireNonNull(id, "id")
        ).orElseThrow(() -> new RuntimeException("Treino não encontrado"));

        return toResponseDTO(treino);
    }

    /**
     * Atualiza um treino existente substituindo seus exercícios.
     * Remove todos os exercícios antigos com clear() e adiciona os novos.
     * Mantém a relação bidirecional usando addExercicio.
     * 
     * @param id ID do treino a ser atualizado
     * @param dto Novos dados (nome, lista de exercícios)
     * @return DTO com os dados atualizados
     * @throws RuntimeException se o treino não for encontrado
     */
    @Override
    @Transactional
    public TreinoResponseDTO atualizar(Long id, TreinoUpdateDTO dto) {
        Treino treino = treinoRepository.findById(
                Objects.requireNonNull(id, "id")
        ).orElseThrow(() -> new RuntimeException("Treino não encontrado"));

        treino.setNome(dto.getNome());
        treino.setTipo(dto.getTipo());

        // Remover exercícios antigos
        treino.getExercicios().clear();

        // Adicionar novos exercícios
        if (dto.getExercicios() != null) {
            for (ExercicioDTO exercicioDTO : dto.getExercicios()) {
                Exercicio exercicio = new Exercicio();
                exercicio.setNome(exercicioDTO.getNome());
                exercicio.setRepeticoes(exercicioDTO.getRepeticoes());
                exercicio.setCarga(exercicioDTO.getCarga());
                exercicio.setGrupoMuscular(exercicioDTO.getGrupoMuscular());
                exercicio.setDescansoIndicado(exercicioDTO.getDescansoIndicado());
                treino.addExercicio(exercicio);
            }
        }

        treino = treinoRepository.save(treino);
        return toResponseDTO(treino);
    }

    /**
     * Remove um treino do sistema.
     * Devido ao cascade configurado na entidade, remove também todos os exercícios associados.
     * 
     * @param id ID do treino a ser removido
     * @throws RuntimeException se o treino não for encontrado
     */
    @Override
    public void remover(Long id) {
        if (!treinoRepository.existsById(Objects.requireNonNull(id, "id"))) {
            throw new RuntimeException("Treino não encontrado");
        }
        treinoRepository.deleteById(Objects.requireNonNull(id, "id"));
    }

    /**
     * Converte a entidade Treino em DTO de resposta.
     * Mapeia a lista de exercícios do treino para ExercicioDTO.
     * Inclui dados do professor e aluno vinculados.
     * 
     * @param treino Entidade Treino a ser convertida
     * @return DTO com treino, exercícios, professor e aluno
     */
    private TreinoResponseDTO toResponseDTO(Treino treino) {
        List<ExercicioDTO> exercicios = treino.getExercicios().stream()
                .map(e -> new ExercicioDTO(
                        e.getId(),
                        e.getNome(),
                        e.getRepeticoes(),
                        e.getCarga(),
                        e.getGrupoMuscular(),
                        e.getDescansoIndicado()
                ))
                .collect(Collectors.toList());

        return new TreinoResponseDTO(
                treino.getId(),
                treino.getNome(),
                treino.getTipo(),
                treino.getDataInicio(),
                treino.getProfessor().getId(),
                treino.getProfessor().getNome(),
                treino.getAluno().getId(),
                treino.getAluno().getNome(),
                exercicios
        );
    }
}
