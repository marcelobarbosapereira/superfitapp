/* ===== SCRIPT DO REGISTRO DE MEDIDAS DO PROFESSOR ===== */

document.addEventListener('DOMContentLoaded', () => {
    loadAlunos();
    loadMedidasRecentes();

    // Submeter formulário
    document.getElementById('formRegistroMedidas').addEventListener('submit', (e) => {
        e.preventDefault();
        registrarMedidas();
    });

    // Data padrão (hoje)
    const data = new Date();
    document.getElementById('data').valueAsDate = data;
});

/**
 * Carrega lista de alunos do professor
 */
function loadAlunos() {
    fetch('/api/professores/alunos', { credentials: 'include' })
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar alunos');
            return response.json();
        })
        .then(alunos => {
            const select = document.getElementById('aluno');
            select.innerHTML = '<option value="">Selecione um aluno</option>';
            
            alunos.forEach(aluno => {
                const option = document.createElement('option');
                option.value = aluno.id;
                option.textContent = aluno.nome;
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Erro ao carregar alunos:', error);
            showMessage('medidaMessage', '❌ Erro ao carregar alunos', 'error');
        });
}

/**
 * Registra novas medidas de um aluno
 */
function registrarMedidas() {
    const alunoId = document.getElementById('aluno').value;
    
    if (!alunoId) {
        showMessage('medidaMessage', '❌ Selecione um aluno', 'error');
        return;
    }

    const medidaData = {
        alunoId: parseInt(alunoId),
        data: document.getElementById('data').value,
        peso: parseFloat(document.getElementById('peso').value) || 0,
        altura: parseFloat(document.getElementById('altura').value) || 0,
        percentualGordura: parseFloat(document.getElementById('percentualGordura').value) || 0,
        peito: parseFloat(document.getElementById('peito').value) || 0,
        cintura: parseFloat(document.getElementById('cintura').value) || 0,
        quadril: parseFloat(document.getElementById('quadril').value) || 0,
        bracoDireito: parseFloat(document.getElementById('bracoDireito').value) || 0,
        bracoEsquerdo: parseFloat(document.getElementById('bracoEsquerdo').value) || 0,
        coxaDireita: parseFloat(document.getElementById('coxaDireita').value) || 0,
        coxaEsquerda: parseFloat(document.getElementById('coxaEsquerda').value) || 0,
        observacoes: document.getElementById('observacoes').value
    };

    fetch('/api/medidas', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(medidaData)
    })
    .then(response => {
        if (!response.ok) throw new Error('Erro ao registrar medidas');
        return response.json();
    })
    .then(data => {
        showMessage('medidaMessage', '✅ Medidas registradas com sucesso!', 'success');
        
        // Reseta o formulário
        document.getElementById('formRegistroMedidas').reset();
        
        // Reseta a data para hoje
        const dataHoje = new Date();
        document.getElementById('data').valueAsDate = dataHoje;
        
        // Recarrega a tabela de medidas
        loadMedidasRecentes();
    })
    .catch(error => {
        console.error('Erro:', error);
        showMessage('medidaMessage', '❌ Erro ao registrar medidas', 'error');
    });
}

/**
 * Carrega as medidas recentes registradas pelo professor
 */
function loadMedidasRecentes() {
    fetch('/api/medidas/professor/recentes')
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar medidas');
            return response.json();
        })
        .then(medidas => {
            const tbody = document.getElementById('medidasBody');
            const emptyState = document.getElementById('emptyState');
            const table = document.getElementById('medidasTable');

            if (!medidas || medidas.length === 0) {
                tbody.innerHTML = '';
                emptyState.classList.remove('d-none');
                table.style.display = 'none';
            } else {
                emptyState.classList.add('d-none');
                table.style.display = 'table';
                tbody.innerHTML = medidas.map(medida => `
                    <tr class="table__body-row">
                        <td class="table__body-cell">${medida.alunoNome || '-'}</td>
                        <td class="table__body-cell">${formatarData(medida.data)}</td>
                        <td class="table__body-cell">${medida.peso ? medida.peso.toFixed(1) : '-'}</td>
                        <td class="table__body-cell">${medida.percentualGordura ? medida.percentualGordura.toFixed(1) : '-'}</td>
                        <td class="table__body-cell">
                            <div class="table__actions">
                                <a href="/professor/medidas/${medida.id}" class="btn btn--info btn--sm">Visualizar</a>
                                <button class="btn btn--primary btn--sm" onclick="editarMedida(${medida.id})">Editar</button>
                                <button class="btn btn--danger btn--sm" onclick="deletarMedida(${medida.id})">Deletar</button>
                            </div>
                        </td>
                    </tr>
                `).join('');
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            showMessage('medidaMessage', '❌ Erro ao carregar medidas', 'error');
        });
}

/**
 * Edita uma medida registrada
 */
function editarMedida(id) {
    window.location.href = `/professor/medidas/${id}/editar`;
}

/**
 * Deleta uma medida registrada
 */
function deletarMedida(id) {
    if (!confirm('Tem certeza que deseja deletar este registro de medidas?')) return;

    fetch(`/api/medidas/${id}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) throw new Error('Erro ao deletar medida');
        showMessage('medidaMessage', '✅ Medida deletada com sucesso!', 'success');
        loadMedidasRecentes();
    })
    .catch(error => {
        console.error('Erro:', error);
        showMessage('medidaMessage', '❌ Erro ao deletar medida', 'error');
    });
}

/**
 * Formata uma data para formato brasileiro (DD/MM/YYYY)
 */
function formatarData(dataString) {
    if (!dataString) return '-';
    const data = new Date(dataString);
    return data.toLocaleDateString('pt-BR');
}
