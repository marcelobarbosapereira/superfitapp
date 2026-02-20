/* ===== CADASTRO DE ALUNOS (GESTOR) ===== */

document.addEventListener('DOMContentLoaded', () => {
    carregarProfessores();
    carregarAlunos();

    const form = document.getElementById('alunoForm');
    const message = document.getElementById('alunosFormMessage');

    if (form) {
        form.addEventListener('submit', async (event) => {
            event.preventDefault();

            const payload = {
                nome: document.getElementById('nome').value.trim(),
                email: document.getElementById('email').value.trim(),
                telefone: document.getElementById('telefone').value.trim(),
                professorId: parseInt(document.getElementById('professorId').value, 10)
            };

            if (!payload.nome || !payload.email || Number.isNaN(payload.professorId)) {
                showFormMessage(message, '❌ Preencha nome, email e professor.', 'error');
                return;
            }

            try {
                const response = await fetch('/api/alunos', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    throw new Error(`Status ${response.status}`);
                }

                showFormMessage(message, '✅ Aluno cadastrado com sucesso!', 'success');
                form.reset();
                setTimeout(() => {
                    carregarAlunos();
                }, 1000);
            } catch (error) {
                console.error('Erro ao cadastrar aluno:', error);
                showFormMessage(message, '❌ Nao foi possivel cadastrar o aluno.', 'error');
            }
        });
    }

    // Configura o formulario de edicao de aluno
    const editForm = document.getElementById('editAlunoForm');
    if (editForm) {
        editForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const id = document.getElementById('editAlunoId').value;
            const payload = {
                nome: document.getElementById('editAlunoNome').value.trim(),
                email: document.getElementById('editAlunoEmail').value.trim(),
                telefone: document.getElementById('editAlunoTelefone').value.trim(),
                ativo: true  // Mantém o aluno ativo ao editar
            };

            // Adiciona senha apenas se foi preenchida
            const senha = document.getElementById('editAlunoSenha').value.trim();
            if (senha) {
                payload.senha = senha;
            }

            if (!payload.nome || !payload.email) {
                showModalMessage('editAlunoModalMessage', '❌ Preencha nome e email.', 'error');
                return;
            }

            try {
                const response = await fetch(`/api/alunos/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    throw new Error(`Status ${response.status}`);
                }

                showModalMessage('editAlunoModalMessage', '✅ Aluno atualizado com sucesso!', 'success');
                setTimeout(() => {
                    closeEditModalAluno();
                    carregarAlunos();
                }, 1000);
            } catch (error) {
                console.error('Erro ao atualizar aluno:', error);
                showModalMessage('editAlunoModalMessage', '❌ Erro ao atualizar aluno.', 'error');
            }
        });
    }

    // Fechar modal ao clicar fora
    document.addEventListener('click', (e) => {
        const modal = document.getElementById('editModalAluno');
        if (modal && e.target === modal) {
            closeEditModalAluno();
        }
    });
});

async function carregarProfessores() {
    const select = document.getElementById('professorId');
    if (!select) return;

    try {
        const response = await fetch('/api/professores', { credentials: 'include' });
        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        const professores = await response.json();
        select.innerHTML = '<option value="">Selecione</option>' + professores
            .map(professor => `<option value="${professor.id}">${professor.nome}</option>`)
            .join('');
    } catch (error) {
        console.error('Erro ao carregar professores:', error);
        select.innerHTML = '<option value="">Sem professores disponiveis</option>';
    }
}

async function carregarAlunos() {
    const tbody = document.getElementById('alunosBody');
    const emptyState = document.getElementById('emptyState');
    const tableWrapper = document.querySelector('.table-wrapper');

    if (!tbody) return;

    try {
        const response = await fetch('/api/alunos', { credentials: 'include' });
        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        const alunos = await response.json();

        if (!alunos || alunos.length === 0) {
            tbody.innerHTML = '';
            if (emptyState) emptyState.style.display = 'block';
            if (tableWrapper) tableWrapper.style.display = 'none';
        } else {
            if (emptyState) emptyState.style.display = 'none';
            if (tableWrapper) tableWrapper.style.display = 'block';
            tbody.innerHTML = alunos.map(aluno => `
                <tr class="table__body-row">
                    <td class="table__body-cell">${aluno.nome}</td>
                    <td class="table__body-cell">${aluno.email}</td>
                    <td class="table__body-cell">
                        <span class="badge badge--${aluno.ativo ? 'success' : 'danger'}">
                            ${aluno.ativo ? 'Ativo' : 'Inativo'}
                        </span>
                    </td>
                    <td class="table__body-cell">-</td>
                    <td class="table__body-cell">
                        <div class="table__actions">
                            <button class="btn btn--primary btn--sm" onclick="editarAluno(${aluno.id})">Editar</button>
                            <button class="btn btn--danger btn--sm" onclick="removerAluno(${aluno.id})">Remover</button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Erro ao carregar alunos:', error);
        if (emptyState) emptyState.style.display = 'block';
        if (emptyState) emptyState.textContent = '❌ Erro ao carregar alunos';
    }
}

function editarAluno(id) {
    const modal = document.getElementById('editModalAluno');
    if (!modal) return;

    // Carregar professores no dropdown
    const selectProfessor = document.getElementById('editAlunoProfessor');
    fetch('/api/professores', { credentials: 'include' })
        .then(response => response.ok ? response.json() : Promise.reject('Erro'))
        .then(professores => {
            selectProfessor.innerHTML = '<option value="">Selecione</option>' + professores
                .map(professor => `<option value="${professor.id}">${professor.nome}</option>`)
                .join('');
        })
        .catch(error => console.error('Erro ao carregar professores:', error));

    // Carregar dados do aluno
    fetch(`/api/alunos/${id}`, { credentials: 'include' })
        .then(response => {
            if (!response.ok) throw new Error(`Status ${response.status}`);
            return response.json();
        })
        .then(aluno => {
            document.getElementById('editAlunoId').value = aluno.id;
            document.getElementById('editAlunoNome').value = aluno.nome;
            document.getElementById('editAlunoEmail').value = aluno.email;
            document.getElementById('editAlunoTelefone').value = aluno.telefone || '';
            document.getElementById('editAlunoProfessor').value = aluno.professorId;
            modal.style.display = 'block';
            document.body.style.overflow = 'hidden';
        })
        .catch(error => {
            console.error('Erro ao carregar aluno:', error);
            alert('Erro ao carregar dados do aluno');
        });
}

function closeEditModalAluno() {
    const modal = document.getElementById('editModalAluno');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

function removerAluno(id) {
    if (!confirm('Deseja remover este aluno?')) return;

    fetch(`/api/alunos/${id}`, {
        method: 'DELETE',
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) throw new Error(`Status ${response.status}`);
        carregarAlunos();
    })
    .catch(error => {
        console.error('Erro ao remover aluno:', error);
        alert('Erro ao remover aluno');
    });
}

function showFormMessage(container, text, type) {
    if (!container) return;
    container.className = `message message--${type}`;
    container.textContent = text;
}

function showModalMessage(elementId, text, type) {
    const container = document.getElementById(elementId);
    if (!container) return;
    container.className = `message message--${type}`;
    container.textContent = text;
    container.style.display = 'block';
}
