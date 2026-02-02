package com.superfit.superfitapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "professores")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Column(name = "especialidade")
    private String especialidade;

    @Column(name = "bio")
    private String bio;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /* ===== Constructors ===== */

    public Professor() {}

    public Professor(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.ativo = true;
    }

    public Professor(String nome, String email, String telefone, String especialidade) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.especialidade = especialidade;
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

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
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
     * Um professor possui vários alunos
     */
    @OneToMany(
            mappedBy = "professor",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Aluno> alunos;
}
