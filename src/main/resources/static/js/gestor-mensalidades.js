/* ===== CADASTRO DE MENSALIDADES (GESTOR) ===== */

document.addEventListener('DOMContentLoaded', () => {
    carregarAlunos();
    carregarMensalidades();

    const form = document.getElementById('mensalidadeForm');
    const message = document.getElementById('mensalidadesFormMessage');

    if (form) {
        form.addEventListener('submit', async (event) => {
            event.preventDefault();

            const payload = {
                alunoId: parseInt(document.getElementById('alunoId').value, 10),
                valor: parseFloat(document.getElementById('valor').value),
                status: document.getElementById('status').value,
                dataVencimento: document.getElementById('dataVencimento').value,
                mesReferencia: document.getElementById('mesReferencia').value,
                anoReferencia: parseInt(document.getElementById('anoReferencia').value, 10),
                observacoes: document.getElementById('observacoes').value.trim()
            };

            if (!payload.alunoId || !payload.valor || !payload.dataVencimento || !payload.mesReferencia || !payload.anoReferencia) {
                showFormMessage(message, '❌ Preencha todos os campos obrigatórios.', 'error');
                return;
            }

            try {
                const response = await fetch('/api/mensalidades', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    throw new Error(`Status ${response.status}`);
                }

                showFormMessage(message, '✅ Mensalidade cadastrada com sucesso!', 'success');
                form.reset();
                setTimeout(() => {
                    carregarMensalidades();
                }, 1000);
            } catch (error) {
                console.error('Erro ao cadastrar mensalidade:', error);
                showFormMessage(message, '❌ Não foi possível cadastrar a mensalidade.', 'error');
            }
        });
    }

    // Configura o formulário de edição de mensalidade
    const editForm = document.getElementById('editMensalidadeForm');
    if (editForm) {
        editForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const id = document.getElementById('editMensalidadeId').value;
            const payload = {
                valor: parseFloat(document.getElementById('editValor').value),
                status: document.getElementById('editStatus').value,
                dataVencimento: document.getElementById('editDataVencimento').value,
                observacoes: document.getElementById('editObservacoes').value.trim()
            };

            if (!payload.valor || !payload.dataVencimento) {
                showModalMessage('editMensalidadeModalMessage', '❌ Preencha valor e data de vencimento.', 'error');
                return;
            }

            try {
                const response = await fetch(`/api/mensalidades/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    throw new Error(`Status ${response.status}`);
                }

                showModalMessage('editMensalidadeModalMessage', '✅ Mensalidade atualizada com sucesso!', 'success');
                setTimeout(() => {
                    closeEditModalMensalidade();
                    carregarMensalidades();
                }, 1000);
            } catch (error) {
                console.error('Erro ao atualizar mensalidade:', error);
                showModalMessage('editMensalidadeModalMessage', '❌ Erro ao atualizar mensalidade.', 'error');
            }
        });
    }

    // Fechar modal ao clicar fora
    document.addEventListener('click', (e) => {
        const modal = document.getElementById('editModalMensalidade');
        if (modal && e.target === modal) {
            closeEditModalMensalidade();
        }
    });
});

async function carregarAlunos() {
    const select = document.getElementById('alunoId');
    if (!select) return;

    try {
        const response = await fetch('/api/alunos', { credentials: 'include' });
        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        const alunos = await response.json();
        select.innerHTML = '<option value="">Selecione um aluno</option>' + alunos
            .map(aluno => `<option value="${aluno.id}">${aluno.nome}</option>`)
            .join('');
    } catch (error) {
        console.error('Erro ao carregar alunos:', error);
        select.innerHTML = '<option value="">Sem alunos disponíveis</option>';
    }
}

async function carregarMensalidades() {
    const tbody = document.getElementById('mensalidadesBody');
    const emptyState = document.getElementById('emptyState');
    const tableWrapper = document.querySelector('.table-wrapper');

    if (!tbody) return;

    try {
        const response = await fetch('/api/mensalidades', { credentials: 'include' });
        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        const mensalidades = await response.json();

        if (!mensalidades || mensalidades.length === 0) {
            tbody.innerHTML = '';
            if (emptyState) emptyState.style.display = 'block';
            if (tableWrapper) tableWrapper.style.display = 'none';
        } else {
            if (emptyState) emptyState.style.display = 'none';
            if (tableWrapper) tableWrapper.style.display = 'block';
            tbody.innerHTML = mensalidades.map(m => {
                const statusClass = m.status === 'PAGA' ? 'success' : 'warning';
                const statusText = m.status === 'PAGA' ? 'Paga' : 'Pendente';
                
                return `
                <tr class="table__body-row">
                    <td class="table__body-cell">${m.alunoNome || 'N/A'}</td>
                    <td class="table__body-cell">R$ ${m.valor.toFixed(2)}</td>
                    <td class="table__body-cell">${formatarData(m.dataVencimento)}</td>
                    <td class="table__body-cell">${m.mesReferencia}/${m.anoReferencia}</td>
                    <td class="table__body-cell">
                        <span class="badge badge--${statusClass}">${statusText}</span>
                    </td>
                    <td class="table__body-cell">
                        <button onclick="editarMensalidade(${m.id})" class="btn btn--secondary btn--small">Editar</button>
                        ${m.status !== 'PAGA' ? `<button onclick="marcarComoPaga(${m.id})" class="btn btn--primary btn--small">Pagar</button>` : ''}
                        <button onclick="removerMensalidade(${m.id})" class="btn btn--danger btn--small">Remover</button>
                    </td>
                </tr>
            `;
            }).join('');
        }
    } catch (error) {
        console.error('Erro ao carregar mensalidades:', error);
        if (tbody) tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">Erro ao carregar mensalidades</td></tr>';
    }
}

async function editarMensalidade(id) {
    try {
        const response = await fetch(`/api/mensalidades/${id}`, { credentials: 'include' });
        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        const mensalidade = await response.json();
        
        document.getElementById('editMensalidadeId').value = mensalidade.id;
        document.getElementById('editValor').value = mensalidade.valor;
        document.getElementById('editStatus').value = mensalidade.status;
        document.getElementById('editDataVencimento').value = mensalidade.dataVencimento;
        document.getElementById('editObservacoes').value = mensalidade.observacoes || '';
        document.getElementById('editAlunoNome').textContent = mensalidade.alunoNome || 'N/A';

        const modal = document.getElementById('editModalMensalidade');
        if (modal) {
            modal.style.display = 'block';
            document.body.style.overflow = 'hidden';
        }
    } catch (error) {
        console.error('Erro ao buscar mensalidade:', error);
        alert('Erro ao carregar mensalidade para edição');
    }
}

function closeEditModalMensalidade() {
    const modal = document.getElementById('editModalMensalidade');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

async function marcarComoPaga(id) {
    if (!confirm('Confirmar o pagamento desta mensalidade?')) {
        return;
    }

    try {
        const response = await fetch(`/api/mensalidades/${id}/pagar`, {
            method: 'PUT',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        alert('✅ Mensalidade marcada como paga!');
        carregarMensalidades();
    } catch (error) {
        console.error('Erro ao marcar como paga:', error);
        alert('❌ Erro ao marcar mensalidade como paga');
    }
}

async function removerMensalidade(id) {
    if (!confirm('Tem certeza que deseja remover esta mensalidade?')) {
        return;
    }

    try {
        const response = await fetch(`/api/mensalidades/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        alert('✅ Mensalidade removida com sucesso!');
        carregarMensalidades();
    } catch (error) {
        console.error('Erro ao remover mensalidade:', error);
        alert('❌ Erro ao remover mensalidade');
    }
}

function formatarData(dataStr) {
    if (!dataStr) return 'N/A';
    const [ano, mes, dia] = dataStr.split('-');
    return `${dia}/${mes}/${ano}`;
}

function showFormMessage(element, message, type) {
    if (!element) return;
    element.textContent = message;
    element.style.display = 'block';
    element.style.backgroundColor = type === 'success' ? '#d4edda' : '#f8d7da';
    element.style.color = type === 'success' ? '#155724' : '#721c24';
    element.style.padding = '0.75rem';
    element.style.borderRadius = '6px';
    element.style.marginBottom = '1rem';
    
    setTimeout(() => {
        element.style.display = 'none';
    }, 5000);
}

function showModalMessage(messageId, message, type) {
    const element = document.getElementById(messageId);
    if (!element) return;
    element.textContent = message;
    element.style.display = 'block';
    element.style.backgroundColor = type === 'success' ? '#d4edda' : '#f8d7da';
    element.style.color = type === 'success' ? '#155724' : '#721c24';
    
    setTimeout(() => {
        element.style.display = 'none';
    }, 5000);
}
