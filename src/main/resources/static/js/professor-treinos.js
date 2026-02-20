/* ===== SCRIPT DO GERENCIAMENTO DE TREINOS DO PROFESSOR ===== */

let contadorExercicios = 0;

document.addEventListener('DOMContentLoaded', () => {
    loadAlunos();
    loadTreinosRecentes();
    adicionarExercicio();

    // Submeter formulário
    document.getElementById('formCriarTreino').addEventListener('submit', (e) => {
        e.preventDefault();
        criarTreino();
    });

    // Data de início padrão (hoje)
    const dataInicio = new Date();
    document.getElementById('dataInicio').valueAsDate = dataInicio;
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
            showMessage('alunosMessage', '❌ Erro ao carregar alunos', 'error');
        });
}

/**
 * Adiciona um novo exercício ao formulário
 */
function adicionarExercicio() {
    const container = document.getElementById('exerciciosContainer');
    const id = contadorExercicios++;
    
    const exercicioHtml = `
        <div class="exercicio-item" id="exercicio-${id}">
            <div class="form-row">
                <div class="form-group" style="flex: 2;">
                    <label class="form-label">Exercício</label>
                    <input type="text" name="exercicios[${id}].nome" class="form-control" 
                           placeholder="Ex: Supino reto" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Séries x Reps</label>
                    <input type="text" name="exercicios[${id}].seriesReps" class="form-control" 
                           placeholder="Ex: 4x12" required>
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Descanso</label>
                <input type="text" name="exercicios[${id}].descanso" class="form-control" 
                       placeholder="Ex: 60s" required>
            </div>
            <button type="button" class="btn btn--danger btn--sm" onclick="removerExercicio(${id})">
                Remover Exercício
            </button>
        </div>
    `;
    
    container.insertAdjacentHTML('beforeend', exercicioHtml);
}

/**
 * Remove um exercício do formulário
 */
function removerExercicio(id) {
    const elemento = document.getElementById(`exercicio-${id}`);
    if (elemento) {
        elemento.remove();
    }
}

/**
 * Cria um novo treino
 */
function criarTreino() {
    const formData = new FormData(document.getElementById('formCriarTreino'));
    
    // Verifica se há pelo menos um exercício
    const exercicios = document.querySelectorAll('.exercicio-item');
    if (exercicios.length === 0) {
        showMessage('treinosMessage', '❌ Adicione pelo menos um exercício', 'error');
        return;
    }

    // Monta objeto de exercícios
    const exerciciosList = [];
    exercicios.forEach((exercicio, index) => {
        const nomeInput = exercicio.querySelector('input[name$=".nome"]');
        const seriesRepsInput = exercicio.querySelector('input[name$=".seriesReps"]');
        const descansoInput = exercicio.querySelector('input[name$=".descanso"]');
        
        if (nomeInput && seriesRepsInput && descansoInput) {
            exerciciosList.push({
                nome: nomeInput.value,
                seriesReps: seriesRepsInput.value,
                descanso: descansoInput.value
            });
        }
    });

    const treinoData = {
        alunoId: parseInt(document.getElementById('aluno').value),
        nome: document.getElementById('nomeTreino').value,
        descricao: document.getElementById('descricao').value,
        tipo: document.getElementById('tipo').value,
        dataInicio: document.getElementById('dataInicio').value,
        exercicios: exerciciosList
    };

    fetch('/api/treinos', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(treinoData)
    })
    .then(response => {
        if (!response.ok) throw new Error('Erro ao criar treino');
        return response.json();
    })
    .then(data => {
        showMessage('treinosMessage', '✅ Treino criado com sucesso!', 'success');
        document.getElementById('formCriarTreino').reset();
        
        // Reseta exercícios
        document.getElementById('exerciciosContainer').innerHTML = '';
        contadorExercicios = 0;
        adicionarExercicio();
        
        // Recarrega treinos recentes
        loadTreinosRecentes();
    })
    .catch(error => {
        console.error('Erro:', error);
        showMessage('treinosMessage', '❌ Erro ao criar treino', 'error');
    });
}

/**
 * Carrega treinos recentes do professor
 */
function loadTreinosRecentes() {
    fetch('/api/treinos/professor/recentes')
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar treinos');
            return response.json();
        })
        .then(treinos => {
            const tbody = document.getElementById('treinosBody');
            const emptyState = document.getElementById('emptyState');
            const table = document.getElementById('treinosTable');

            if (!treinos || treinos.length === 0) {
                tbody.innerHTML = '';
                emptyState.classList.remove('d-none');
                table.style.display = 'none';
            } else {
                emptyState.classList.add('d-none');
                table.style.display = 'table';
                tbody.innerHTML = treinos.map(treino => `
                    <tr class="table__body-row">
                        <td class="table__body-cell">${treino.alunoNome || '-'}</td>
                        <td class="table__body-cell">${treino.nome}</td>
                        <td class="table__body-cell">${treino.tipo || '-'}</td>
                        <td class="table__body-cell">${formatarData(treino.dataInicio)}</td>
                        <td class="table__body-cell">
                            <div class="table__actions">
                                <a href="/professor/treinos/${treino.id}" class="btn btn--info btn--sm">Visualizar</a>
                                <button class="btn btn--primary btn--sm" onclick="editarTreino(${treino.id})">Editar</button>
                                <button class="btn btn--danger btn--sm" onclick="deletarTreino(${treino.id})">Deletar</button>
                            </div>
                        </td>
                    </tr>
                `).join('');
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            showMessage('treinosMessage', '❌ Erro ao carregar treinos', 'error');
        });
}

/**
 * Edita um treino
 */
function editarTreino(id) {
    window.location.href = `/professor/treinos/${id}/editar`;
}

/**
 * Deleta um treino
 */
function deletarTreino(id) {
    if (!confirm('Tem certeza que deseja deletar este treino?')) return;

    fetch(`/api/treinos/${id}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) throw new Error('Erro ao deletar treino');
        showMessage('treinosMessage', '✅ Treino deletado com sucesso!', 'success');
        loadTreinosRecentes();
    })
    .catch(error => {
        console.error('Erro:', error);
        showMessage('treinosMessage', '❌ Erro ao deletar treino', 'error');
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
