package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.aluno.AlunoCreateDTO;
import com.superfit.superfitapp.dto.aluno.AlunoResponseDTO;
import com.superfit.superfitapp.dto.aluno.AlunoUpdateDTO;
import com.superfit.superfitapp.model.Aluno;
import com.superfit.superfitapp.model.Professor;
import com.superfit.superfitapp.model.Role;
import com.superfit.superfitapp.model.User;
import com.superfit.superfitapp.repository.AlunoRepository;
import com.superfit.superfitapp.repository.ProfessorRepository;
import com.superfit.superfitapp.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de gerenciamento de Alunos.
 * Gerencia operações CRUD, validações de segurança e conversão de DTOs.
 */
@Service("alunoService")
public class AlunoServiceImpl implements AlunoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoServiceImpl(AlunoRepository alunoRepository, ProfessorRepository professorRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* =======================
       SEGURANÇA
       ======================= */

    /**
     * Verifica se o aluno pertence ao usuário autenticado.
     * Extrai o email do SecurityContext e verifica no repositório.
     * 
     * @param alunoId ID do aluno a ser verificado
     * @return true se existe um aluno com o ID fornecido vinculado ao email do usuário autenticado
     */
    @Override
    public boolean isAlunoDoToken(Long alunoId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return alunoRepository.existsByIdAndUserEmail(alunoId, email);
    }

    /**
     * Verifica se o aluno está sob supervisão do professor autenticado.
     * Extrai o email do professor do SecurityContext e valida a relação aluno-professor.
     * 
     * @param alunoId ID do aluno a ser verificado
     * @return true se o aluno pertence ao professor autenticado
     */
    @Override
    public boolean isAlunoDoProfessor(Long alunoId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return alunoRepository.existsByIdAndProfessorUserEmail(alunoId, email);
    }

    /* =======================
       NEGÓCIO
       ======================= */

    /**
     * Cria um novo aluno no sistema.
     * Busca o professor pelo ID fornecido, cria a entidade Aluno, define como ativo e persiste.
     * 
     * @param dto Dados do aluno (nome, email, telefone, professorId)
     * @return DTO com os dados do aluno criado incluindo o ID gerado
     * @throws RuntimeException se o professor não for encontrado
     */
    @Override
    public AlunoResponseDTO criar(AlunoCreateDTO dto) {
        Professor professor = professorRepository.findById(
                Objects.requireNonNull(dto.getProfessorId(), "professorId")
            )
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        // Cria um usuário com senha padrão "123456" para o aluno
        User user = new User(
            dto.getEmail(),
            passwordEncoder.encode("123456"),
            Role.ROLE_ALUNO
        );
        user = userRepository.save(user);

        Aluno aluno = new Aluno();
        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setTelefone(dto.getTelefone());
        aluno.setProfessor(professor);
        aluno.setAtivo(true);
        aluno.setUser(user);

        aluno = alunoRepository.save(aluno);
        return toResponseDTO(aluno);
    }

    /**
     * Lista todos os alunos cadastrados.
     * Utiliza stream para converter cada entidade em DTO.
     * 
     * @return Lista de DTOs com todos os alunos
     */
    @Override
    public List<AlunoResponseDTO> listar() {
        return alunoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um aluno específico por ID.
     * Valida que o ID não seja nulo e lança exceção se não encontrar.
     * 
     * @param id ID do aluno
     * @return DTO com os dados do aluno
     * @throws RuntimeException se o aluno não for encontrado
     */
    @Override
    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = alunoRepository.findById(
                Objects.requireNonNull(id, "id")
            )
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        return toResponseDTO(aluno);
    }

    /**
     * Atualiza os dados de um aluno existente.
     * Busca o aluno por ID, atualiza os campos permitidos (nome, telefone, ativo) e persiste.
     * 
     * @param id ID do aluno a ser atualizado
     * @param dto Novos dados (nome, telefone, ativo)
     * @return DTO com os dados atualizados
     * @throws RuntimeException se o aluno não for encontrado
     */
    @Override
    public AlunoResponseDTO atualizar(Long id, AlunoUpdateDTO dto) {
        Aluno aluno = alunoRepository.findById(
                Objects.requireNonNull(id, "id")
            )
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        aluno.setNome(dto.getNome());
        aluno.setTelefone(dto.getTelefone());
        aluno.setAtivo(dto.getAtivo());

        // Atualiza a senha do usuário vinculado se fornecida
        if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty()) {
            User user = aluno.getUser();
            if (user != null) {
                user.setPassword(passwordEncoder.encode(dto.getSenha()));
                userRepository.save(user);
            }
        }

        aluno = alunoRepository.save(aluno);
        return toResponseDTO(aluno);
    }

    /**
     * Remove um aluno do sistema.
     * Valida que o ID não seja nulo antes de deletar.
     * 
     * @param id ID do aluno a ser removido
     */
    @Override
    public void remover(Long id) {
        alunoRepository.deleteById(Objects.requireNonNull(id, "id"));
    }

    /* =======================
       DTO
       ======================= */

    /**
     * Converte a entidade Aluno em DTO de resposta.
     * Prioriza o email do User vinculado, caso contrário usa o email direto do Aluno.
     * 
     * @param aluno Entidade Aluno a ser convertida
     * @return DTO com os dados do aluno formatados para resposta
     */
    private AlunoResponseDTO toResponseDTO(Aluno aluno) {
        String email = aluno.getUser() != null
            ? aluno.getUser().getEmail()
            : aluno.getEmail();

        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
            email,
                aluno.getTelefone(),
                aluno.getAtivo(),
                aluno.getProfessor().getId(),
                aluno.getProfessor().getNome()
        );
    }
}
