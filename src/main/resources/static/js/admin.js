/* ===== SCRIPT DO ADMIN ===== */

let editingGestorId = null;

// Carregar gestores ao iniciar
document.addEventListener('DOMContentLoaded', () => {
    console.log('Admin dashboard carregado');
    loadGestores();
    setupEventListeners();
});

/**
 * Configura event listeners
 */
function setupEventListeners() {
    console.log('Configurando event listeners');
    
    // Evento de cadastro
    const createForm = document.getElementById('createForm');
    if (createForm) {
        createForm.addEventListener('submit', handleCreateGestor);
        console.log('Listener de cadastro registrado');
    } else {
        console.error('Formulário de criação não encontrado');
    }

    // Evento de edição
    const editForm = document.getElementById('editForm');
    if (editForm) {
        editForm.addEventListener('submit', handleEditGestor);
        console.log('Listener de edição registrado');
    } else {
        console.error('Formulário de edição não encontrado');
    }

    // Fechar modal ao clicar fora
    window.addEventListener('click', (event) => {
        const modal = document.getElementById('editModal');
        if (event.target === modal) {
            closeEditModal();
        }
    });
}

/**
 * Carrega lista de gestores
 */
async function loadGestores() {
    console.log('Carregando gestores...');
    try {
        const response = await fetch('/admin/api/gestores');
        
        if (!response.ok) {
            console.error('Erro na resposta:', response.status);
            throw new Error(`Status: ${response.status}`);
        }
        
        const gestores = await response.json();
        console.log('Gestores carregados:', gestores);

        const tbody = document.getElementById('gestoresBody');
        const emptyState = document.getElementById('emptyState');
        const table = document.getElementById('gestoresTable');

        if (!tbody || !emptyState || !table) {
            console.error('Elementos da tabela não encontrados');
            return;
        }

        if (gestores.length === 0) {
            tbody.innerHTML = '';
            emptyState.classList.remove('d-none');
            table.style.display = 'none';
        } else {
            emptyState.classList.add('d-none');
            table.style.display = 'table';
            tbody.innerHTML = gestores.map(g => `
                <tr class="table__body-row">
                    <td class="table__body-cell">${g.id}</td>
                    <td class="table__body-cell">${g.email}</td>
                    <td class="table__body-cell">
                        <div class="table__actions">
                            <button class="btn btn--info btn--sm" onclick="openEditModal(${g.id}, '${g.email}')">Editar</button>
                            <button class="btn btn--danger btn--sm" onclick="deleteGestor(${g.id})">Deletar</button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Erro ao carregar gestores:', error);
        showMessage('listMessage', '❌ Erro ao carregar gestores', 'error');
    }
}

/**
 * Handle de criação de gestor
 */
async function handleCreateGestor(e) {
    e.preventDefault();
    console.log('Criando gestor...');

    const email = document.getElementById('newEmail').value.trim();
    const password = document.getElementById('newPassword').value;

    if (!email) {
        showMessage('createMessage', '❌ Email é obrigatório', 'error');
        return;
    }

    if (password.length < 6) {
        showMessage('createMessage', '❌ Senha deve ter no mínimo 6 caracteres', 'error');
        return;
    }

    try {
        const response = await fetch('/admin/api/gestores', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password })
        });

        console.log('Resposta de criação:', response.status);

        if (response.ok) {
            showMessage('createMessage', '✅ Gestor cadastrado com sucesso!', 'success', 1500);
            document.getElementById('createForm').reset();
            loadGestores();
        } else {
            const error = await response.json();
            showMessage('createMessage', `❌ Erro: ${error.message || 'Email já cadastrado'}`, 'error');
        }
    } catch (error) {
        console.error('Erro:', error);
        showMessage('createMessage', '❌ Erro ao conectar ao servidor', 'error');
    }
}

/**
 * Abre modal de edição
 */
function openEditModal(id, email) {
    console.log('Abrindo modal de edição para ID:', id);
    editingGestorId = id;
    document.getElementById('editEmail').value = email;
    document.getElementById('editPassword').value = '';
    document.getElementById('editMessage').innerHTML = '';
    openModal('editModal');
}

/**
 * Fecha modal de edição
 */
function closeEditModal() {
    console.log('Fechando modal de edição');
    closeModal('editModal');
    editingGestorId = null;
}

/**
 * Handle de edição de gestor
 */
async function handleEditGestor(e) {
    e.preventDefault();
    console.log('Editando gestor ID:', editingGestorId);

    const email = document.getElementById('editEmail').value.trim();
    const password = document.getElementById('editPassword').value;

    if (!email) {
        showMessage('editMessage', '❌ Email é obrigatório', 'error');
        return;
    }

    try {
        const response = await fetch(`/admin/api/gestores/${editingGestorId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email,
                password: password || null
            })
        });

        console.log('Resposta de edição:', response.status);

        if (response.ok) {
            showMessage('editMessage', '✅ Gestor atualizado com sucesso!', 'success', 1500);
            setTimeout(() => {
                closeEditModal();
                loadGestores();
            }, 1500);
        } else {
            const error = await response.json();
            showMessage('editMessage', `❌ Erro: ${error.message || 'Falha ao atualizar'}`, 'error');
        }
    } catch (error) {
        console.error('Erro:', error);
        showMessage('editMessage', '❌ Erro ao conectar ao servidor', 'error');
    }
}

/**
 * Deleta um gestor
 */
async function deleteGestor(id) {
    console.log('Deletando gestor ID:', id);
    
    if (!confirmAction('Tem certeza que deseja deletar este gestor?')) {
        console.log('Deleção cancelada pelo usuário');
        return;
    }

    try {
        const response = await fetch(`/admin/api/gestores/${id}`, {
            method: 'DELETE'
        });

        console.log('Resposta de deleção:', response.status);

        if (response.ok) {
            showMessage('listMessage', '✅ Gestor deletado com sucesso!', 'success', 1500);
            loadGestores();
        } else {
            showMessage('listMessage', '❌ Erro ao deletar gestor', 'error');
        }
    } catch (error) {
        console.error('Erro:', error);
        showMessage('listMessage', '❌ Erro ao conectar ao servidor', 'error');
    }
}

