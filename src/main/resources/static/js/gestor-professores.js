/* ===== CADASTRO DE PROFESSORES (GESTOR) ===== */

document.addEventListener('DOMContentLoaded', () => {
    carregarProfessores();

    const form = document.getElementById('professorForm');
    const message = document.getElementById('professoresFormMessage');

    if (!form) return;

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const payload = {
            nome: document.getElementById('nome').value.trim(),
            email: document.getElementById('email').value.trim(),
            telefone: document.getElementById('telefone').value.trim(),
            crefi: document.getElementById('crefi').value.trim()
        };

        if (!payload.nome || !payload.email || !payload.crefi) {
            showFormMessage(message, '❌ Preencha nome, email e CREFI.', 'error');
            return;
        }

        try {
            const response = await fetch('/api/professores', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                throw new Error(`Status ${response.status}`);
            }

            showFormMessage(message, '✅ Professor cadastrado com sucesso!', 'success');
            form.reset();
            setTimeout(() => {
                carregarProfessores();
            }, 1000);
        } catch (error) {
            console.error('Erro ao cadastrar professor:', error);
            showFormMessage(message, '❌ Nao foi possivel cadastrar o professor.', 'error');
        }
    });

    const editForm = document.getElementById('editProfessorForm');
    if (editForm) {
        editForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const id = document.getElementById('editProfessorId').value;
            const payload = {
                nome: document.getElementById('editNome').value.trim(),
                email: document.getElementById('editEmail').value.trim(),
                telefone: document.getElementById('editTelefone').value.trim(),
                crefi: document.getElementById('editCrefi').value.trim()
            };

            // Adiciona senha apenas se foi preenchida
            const senha = document.getElementById('editSenha').value.trim();
            if (senha) {
                payload.senha = senha;
            }

            if (!payload.nome || !payload.email || !payload.crefi) {
                showModalMessage('editModalMessage', '❌ Preencha nome, email e CREFI.', 'error');
                return;
            }

            try {
                const response = await fetch(`/api/professores/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    throw new Error(`Status ${response.status}`);
                }

                showModalMessage('editModalMessage', '✅ Professor atualizado com sucesso!', 'success');
                setTimeout(() => {
                    closeEditModal();
                    carregarProfessores();
                }, 1000);
            } catch (error) {
                console.error('Erro ao atualizar professor:', error);
                showModalMessage('editModalMessage', '❌ Erro ao atualizar professor.', 'error');
            }
        });
    }

    document.addEventListener('click', (e) => {
        const modal = document.getElementById('editModalProfessor');
        if (modal && e.target === modal) {
            closeEditModal();
        }
    });
});

async function carregarProfessores() {
    const tbody = document.getElementById('professoresBody');
    const emptyState = document.getElementById('emptyState');
    const tableWrapper = document.querySelector('.table-wrapper');

    if (!tbody) return;

    try {
        const response = await fetch('/api/professores', { credentials: 'include' });
        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        const professores = await response.json();

        if (!professores || professores.length === 0) {
            tbody.innerHTML = '';
            if (emptyState) emptyState.style.display = 'block';
            if (tableWrapper) tableWrapper.style.display = 'none';
        } else {
            if (emptyState) emptyState.style.display = 'none';
            if (tableWrapper) tableWrapper.style.display = 'block';
            tbody.innerHTML = professores.map(prof => `
                <tr class="table__body-row">
                    <td class="table__body-cell">${prof.nome}</td>
                    <td class="table__body-cell">${prof.email}</td>
                    <td class="table__body-cell">${prof.crefi || '-'}</td>
                    <td class="table__body-cell">
                        <span class="badge badge--success">Ativo</span>
                    </td>
                    <td class="table__body-cell">
                        <div class="table__actions">
                            <button class="btn btn--primary btn--sm" onclick="editarProfessor(${prof.id})">Editar</button>
                            <button class="btn btn--danger btn--sm" onclick="removerProfessor(${prof.id})">Remover</button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Erro ao carregar professores:', error);
        if (emptyState) emptyState.style.display = 'block';
        if (emptyState) emptyState.textContent = '❌ Erro ao carregar professores';
    }
}

function editarProfessor(id) {
    const modal = document.getElementById('editModalProfessor');
    if (!modal) return;

    fetch(`/api/professores/${id}`, { credentials: 'include' })
        .then(response => {
            if (!response.ok) throw new Error(`Status ${response.status}`);
            return response.json();
        })
        .then(professor => {
            document.getElementById('editProfessorId').value = professor.id;
            document.getElementById('editNome').value = professor.nome;
            document.getElementById('editEmail').value = professor.email;
            document.getElementById('editTelefone').value = professor.telefone || '';
            document.getElementById('editCrefi').value = professor.crefi;
            modal.style.display = 'block';
            document.body.style.overflow = 'hidden';
        })
        .catch(error => {
            console.error('Erro ao carregar professor:', error);
            alert('Erro ao carregar dados do professor');
        });
}

function closeEditModal() {
    const modal = document.getElementById('editModalProfessor');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const editForm = document.getElementById('editProfessorForm');
    if (editForm) {
        editForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const id = document.getElementById('editProfessorId').value;
            const payload = {
                nome: document.getElementById('editNome').value.trim(),
                email: document.getElementById('editEmail').value.trim(),
                telefone: document.getElementById('editTelefone').value.trim(),
                crefi: document.getElementById('editCrefi').value.trim()
            };

            // Adiciona senha apenas se foi preenchida
            const senha = document.getElementById('editSenha').value.trim();
            if (senha) {
                payload.senha = senha;
            }

            if (!payload.nome || !payload.email || !payload.crefi) {
                showModalMessage('editModalMessage', '❌ Preencha nome, email e CREFI.', 'error');
                return;
            }

            try {
                const response = await fetch(`/api/professores/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    throw new Error(`Status ${response.status}`);
                }

                showModalMessage('editModalMessage', '✅ Professor atualizado com sucesso!', 'success');
                setTimeout(() => {
                    closeEditModal();
                    carregarProfessores();
                }, 1000);
            } catch (error) {
                console.error('Erro ao atualizar professor:', error);
                showModalMessage('editModalMessage', '❌ Erro ao atualizar professor.', 'error');
            }
        });
    }

    document.addEventListener('click', (e) => {
        const modal = document.getElementById('editModalProfessor');
        if (modal && e.target === modal) {
            closeEditModal();
        }
    });
});

function showModalMessage(elementId, text, type) {
    const container = document.getElementById(elementId);
    if (!container) return;
    container.className = `message message--${type}`;
    container.textContent = text;
    container.style.display = 'block';
}

function removerProfessor(id) {
    if (!confirm('Deseja remover este professor?')) return;

    fetch(`/api/professores/${id}`, {
        method: 'DELETE',
        credentials: 'include'
    })
    .then(response => {
        if (!response.ok) throw new Error(`Status ${response.status}`);
        carregarProfessores();
    })
    .catch(error => {
        console.error('Erro ao remover professor:', error);
        alert('Erro ao remover professor');
    });
}

function showFormMessage(container, text, type) {
    if (!container) return;
    container.className = `message message--${type}`;
    container.textContent = text;
}
