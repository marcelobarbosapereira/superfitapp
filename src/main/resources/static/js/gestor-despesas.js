/* ===== CADASTRO DE DESPESAS (GESTOR) ===== */

document.addEventListener('DOMContentLoaded', () => {
    carregarDespesas();

    const form = document.getElementById('despesaForm');
    const message = document.getElementById('despesasFormMessage');
    const pagaCheckbox = document.getElementById('paga');
    const dataPagamentoInput = document.getElementById('dataPagamento');

    if (pagaCheckbox && dataPagamentoInput) {
        pagaCheckbox.addEventListener('change', () => {
            dataPagamentoInput.disabled = !pagaCheckbox.checked;
            if (!pagaCheckbox.checked) {
                dataPagamentoInput.value = '';
            }
        });
    }

    if (form) {
        form.addEventListener('submit', async (event) => {
            event.preventDefault();

            const paga = pagaCheckbox ? pagaCheckbox.checked : false;
            const dataPagamentoValue = dataPagamentoInput ? dataPagamentoInput.value : '';

            const payload = {
                descricao: document.getElementById('descricao').value.trim(),
                valor: parseFloat(document.getElementById('valor').value),
                categoria: document.getElementById('categoria').value,
                dataDespesa: document.getElementById('dataDespesa').value,
                paga: paga,
                dataPagamento: paga ? (dataPagamentoValue || document.getElementById('dataDespesa').value) : null,
                observacoes: document.getElementById('observacoes').value.trim()
            };

            if (!payload.descricao || !payload.valor || !payload.categoria || !payload.dataDespesa) {
                showFormMessage(message, '❌ Preencha todos os campos obrigatorios.', 'error');
                return;
            }

            try {
                const response = await fetch('/api/despesas', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    throw new Error(`Status ${response.status}`);
                }

                showFormMessage(message, '✅ Despesa cadastrada com sucesso!', 'success');
                form.reset();
                if (dataPagamentoInput) {
                    dataPagamentoInput.disabled = true;
                }
                setTimeout(() => {
                    carregarDespesas();
                }, 1000);
            } catch (error) {
                console.error('Erro ao cadastrar despesa:', error);
                showFormMessage(message, '❌ Nao foi possivel cadastrar a despesa.', 'error');
            }
        });
    }

    const editForm = document.getElementById('editDespesaForm');
    const editPagaCheckbox = document.getElementById('editPaga');
    const editDataPagamentoInput = document.getElementById('editDataPagamento');

    if (editPagaCheckbox && editDataPagamentoInput) {
        editPagaCheckbox.addEventListener('change', () => {
            editDataPagamentoInput.disabled = !editPagaCheckbox.checked;
            if (!editPagaCheckbox.checked) {
                editDataPagamentoInput.value = '';
            }
        });
    }

    if (editForm) {
        editForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const id = document.getElementById('editDespesaId').value;
            const editPaga = editPagaCheckbox ? editPagaCheckbox.checked : false;
            const editDataPagamento = editDataPagamentoInput ? editDataPagamentoInput.value : '';

            const payload = {
                descricao: document.getElementById('editDescricao').value.trim(),
                valor: parseFloat(document.getElementById('editValor').value),
                categoria: document.getElementById('editCategoria').value,
                dataDespesa: document.getElementById('editDataDespesa').value,
                paga: editPaga,
                dataPagamento: editPaga ? (editDataPagamento || document.getElementById('editDataDespesa').value) : null,
                observacoes: document.getElementById('editObservacoes').value.trim()
            };

            if (!payload.descricao || !payload.valor || !payload.categoria || !payload.dataDespesa) {
                showModalMessage('editDespesaModalMessage', '❌ Preencha os campos obrigatorios.', 'error');
                return;
            }

            try {
                const response = await fetch(`/api/despesas/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    throw new Error(`Status ${response.status}`);
                }

                showModalMessage('editDespesaModalMessage', '✅ Despesa atualizada com sucesso!', 'success');
                setTimeout(() => {
                    closeEditModalDespesa();
                    carregarDespesas();
                }, 1000);
            } catch (error) {
                console.error('Erro ao atualizar despesa:', error);
                showModalMessage('editDespesaModalMessage', '❌ Erro ao atualizar despesa.', 'error');
            }
        });
    }

    document.addEventListener('click', (e) => {
        const modal = document.getElementById('editModalDespesa');
        if (modal && e.target === modal) {
            closeEditModalDespesa();
        }
    });
});

async function carregarDespesas() {
    const tbody = document.getElementById('despesasBody');
    const emptyState = document.getElementById('emptyState');
    const tableWrapper = document.querySelector('.table-wrapper');

    if (!tbody) return;

    try {
        const response = await fetch('/api/despesas', { credentials: 'include' });
        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        const despesas = await response.json();

        if (!despesas || despesas.length === 0) {
            tbody.innerHTML = '';
            if (emptyState) emptyState.style.display = 'block';
            if (tableWrapper) tableWrapper.style.display = 'none';
        } else {
            if (emptyState) emptyState.style.display = 'none';
            if (tableWrapper) tableWrapper.style.display = 'block';
            tbody.innerHTML = despesas.map(d => {
                const statusClass = d.paga ? 'success' : 'warning';
                const statusText = d.paga ? 'Paga' : 'Pendente';

                return `
                <tr class="table__body-row">
                    <td class="table__body-cell">${d.descricao || 'N/A'}</td>
                    <td class="table__body-cell">R$ ${d.valor.toFixed(2)}</td>
                    <td class="table__body-cell">${formatarCategoria(d.categoria)}</td>
                    <td class="table__body-cell">${formatarData(d.dataDespesa)}</td>
                    <td class="table__body-cell">
                        <span class="badge badge--${statusClass}">${statusText}</span>
                    </td>
                    <td class="table__body-cell">
                        <button onclick="editarDespesa(${d.id})" class="btn btn--secondary btn--small">Editar</button>
                        ${!d.paga ? `<button onclick="marcarDespesaComoPaga(${d.id})" class="btn btn--primary btn--small">Pagar</button>` : ''}
                        <button onclick="removerDespesa(${d.id})" class="btn btn--danger btn--small">Remover</button>
                    </td>
                </tr>
            `;
            }).join('');
        }
    } catch (error) {
        console.error('Erro ao carregar despesas:', error);
        if (tbody) tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">Erro ao carregar despesas</td></tr>';
    }
}

async function editarDespesa(id) {
    try {
        const response = await fetch(`/api/despesas/${id}`, { credentials: 'include' });
        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        const despesa = await response.json();

        document.getElementById('editDespesaId').value = despesa.id;
        document.getElementById('editDescricao').value = despesa.descricao || '';
        document.getElementById('editValor').value = despesa.valor || 0;
        document.getElementById('editCategoria').value = despesa.categoria || '';
        document.getElementById('editDataDespesa').value = despesa.dataDespesa || '';
        document.getElementById('editPaga').checked = !!despesa.paga;
        document.getElementById('editDataPagamento').value = despesa.dataPagamento || '';
        document.getElementById('editObservacoes').value = despesa.observacoes || '';

        const editDataPagamentoInput = document.getElementById('editDataPagamento');
        if (editDataPagamentoInput) {
            editDataPagamentoInput.disabled = !despesa.paga;
        }

        const modal = document.getElementById('editModalDespesa');
        if (modal) {
            modal.style.display = 'block';
            document.body.style.overflow = 'hidden';
        }
    } catch (error) {
        console.error('Erro ao buscar despesa:', error);
        alert('Erro ao carregar despesa para edicao');
    }
}

function closeEditModalDespesa() {
    const modal = document.getElementById('editModalDespesa');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

async function marcarDespesaComoPaga(id) {
    if (!confirm('Confirmar o pagamento desta despesa?')) {
        return;
    }

    try {
        const response = await fetch(`/api/despesas/${id}/pagar`, {
            method: 'PUT',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        alert('✅ Despesa marcada como paga!');
        carregarDespesas();
    } catch (error) {
        console.error('Erro ao marcar como paga:', error);
        alert('❌ Erro ao marcar despesa como paga');
    }
}

async function removerDespesa(id) {
    if (!confirm('Tem certeza que deseja remover esta despesa?')) {
        return;
    }

    try {
        const response = await fetch(`/api/despesas/${id}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error(`Status ${response.status}`);
        }

        alert('✅ Despesa removida com sucesso!');
        carregarDespesas();
    } catch (error) {
        console.error('Erro ao remover despesa:', error);
        alert('❌ Erro ao remover despesa');
    }
}

function formatarData(dataStr) {
    if (!dataStr) return 'N/A';
    const [ano, mes, dia] = dataStr.split('-');
    return `${dia}/${mes}/${ano}`;
}

function formatarCategoria(categoria) {
    if (!categoria) return 'N/A';
    return categoria
        .toLowerCase()
        .replace('_', ' ')
        .replace(/(^|\s)\S/g, (l) => l.toUpperCase());
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
