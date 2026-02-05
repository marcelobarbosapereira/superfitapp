/* ===== SCRIPT DO PROFESSOR ===== */

document.addEventListener('DOMContentLoaded', () => {
    loadEstatisticas();
    loadAlunosRecentes();
});

/**
 * Carrega estatísticas
 */
function loadEstatisticas() {
    // Total de alunos
    fetch('/api/professor/alunos/total')
        .then(response => response.ok ? response.json() : Promise.reject('Erro'))
        .then(data => {
            document.getElementById('totalAlunos').textContent = data || '0';
        })
        .catch(error => {
            console.error('Erro ao carregar alunos:', error);
            document.getElementById('totalAlunos').textContent = '0';
        });

    // Total de treinos
    fetch('/api/professor/treinos/total')
        .then(response => response.ok ? response.json() : Promise.reject('Erro'))
        .then(data => {
            document.getElementById('totalTreinos').textContent = data || '0';
        })
        .catch(error => {
            console.error('Erro ao carregar treinos:', error);
            document.getElementById('totalTreinos').textContent = '0';
        });

    // Últimas medidas
    fetch('/api/professor/medidas/recentes/count')
        .then(response => response.ok ? response.json() : Promise.reject('Erro'))
        .then(data => {
            document.getElementById('ultimasMedidas').textContent = data || '0';
        })
        .catch(error => {
            console.error('Erro ao carregar medidas:', error);
            document.getElementById('ultimasMedidas').textContent = '0';
        });
}

/**
 * Carrega alunos recentes
 */
function loadAlunosRecentes() {
    fetch('/api/professor/alunos/recentes')
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
                        <td class="table__body-cell" th:text="${aluno.nome}">${aluno.nome}</td>
                        <td class="table__body-cell">${aluno.email}</td>
                        <td class="table__body-cell">${aluno.ultimoTreino || 'Sem treino'}</td>
                        <td class="table__body-cell">
                            <div class="table__actions">
                                <a href="/professor/alunos/${aluno.id}" class="btn btn--info btn--sm">Visualizar</a>
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
 * Edita aluno
 */
function editarAluno(id) {
    window.location.href = `/professor/alunos/${id}/editar`;
}
