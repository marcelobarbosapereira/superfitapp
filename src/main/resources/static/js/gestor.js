/* ===== SCRIPT DO GESTOR ===== */

document.addEventListener('DOMContentLoaded', () => {
    loadEstatisticas();
    loadAlunosRecentes();
    loadMensalidadesPendentes();
});

/**
 * Carrega estatísticas principais
 */
function loadEstatisticas() {
    // Total de alunos
    fetch('/api/gestor/alunos/total')
        .then(response => response.ok ? response.json() : Promise.reject('Erro'))
        .then(data => {
            document.getElementById('totalAlunos').textContent = data || '0';
        })
        .catch(error => {
            console.error('Erro ao carregar alunos:', error);
            document.getElementById('totalAlunos').textContent = '0';
        });

    // Total de professores
    fetch('/api/gestor/professores/total')
        .then(response => response.ok ? response.json() : Promise.reject('Erro'))
        .then(data => {
            document.getElementById('totalProfessores').textContent = data || '0';
        })
        .catch(error => {
            console.error('Erro ao carregar professores:', error);
            document.getElementById('totalProfessores').textContent = '0';
        });

    // Mensalidades pendentes
    fetch('/api/gestor/mensalidades/pendentes/count')
        .then(response => response.ok ? response.json() : Promise.reject('Erro'))
        .then(data => {
            document.getElementById('mensalidadesPendentes').textContent = data || '0';
        })
        .catch(error => {
            console.error('Erro ao carregar mensalidades:', error);
            document.getElementById('mensalidadesPendentes').textContent = '0';
        });

    // Receita do mês
    fetch('/api/gestor/receita/mes')
        .then(response => response.ok ? response.json() : Promise.reject('Erro'))
        .then(data => {
            document.getElementById('receitaMes').textContent = formatarMoeda(data) || 'R$ 0,00';
        })
        .catch(error => {
            console.error('Erro ao carregar receita:', error);
            document.getElementById('receitaMes').textContent = 'R$ 0,00';
        });
}

/**
 * Carrega alunos recentes
 */
function loadAlunosRecentes() {
    fetch('/api/gestor/alunos/recentes')
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar');
            return response.json();
        })
        .then(alunos => {
            const tbody = document.getElementById('alunosBody');
            const emptyState = document.getElementById('emptyState');
            const table = document.getElementById('alunosTable');

            if (!alunos || alunos.length === 0) {
                tbody.innerHTML = '';
                emptyState.classList.remove('d-none');
                table.style.display = 'none';
            } else {
                emptyState.classList.add('d-none');
                table.style.display = 'table';
                tbody.innerHTML = alunos.map(aluno => `
                    <tr class="table__body-row">
                        <td class="table__body-cell">${aluno.nome}</td>
                        <td class="table__body-cell">${aluno.email}</td>
                        <td class="table__body-cell">
                            <span class="badge badge--${aluno.ativo ? 'success' : 'danger'}">
                                ${aluno.ativo ? 'Ativo' : 'Inativo'}
                            </span>
                        </td>
                        <td class="table__body-cell">${aluno.statusMensalidade}</td>
                        <td class="table__body-cell">
                            <div class="table__actions">
                                <a href="/gestor/alunos/${aluno.id}" class="btn btn--info btn--sm">Ver</a>
                                <button class="btn btn--primary btn--sm" onclick="editarAluno(${aluno.id})">Editar</button>
                            </div>
                        </td>
                    </tr>
                `).join('');
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            showMessage('alunosMessage', '❌ Erro ao carregar alunos', 'error');
        });
}

/**
 * Carrega mensalidades pendentes
 */
function loadMensalidadesPendentes() {
    fetch('/api/gestor/mensalidades/pendentes')
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar');
            return response.json();
        })
        .then(mensalidades => {
            const tbody = document.getElementById('mensalidadesBody');
            const emptyState = document.getElementById('emptyMensalidades');
            const table = document.getElementById('mensalidadesTable');

            if (!mensalidades || mensalidades.length === 0) {
                tbody.innerHTML = '';
                emptyState.classList.remove('d-none');
                table.style.display = 'none';
            } else {
                emptyState.classList.add('d-none');
                table.style.display = 'table';
                tbody.innerHTML = mensalidades.map(m => `
                    <tr class="table__body-row">
                        <td class="table__body-cell">${m.alunoNome}</td>
                        <td class="table__body-cell">${formatarMoeda(m.valor)}</td>
                        <td class="table__body-cell">${formatarData(m.vencimento)}</td>
                        <td class="table__body-cell">
                            <span class="badge badge--${m.atrasada ? 'danger' : 'warning'}">
                                ${m.atrasada ? 'Atrasada' : 'Pendente'}
                            </span>
                        </td>
                        <td class="table__body-cell">
                            <div class="table__actions">
                                <button class="btn btn--success btn--sm" onclick="registrarPagamento(${m.id})">Pagar</button>
                                <button class="btn btn--info btn--sm" onclick="enviarCobranca(${m.id})">Cobrar</button>
                            </div>
                        </td>
                    </tr>
                `).join('');
            }
        })
        .catch(error => {
            console.error('Erro:', error);
        });
}

/**
 * Formata valor para moeda
 */
function formatarMoeda(valor) {
    if (!valor) return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor);
}

/**
 * Edita aluno
 */
function editarAluno(id) {
    window.location.href = `/gestor/alunos/${id}/editar`;
}

/**
 * Registra pagamento
 */
function registrarPagamento(id) {
    if (confirmAction('Deseja registrar o pagamento desta mensalidade?')) {
        // Chamada à API para registrar pagamento
        fetch(`/api/gestor/mensalidades/${id}/pagar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
        .then(response => response.ok ? response.json() : Promise.reject('Erro'))
        .then(() => {
            showMessage('alunosMessage', '✅ Pagamento registrado!', 'success', 2000);
            loadMensalidadesPendentes();
            loadEstatisticas();
        })
        .catch(error => {
            console.error('Erro:', error);
            showMessage('alunosMessage', '❌ Erro ao registrar pagamento', 'error');
        });
    }
}

/**
 * Envia cobrança ao aluno
 */
function enviarCobranca(id) {
    if (confirmAction('Deseja enviar uma cobrança ao aluno?')) {
        fetch(`/api/gestor/mensalidades/${id}/cobrar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
        .then(response => response.ok ? response.json() : Promise.reject('Erro'))
        .then(() => {
            showMessage('alunosMessage', '✅ Cobrança enviada ao aluno!', 'success', 2000);
        })
        .catch(error => {
            console.error('Erro:', error);
            showMessage('alunosMessage', '❌ Erro ao enviar cobrança', 'error');
        });
    }
}
