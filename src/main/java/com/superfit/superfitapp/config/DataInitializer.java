package com.superfit.superfitapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.superfit.superfitapp.model.Role;
import com.superfit.superfitapp.model.User;
import com.superfit.superfitapp.repository.UserRepository;

/**
 * Inicializador de dados padrão do sistema.
 * Executa automaticamente na inicialização da aplicação (CommandLineRunner).
 * 
 * Responsabilidades:
 * - Criar usuário ADMIN padrão (email: admin, senha: 12345)
 * - Criar usuário GESTOR padrão (email: gestor, senha: 12345)
 * - Verificar existência antes de criar para evitar duplicação
 * - Logar informações de criação no console
 * 
 * Nota: Em produção, considere remover ou alterar senhas padrão.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Método executado automaticamente após a inicialização da aplicação.
     * Cria usuários padrão se não existirem no banco de dados.
     * 
     * @param args Argumentos da linha de comando (não utilizados)
     * @throws Exception em caso de erro na inicialização
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("Iniciando DataInitializer...");
        System.out.println("========================================");
        
        // Cria usuário ADMIN
        criarAdminSeNaoExistir();
        
        // Cria GESTOR padrão
        criarGestorSeNaoExistir();
        
        // Cria PROFESSOR padrão
        criarProfessorSeNaoExistir();
        
        // Cria ALUNO padrão
        criarAlunoSeNaoExistir();
        
        System.out.println("========================================");
        System.out.println("DataInitializer finalizado!");
        System.out.println("========================================\n");
    }
    
    /**
     * Cria o usuário ADMIN padrão se não existir.
     * Credenciais: email="admin", senha="12345", role=ROLE_ADMIN.
     * Senha é codificada com BCrypt antes de persistir.
     */
    private void criarAdminSeNaoExistir() {
        if (!userRepository.existsByEmail("admin@superfit.com")) {
            User adminUser = new User(
                "admin@superfit.com",
                passwordEncoder.encode("12345"),
                Role.ROLE_ADMIN
            );
            userRepository.save(adminUser);
            System.out.println("✅ Usuário ADMIN criado com sucesso!");
            System.out.println("   Email: admin | Senha: 12345");
        } else {
            System.out.println("ℹ️ Usuário ADMIN já existe");
        }
    }
    
    /**
     * Cria o usuário GESTOR padrão se não existir.
     * Credenciais: email="gestor", senha="12345", role=ROLE_GESTOR.
     * Senha é codificada com BCrypt antes de persistir.
     */
    private void criarGestorSeNaoExistir() {
        if (!userRepository.existsByEmail("gestor@superfit.com")) {
            User gestorUser = new User(
                "gestor@superfit.com",
                passwordEncoder.encode("12345"),
                Role.ROLE_GESTOR
            );
            userRepository.save(gestorUser);
            System.out.println("✅ Usuário GESTOR criado com sucesso!");
            System.out.println("   Email: gestor | Senha: 12345");
        } else {
            System.out.println("ℹ️ Usuário GESTOR já existe");
        }
    }
    
    /**
     * Cria o usuário PROFESSOR padrão se não existir.
     * Credenciais: email="professor", senha="12345", role=ROLE_PROFESSOR.
     * Senha é codificada com BCrypt antes de persistir.
     */
    private void criarProfessorSeNaoExistir() {
        if (!userRepository.existsByEmail("professor@superfit.com")) {
            User professorUser = new User(
                "professor@superfit.com",
                passwordEncoder.encode("12345"),
                Role.ROLE_PROFESSOR
            );
            userRepository.save(professorUser);
            System.out.println("✅ Usuário PROFESSOR criado com sucesso!");
            System.out.println("   Email: professor | Senha: 12345");
        } else {
            System.out.println("ℹ️ Usuário PROFESSOR já existe");
        }
    }
    
    /**
     * Cria o usuário ALUNO padrão se não existir.
     * Credenciais: email="aluno", senha="12345", role=ROLE_ALUNO.
     * Senha é codificada com BCrypt antes de persistir.
     */
    private void criarAlunoSeNaoExistir() {
        if (!userRepository.existsByEmail("aluno@superfit.com")) {
            User alunoUser = new User(
                "aluno@superfit.com",
                passwordEncoder.encode("12345"),
                Role.ROLE_ALUNO
            );
            userRepository.save(alunoUser);
            System.out.println("✅ Usuário ALUNO criado com sucesso!");
            System.out.println("   Email: aluno@superfit.com | Senha: 12345");
        } else {
            System.out.println("ℹ️ Usuário ALUNO já existe");
        }
    }
}
