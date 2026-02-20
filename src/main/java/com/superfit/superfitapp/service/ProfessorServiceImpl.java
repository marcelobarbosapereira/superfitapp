package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.professor.ProfessorCreateDTO;
import com.superfit.superfitapp.dto.professor.ProfessorResponseDTO;
import com.superfit.superfitapp.dto.professor.ProfessorUpdateDTO;
import com.superfit.superfitapp.dto.aluno.AlunoResponseDTO;
import com.superfit.superfitapp.model.Professor;
import com.superfit.superfitapp.model.Aluno;
import com.superfit.superfitapp.model.Role;
import com.superfit.superfitapp.model.User;
import com.superfit.superfitapp.repository.ProfessorRepository;
import com.superfit.superfitapp.repository.AlunoRepository;
import com.superfit.superfitapp.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de gerenciamento de Professores.
 * Gerencia operações CRUD, autenticação e conversão de DTOs.
 */
@Service("professorService")
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UserRepository userRepository;
    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfessorServiceImpl(ProfessorRepository professorRepository, UserRepository userRepository, AlunoRepository alunoRepository, PasswordEncoder passwordEncoder) {
        this.professorRepository = professorRepository;
        this.userRepository = userRepository;
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* =======================
       MÉTODOS DE SEGURANÇA
       ======================= */

    /**
     * Verifica se o professor pertence ao usuário autenticado.
     * Extrai o email do SecurityContext e valida no repositório.
     * 
     * @param professorId ID do professor a ser verificado
     * @return true se existe um professor com o ID fornecido vinculado ao email do usuário autenticado
     */
    @Override
    public boolean isProfessorDoToken(Long professorId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return professorRepository.existsByIdAndUserEmail(professorId, email);
    }

    /* =======================
       MÉTODOS DE NEGÓCIO
       ======================= */

    /**
     * Cria um novo professor no sistema.
     * Instancia a entidade Professor, define os atributos e persiste no banco.
     * 
     * @param dto Dados do professor (nome, email, telefone, CREFI)
     * @return DTO com os dados do professor criado incluindo o ID gerado
     */
    @Override
    public ProfessorResponseDTO criar(ProfessorCreateDTO dto) {
        // Cria um usuário com senha padrão "123456" para o professor
        User user = new User(
            dto.getEmail(),
            passwordEncoder.encode("123456"),
            Role.ROLE_PROFESSOR
        );
        user = userRepository.save(user);

        Professor professor = new Professor();
        professor.setNome(dto.getNome());
        professor.setEmail(dto.getEmail());
        professor.setTelefone(dto.getTelefone());
        professor.setCrefi(dto.getCrefi());
        professor.setUser(user);

        professor = professorRepository.save(professor);
        return toResponseDTO(professor);
    }

    /**
     * Lista todos os professores cadastrados.
     * Utiliza stream para converter cada entidade em DTO.
     * 
     * @return Lista de DTOs com todos os professores
     */
    @Override
    public List<ProfessorResponseDTO> listarTodos() {
        return professorRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um professor específico por ID.
     * Valida que o ID não seja nulo e lança exceção se não encontrar.
     * 
     * @param id ID do professor
     * @return DTO com os dados do professor
     * @throws RuntimeException se o professor não for encontrado
     */
    @Override
    public ProfessorResponseDTO buscarPorId(Long id) {
        Professor professor = professorRepository.findById(
                Objects.requireNonNull(id, "id")
            )
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        return toResponseDTO(professor);
    }

    /**
     * Atualiza os dados de um professor existente.
     * Busca o professor por ID, atualiza os campos permitidos (nome, telefone, CREFI) e persiste.
     * 
     * @param id ID do professor a ser atualizado
     * @param dto Novos dados (nome, telefone, CREFI)
     * @return DTO com os dados atualizados
     * @throws RuntimeException se o professor não for encontrado
     */
    @Override
    public ProfessorResponseDTO atualizar(Long id, ProfessorUpdateDTO dto) {
        Professor professor = professorRepository.findById(
                Objects.requireNonNull(id, "id")
            )
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        professor.setNome(dto.getNome());
        professor.setTelefone(dto.getTelefone());
        professor.setCrefi(dto.getCrefi());

        // Atualiza a senha do usuário vinculado se fornecida
        if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty()) {
            User user = professor.getUser();
            if (user != null) {
                user.setPassword(passwordEncoder.encode(dto.getSenha()));
                userRepository.save(user);
            }
        }

        professor = professorRepository.save(professor);
        return toResponseDTO(professor);
    }

    /**
     * Remove um professor do sistema.
     * Verifica existência antes de deletar para lançar mensagem de erro apropriada.
     * 
     * @param id ID do professor a ser removido
     * @throws RuntimeException se o professor não for encontrado
     */
    @Override
    public void remover(Long id) {
        if (!professorRepository.existsById(Objects.requireNonNull(id, "id"))) {
            throw new RuntimeException("Professor não encontrado");
        }
        professorRepository.deleteById(Objects.requireNonNull(id, "id"));
    }*
     * Lista todos os alunos vinculados ao professor autenticado.
     * Busca o professor pelo email no token JWT e retorna seus alunos.
     * 
     * @return Lista de DTOs com os alunos do professor
     */
    @Override
    public List<AlunoResponseDTO> listarMeusAlunos() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Professor professor = professorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        List<Aluno> alunos = alunoRepository.findByProfessorId(professor.getId());

        return alunos.stream()
                .map(this::toAlunoResponseDTO)
                .collect(Collectors.toList());
    }

    /* =======================
       MÉTODO UTILITÁRIO (DTO)
       ======================= */

    /**
     * Converte a entidade Professor em DTO de resposta.
     * Prioriza o email do User vinculado, caso contrário usa o email direto do Professor.
     * 
     * @param professor Entidade Professor a ser convertida
     * @return DTO com os dados do professor formatados para resposta
     */
    private ProfessorResponseDTO toResponseDTO(Professor professor) {
        String email = professor.getUser() != null
                ? professor.getUser().getEmail()
                : professor.getEmail();

        return new ProfessorResponseDTO(
                professor.getId(),
                professor.getNome(),
                email,
                professor.getTelefone(),
                professor.getCrefi()
        );
    }

    /**
     * Converte a entidade Aluno em DTO de resposta.
     * 
     * @param aluno Entidade Aluno a ser convertida
     * @return DTO com os dados do aluno formatados para resposta
     */
    private AlunoResponseDTO toAlunoResponseDTO(Aluno aluno) {
        String email = aluno.getUser() != null
                ? aluno.getUser().getEmail()
                : aluno.getEmail();

        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                email,
                aluno.getTelefone(),
                aluno.getDataNascimento(),
                aluno.getAtivo(),
                aluno.getProfessor() != null ? aluno.getProfessor().getId() : null
                professor.getTelefone(),
                professor.getCrefi()
        );
    }
}

