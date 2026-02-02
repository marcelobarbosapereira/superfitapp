package com.superfit.superfitapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Column(name = "data_nascimento")
    private String dataNascimento;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "bio")
    private String bio;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /* ===== Constructors ===== */

    public Aluno() {}

    public Aluno(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.ativo = true;
    }

    public Aluno(String nome, String email, String telefone, String dataNascimento) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.ativo = true;
    }

    /* ===== Getters & Setters ===== */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    
    /**
     * Relacionamento com User (login)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Muitos alunos pertencem a um professor
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;
}
