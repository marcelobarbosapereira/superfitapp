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
        <div class="exercicio-item" id="exercicio-${id}" style="border: 1px solid #e0e0e0; padding: 1rem; border-radius: 8px; margin-bottom: 1rem;">
            <div class="form-row">
                <div class="form-group" style="flex: 2;">
                    <label class="form-label">Nome do Exercício</label>
                    <input type="text" name="exercicios[${id}].nome" class="form-control" 
                           placeholder="Ex: Supino reto" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Repetições</label>
                    <input type="text" name="exercicios[${id}].repeticoes" class="form-control" 
                           placeholder="Ex: 4x12" required>
                </div>
                <div class="form-group">
                    <label class="form-label">Carga</label>
                    <input type="text" name="exercicios[${id}].carga" class="form-control" 
                           placeholder="Ex: 60kg">
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Grupo Muscular</label>
                    <input type="text" name="exercicios[${id}].grupoMuscular" class="form-control" 
                           placeholder="Ex: Peito">
                </div>
                <div class="form-group">
                    <label class="form-label">Descanso</label>
                    <input type="text" name="exercicios[${id}].descansoIndicado" class="form-control" 
                           placeholder="Ex: 60s" required>
                </div>
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
        const repeticoesInput = exercicio.querySelector('input[name$=".repeticoes"]');
        const cargaInput = exercicio.querySelector('input[name$=".carga"]');
        const grupoMuscularInput = exercicio.querySelector('input[name$=".grupoMuscular"]');
        const descansoInput = exercicio.querySelector('input[name$=".descansoIndicado"]');
        
        if (nomeInput && repeticoesInput && descansoInput) {
            exerciciosList.push({
                nome: nomeInput.value,
                repeticoes: repeticoesInput.value,
                carga: cargaInput ? cargaInput.value : null,
                grupoMuscular: grupoMuscularInput ? grupoMuscularInput.value : null,
                descansoIndicado: descansoInput.value
            });
        }
    });

    const treinoData = {
        alunoId: parseInt(document.getElementById('aluno').value),
        nome: document.getElementById('nomeTreino').value,
        tipo: document.getElementById('tipo').value,
        dataInicio: document.getElementById('dataInicio').value,
        exercicios: exerciciosList
    };

    console.log('Enviando treino:', JSON.stringify(treinoData, null, 2));

    fetch('/api/treinos', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(treinoData)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                console.error('Erro do servidor:', text);
                throw new Error('Erro ao criar treino: ' + text);
            });
        }
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
                    <tr class="table__body-row" id="treino-row-${treino.id}">
                        <td class="table__body-cell">${treino.alunoNome || '-'}</td>
                        <td class="table__body-cell">${treino.nome}</td>
                        <td class="table__body-cell">${treino.tipo || '-'}</td>
                        <td class="table__body-cell">${formatarData(treino.dataInicio)}</td>
                        <td class="table__body-cell">
                            <div class="table__actions">
                                <button class="btn btn--info btn--sm" onclick="visualizarTreino(${treino.id})">Visualizar</button>
                                <button class="btn btn--primary btn--sm" onclick="editarTreino(${treino.id})">Editar</button>
                                <button class="btn btn--danger btn--sm" onclick="deletarTreino(${treino.id})">Deletar</button>
                            </div>
                        </td>
                    </tr>
                    <tr class="table__body-row" id="treino-exercicios-${treino.id}" style="display: none;">
                        <td colspan="5" style="padding: 1rem; background: #f5f5f5;">
                            <div id="exercicios-content-${treino.id}"></div>
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
 * Visualiza os exercícios de um treino
 */
function visualizarTreino(id) {
    const exerciciosRow = document.getElementById(`treino-exercicios-${id}`);
    const contentDiv = document.getElementById(`exercicios-content-${id}`);
    
    // Se já está visível, esconde
    if (exerciciosRow.style.display !== 'none') {
        exerciciosRow.style.display = 'none';
        return;
    }
    
    // Busca os exercícios do treino
    fetch(`/api/treinos/${id}`, { credentials: 'include' })
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar treino');
            return response.json();
        })
        .then(treino => {
            if (!treino.exercicios || treino.exercicios.length === 0) {
                contentDiv.innerHTML = '<p style="color: #666;">Nenhum exercício cadastrado para este treino.</p>';
            } else {
                contentDiv.innerHTML = `
                    <h4 style="margin-bottom: 1rem; color: #333;">Exercícios do Treino: ${treino.nome}</h4>
                    <table class="table" style="background: white;">
                        <thead class="table__head">
                            <tr>
                                <th class="table__header-cell">Exercício</th>
                                <th class="table__header-cell">Repetições</th>
                                <th class="table__header-cell">Carga</th>
                                <th class="table__header-cell">Grupo Muscular</th>
                                <th class="table__header-cell">Descanso</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${treino.exercicios.map(ex => `
                                <tr class="table__body-row">
                                    <td class="table__body-cell">${ex.nome}</td>
                                    <td class="table__body-cell">${ex.repeticoes || '-'}</td>
                                    <td class="table__body-cell">${ex.carga || '-'}</td>
                                    <td class="table__body-cell">${ex.grupoMuscular || '-'}</td>
                                    <td class="table__body-cell">${ex.descansoIndicado || '-'}</td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                `;
            }
            exerciciosRow.style.display = 'table-row';
        })
        .catch(error => {
            console.error('Erro:', error);
            contentDiv.innerHTML = '<p style="color: #d32f2f;">❌ Erro ao carregar exercícios</p>';
            exerciciosRow.style.display = 'table-row';
        });
}

/**
 * Edita um treino
 */
function editarTreino(id) {
    // TODO: Implementar página de edição de treino
    alert('⚠️ Funcionalidade de edição em desenvolvimento. Por enquanto, você pode deletar e criar um novo treino.');
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
