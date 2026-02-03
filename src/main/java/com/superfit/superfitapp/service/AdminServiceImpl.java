package com.superfit.superfitapp.service;

import org.springframework.stereotype.Service;
import com.superfit.superfitapp.repository.UserRepository;
import com.superfit.superfitapp.repository.ProfessorRepository;
import com.superfit.superfitapp.repository.AlunoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.superfit.superfitapp.dto.admin.GestorCreateDTO;
import com.superfit.superfitapp.dto.admin.GestorResponseDTO;
import com.superfit.superfitapp.dto.admin.GestorUpdateDTO;
import com.superfit.superfitapp.dto.admin.ChangePasswordDTO;
import com.superfit.superfitapp.dto.admin.ProfessorCreateDTO;
import com.superfit.superfitapp.dto.admin.ProfessorResponseDTO;
import com.superfit.superfitapp.dto.admin.ProfessorUpdateDTO;
import com.superfit.superfitapp.dto.admin.AlunoCreateDTO;
import com.superfit.superfitapp.dto.admin.AlunoResponseDTO;
import com.superfit.superfitapp.dto.admin.AlunoUpdateDTO;
import com.superfit.superfitapp.model.User;
import com.superfit.superfitapp.model.Role;
import com.superfit.superfitapp.model.Professor;
import com.superfit.superfitapp.model.Aluno;
import java.util.List;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;
    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(
            UserRepository userRepository,
            ProfessorRepository professorRepository,
            AlunoRepository alunoRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.professorRepository = professorRepository;
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===============================
    // GESTORES
    // ===============================

    @Override
    public GestorResponseDTO cadastrarGestor(GestorCreateDTO dto) {

                if (userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email já cadastrado");
        }

        User gestor = new User();
        gestor.setEmail(dto.email());
        gestor.setPassword(passwordEncoder.encode(dto.password()));
        gestor.setRole(Role.ROLE_GESTOR);

        User salvo = userRepository.save(gestor);

        return new GestorResponseDTO(
                salvo.getId(),
                salvo.getEmail()
        );
    }

    @Override
            public void excluirGestor(Long gestorId) {

                User gestor = userRepository.findById(
                                Objects.requireNonNull(gestorId, "gestorId")
                        )
                .filter(u -> u.getRole() == Role.ROLE_GESTOR)
                .orElseThrow(() ->
                        new RuntimeException("Gestor não encontrado")
                );

                userRepository.delete(Objects.requireNonNull(gestor, "gestor"));
    }

    @Override
    public GestorResponseDTO atualizarGestor(
            Long gestorId,
            GestorUpdateDTO dto
    ) {

        User gestor = userRepository.findById(
                        Objects.requireNonNull(gestorId, "gestorId")
                )
                .filter(u -> u.getRole() == Role.ROLE_GESTOR)
                .orElseThrow(() ->
                        new RuntimeException("Gestor não encontrado")
                );

        gestor.setEmail(dto.email());

        if (dto.password() != null && !dto.password().isBlank()) {
            gestor.setPassword(
                    passwordEncoder.encode(dto.password())
            );
        }

        User atualizado = userRepository.save(
                Objects.requireNonNull(gestor, "gestor")
        );

        return new GestorResponseDTO(
                atualizado.getId(),
                atualizado.getEmail()
        );
    }

    @Override
    public List<GestorResponseDTO> listarGestores() {

        return userRepository.findByRole(Role.ROLE_GESTOR)
                .stream()
                .map(u ->
                        new GestorResponseDTO(
                                u.getId(),
                                u.getEmail()
                        )
                )
                .toList();
    }

    // ===============================
    // PROFESSORES
    // ===============================

    @Override
    public ProfessorResponseDTO cadastrarProfessor(ProfessorCreateDTO dto) {

        if (professorRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email já cadastrado");
        }

        User professor = new User();
        professor.setEmail(dto.email());
        professor.setPassword(passwordEncoder.encode(dto.password()));
        professor.setRole(Role.ROLE_PROFESSOR);
        User usuarioSalvo = userRepository.save(professor);

        Professor prof = new Professor(
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.especialidade()
        );
        prof.setUser(usuarioSalvo);
        Professor salvo = professorRepository.save(prof);

        return new ProfessorResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getTelefone(),
                salvo.getEspecialidade(),
                salvo.getAtivo()
        );
    }

    @Override
            public void excluirProfessor(Long professorId) {

                Professor professor = professorRepository.findById(
                                Objects.requireNonNull(professorId, "professorId")
                        )
                .orElseThrow(() ->
                        new RuntimeException("Professor não encontrado")
                );

                User professorUser = professor.getUser();
                if (professorUser != null) {
                        userRepository.delete(professorUser);
        }
                professorRepository.delete(Objects.requireNonNull(professor, "professor"));
    }

    @Override
    public ProfessorResponseDTO atualizarProfessor(
            Long professorId,
            ProfessorUpdateDTO dto
    ) {

        Professor professor = professorRepository.findById(
                        Objects.requireNonNull(professorId, "professorId")
                )
                .orElseThrow(() ->
                        new RuntimeException("Professor não encontrado")
                );

        if (dto.nome() != null) {
            professor.setNome(dto.nome());
        }
        if (dto.email() != null) {
            professor.setEmail(dto.email());
        }
        if (dto.telefone() != null) {
            professor.setTelefone(dto.telefone());
        }
        if (dto.especialidade() != null) {
            professor.setEspecialidade(dto.especialidade());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
                        User professorUser = professor.getUser();
                        if (professorUser != null) {
                                professorUser.setPassword(
                        passwordEncoder.encode(dto.password())
                );
                                userRepository.save(professorUser);
            }
        }

                Professor atualizado = professorRepository.save(
                        Objects.requireNonNull(professor, "professor")
                );

        return new ProfessorResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getTelefone(),
                atualizado.getEspecialidade(),
                atualizado.getAtivo()
        );
    }

    @Override
    public List<ProfessorResponseDTO> listarProfessores() {

        return professorRepository.findByAtivo(true)
                .stream()
                .map(p ->
                        new ProfessorResponseDTO(
                                p.getId(),
                                p.getNome(),
                                p.getEmail(),
                                p.getTelefone(),
                                p.getEspecialidade(),
                                p.getAtivo()
                        )
                )
                .toList();
    }

    // ===============================
    // ALUNOS
    // ===============================

    @Override
    public AlunoResponseDTO cadastrarAluno(AlunoCreateDTO dto) {

        if (alunoRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email já cadastrado");
        }

        User aluno = new User();
        aluno.setEmail(dto.email());
        aluno.setPassword(passwordEncoder.encode(dto.password()));
        aluno.setRole(Role.ROLE_ALUNO);
        User usuarioSalvo = userRepository.save(aluno);

        Aluno aln = new Aluno(
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.dataNascimento()
        );
        aln.setUser(usuarioSalvo);
        Aluno salvo = alunoRepository.save(aln);

        return new AlunoResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getTelefone(),
                salvo.getDataNascimento(),
                salvo.getAtivo()
        );
    }

    @Override
            public void excluirAluno(Long alunoId) {

                Aluno aluno = alunoRepository.findById(
                                Objects.requireNonNull(alunoId, "alunoId")
                        )
                .orElseThrow(() ->
                        new RuntimeException("Aluno não encontrado")
                );

                User alunoUser = aluno.getUser();
                if (alunoUser != null) {
                        userRepository.delete(alunoUser);
        }
                alunoRepository.delete(Objects.requireNonNull(aluno, "aluno"));
    }

    @Override
    public AlunoResponseDTO atualizarAluno(
            Long alunoId,
            AlunoUpdateDTO dto
    ) {

        Aluno aluno = alunoRepository.findById(
                        Objects.requireNonNull(alunoId, "alunoId")
                )
                .orElseThrow(() ->
                        new RuntimeException("Aluno não encontrado")
                );

        if (dto.nome() != null) {
            aluno.setNome(dto.nome());
        }
        if (dto.email() != null) {
            aluno.setEmail(dto.email());
        }
        if (dto.telefone() != null) {
            aluno.setTelefone(dto.telefone());
        }
        if (dto.dataNascimento() != null) {
            aluno.setDataNascimento(dto.dataNascimento());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
                        User alunoUser = aluno.getUser();
                        if (alunoUser != null) {
                                alunoUser.setPassword(
                        passwordEncoder.encode(dto.password())
                );
                                userRepository.save(alunoUser);
            }
        }

                Aluno atualizado = alunoRepository.save(
                        Objects.requireNonNull(aluno, "aluno")
                );

        return new AlunoResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getTelefone(),
                atualizado.getDataNascimento(),
                atualizado.getAtivo()
        );
    }

    @Override
    public List<AlunoResponseDTO> listarAlunos() {

        return alunoRepository.findByAtivo(true)
                .stream()
                .map(a ->
                        new AlunoResponseDTO(
                                a.getId(),
                                a.getNome(),
                                a.getEmail(),
                                a.getTelefone(),
                                a.getDataNascimento(),
                                a.getAtivo()
                        )
                )
                .toList();
    }

    // ===============================
    // ADMIN
    // ===============================

    @Override
        public void alterarSenha(String emailAdmin, ChangePasswordDTO dto) {

        User admin = userRepository.findByEmail(emailAdmin)
                .orElseThrow(() ->
                        new RuntimeException("Admin não encontrado")
                );

        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Usuário não é ADMIN");
        }

        if (!passwordEncoder.matches(
                dto.senhaAtual(),
                admin.getPassword()
        )) {
            throw new RuntimeException("Senha atual inválida");
        }

        admin.setPassword(
                passwordEncoder.encode(dto.novaSenha())
        );

        userRepository.save(admin);
    }
}
