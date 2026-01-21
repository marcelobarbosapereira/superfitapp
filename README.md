# SuperFitApp 🏋️‍♂️

SuperFitApp é um sistema web para **gestão de academias**, desenvolvido com **Java Spring Boot** no backend e **React.js** no frontend.

O sistema permite o controle de alunos, professores, treinos, medidas físicas, mensalidades e despesas, com autenticação segura via **JWT** e permissões por perfil de usuário.

Este projeto está sendo desenvolvido de forma incremental, com foco em um **MVP funcional** e boas práticas de desenvolvimento.

---

## 🚀 Tecnologias Utilizadas

### Backend
- Java 17+
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- JPA / Hibernate
- Banco de dados H2 (ambiente de desenvolvimento)

### Frontend
- React.js
- Axios
- React Router DOM

---

## 👥 Perfis de Usuário

### Gestor
- Cadastrar e gerenciar professores e alunos
- Controlar mensalidades
- Registrar despesas da academia
- Emitir relatórios financeiros e operacionais

### Professor
- Acompanhar um ou mais alunos
- Prescrever e atualizar treinos
- Registrar e alterar medidas físicas dos alunos

### Aluno
- Visualizar seus treinos
- Acompanhar medidas e progresso físico
- Consultar status de pagamento
- Acessar link fictício de pagamento

---

## 🔐 Autenticação e Segurança

- Autenticação baseada em **JWT**
- Controle de acesso por **roles** (GESTOR, PROFESSOR, ALUNO)
- Endpoints protegidos via Spring Security

---

## 🧩 Funcionalidades (MVP)

- [ ] Autenticação e autorização de usuários
- [ ] CRUD de professores e alunos
- [ ] Prescrição e visualização de treinos
- [ ] Registro de medidas físicas e histórico de progresso
- [ ] Controle de mensalidades e despesas
- [ ] Relatórios básicos
- [ ] Interface web com React

---

## 📁 Estrutura do Projeto (Backend)

src/main/java/com/superfitapp
├── controller
├── service
├── repository
├── model
├── dto
├── security
└── SuperFitAppApplication.java


---

## ▶️ Como Executar (Backend)

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/superfitapp.git 
```
2. Acesse o projeto backend e execute:
```bash
./mvnw spring-boot:run
```
3. Acesse o H2 Console:
```bash
http://localhost:8080/h2-console
```

📌 Status do Projeto

🚧 Em desenvolvimento — MVP em construção.