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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    public boolean isTreinoDoProfessor(Long treinoId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return treinoRepository.existsByIdAndProfessorUserEmail(treinoId, email);
    }

    @Override
    public boolean isTreinoDoAluno(Long treinoId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return treinoRepository.existsByIdAndAlunoUserEmail(treinoId, email);
    }

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

    @Override
    public TreinoResponseDTO buscarPorId(Long id) {
        Treino treino = treinoRepository.findById(
                Objects.requireNonNull(id, "id")
        ).orElseThrow(() -> new RuntimeException("Treino não encontrado"));

        return toResponseDTO(treino);
    }

    @Override
    @Transactional
    public TreinoResponseDTO atualizar(Long id, TreinoUpdateDTO dto) {
        Treino treino = treinoRepository.findById(
                Objects.requireNonNull(id, "id")
        ).orElseThrow(() -> new RuntimeException("Treino não encontrado"));

        treino.setNome(dto.getNome());

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

    @Override
    public void remover(Long id) {
        if (!treinoRepository.existsById(Objects.requireNonNull(id, "id"))) {
            throw new RuntimeException("Treino não encontrado");
        }
        treinoRepository.deleteById(Objects.requireNonNull(id, "id"));
    }

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
                treino.getProfessor().getId(),
                treino.getProfessor().getNome(),
                treino.getAluno().getId(),
                treino.getAluno().getNome(),
                exercicios
        );
    }
}
