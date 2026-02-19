/* ===== SCRIPT DE TREINOS DO ALUNO ===== */

let todosTreinos = [];
let treinoSelecionado = null;

document.addEventListener('DOMContentLoaded', () => {
    carregarTreinos();
});

/**
 * Carrega todos os treinos do aluno
 */
function carregarTreinos() {
    fetch('/api/alunos/treinos')
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar treinos');
            return response.json();
        })
        .then(treinos => {
            todosTreinos = treinos || [];
            
            if (todosTreinos.length === 0) {
                document.getElementById('mensagemSelecao').innerHTML = '<p>❌ Nenhum treino encontrado</p>';
                document.getElementById('treinosContent').style.display = 'none';
                return;
            }

            atualizarPeriodosAnteriores();
            filtrarTreinos();
        })
        .catch(error => {
            console.error('Erro:', error);
            document.getElementById('mensagemSelecao').innerHTML = '<p>❌ Erro ao carregar treinos</p>';
        });
}

/**
 * Atualiza a lista de períodos anteriores
 */
function atualizarPeriodosAnteriores() {
    const periodosUnicos = new Map();

    todosTreinos.forEach(treino => {
        const dataInicio = new Date(treino.dataInicio);
        const mes = dataInicio.getMonth();
        const ano = dataInicio.getFullYear();
        const chave = `${ano}-${mes}`;
        
        if (!periodosUnicos.has(chave)) {
            periodosUnicos.set(chave, { ano, mes });
        }
    });

    // Ordena em ordem decrescente
    const periodosOrdenados = Array.from(periodosUnicos.values())
        .sort((a, b) => b.ano - a.ano || b.mes - a.mes);

    const container = document.getElementById('periodosAnteriores');
    container.innerHTML = '';

    const meses = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
                   'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'];

    periodosOrdenados.forEach(periodo => {
        const mesNome = meses[periodo.mes];
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'btn btn--secondary btn--sm periodo-btn';
        button.textContent = `${mesNome} ${periodo.ano}`;
        button.onclick = () => filtrarPorPeriodo(periodo.ano, periodo.mes);
        container.appendChild(button);
    });
}

/**
 * Filtra treinos para o período selecionado
 */
function filtrarPorPeriodo(ano, mes) {
    const treinosFiltrados = todosTreinos.filter(treino => {
        const data = new Date(treino.dataInicio);
        return data.getMonth() === mes && data.getFullYear() === ano;
    });

    document.getElementById('mensagemSelecao').style.display = 'none';
    document.getElementById('treinosContent').style.display = 'block';
    document.getElementById('treinoAtivoContainer').style.display = 'none';
    document.getElementById('treinoDetalheContainer').style.display = 'none';

    const meses = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
                   'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'];
    document.getElementById('tituloTreinos').textContent = `Treinos de ${meses[mes]} de ${ano}`;

    preencherTabelaTreinos(treinosFiltrados);
}

/**
 * Filtra treinos baseado no filtro selecionado
 */
function filtrarTreinos() {
    const filtro = document.getElementById('periodoSelect').value;
    const hoje = new Date();
    const treinosFiltrados = [];

    if (filtro === 'valido') {
        // Mostrar apenas treinos válidos (data fim >= hoje)
        const treinosValidos = todosTreinos.filter(treino => {
            const dataFim = calcularDataFim(treino.dataInicio);
            return dataFim >= hoje;
        });

        document.getElementById('mensagemSelecao').style.display = 'none';
        document.getElementById('treinosContent').style.display = 'block';
        document.getElementById('tituloTreinos').textContent = 'Treinos Válidos';
        document.getElementById('treinoDetalheContainer').style.display = 'none';

        // Mostra o treino mais recente como "ativo"
        if (treinosValidos.length > 0) {
            const treinoAtivo = treinosValidos[0];
            exibirTreinoAtivo(treinoAtivo);
        } else {
            document.getElementById('treinoAtivoContainer').style.display = 'none';
        }

        preencherTabelaTreinos(treinosValidos);
    } else if (filtro === 'todos') {
        document.getElementById('mensagemSelecao').style.display = 'none';
        document.getElementById('treinosContent').style.display = 'block';
        document.getElementById('tituloTreinos').textContent = 'Todos os Treinos';
        document.getElementById('treinoAtivoContainer').style.display = 'none';
        document.getElementById('treinoDetalheContainer').style.display = 'none';
        preencherTabelaTreinos(todosTreinos);
    }
}

/**
 * Exibe o treino ativo em destaque
 */
function exibirTreinoAtivo(treino) {
    document.getElementById('treinoAtivoContainer').style.display = 'block';
    
    const dataFim = calcularDataFim(treino.dataInicio);
    const diasRestantes = Math.floor((dataFim - new Date()) / (1000 * 60 * 60 * 24));

    const card = `
        <div class="treino-info">
            <h4>${treino.nome}</h4>
            <p><strong>Tipo:</strong> ${treino.tipo || 'N/A'}</p>
            <p><strong>Data Início:</strong> ${formatarData(treino.dataInicio)}</p>
            <p><strong>Válido Até:</strong> ${formatarData(dataFim)}</p>
            <p><strong>Dias Restantes:</strong> <span class="dias-restantes">${diasRestantes > 0 ? diasRestantes : 'Expirado'}</span></p>
            <p><strong>Descrição:</strong> ${treino.descricao || 'Sem descrição'}</p>
            <button type="button" class="btn btn--primary" onclick="exibirDetalhes(${treino.id})">
                Ver Detalhes
            </button>
        </div>
    `;

    document.getElementById('treinoAtivoCard').innerHTML = card;
}

/**
 * Preenche a tabela de treinos
 */
function preencherTabelaTreinos(treinos) {
    const tbody = document.getElementById('treinosBody');
    const emptyState = document.getElementById('emptyState');
    const table = document.getElementById('treinosTable');
    const hoje = new Date();

    if (!treinos || treinos.length === 0) {
        tbody.innerHTML = '';
        emptyState.classList.remove('d-none');
        table.style.display = 'none';
        return;
    }

    emptyState.classList.add('d-none');
    table.style.display = 'table';

    tbody.innerHTML = treinos.map(treino => {
        const dataFim = calcularDataFim(treino.dataInicio);
        const isValido = dataFim >= hoje;
        const statusClass = isValido ? 'status-valido' : 'status-expirado';
        const statusText = isValido ? '✅ Ativo' : '❌ Expirado';
        const diasRestantes = Math.floor((dataFim - hoje) / (1000 * 60 * 60 * 24));

        return `
            <tr class="table__body-row">
                <td class="table__body-cell"><strong>${treino.nome}</strong></td>
                <td class="table__body-cell">${treino.tipo || '-'}</td>
                <td class="table__body-cell">${formatarData(treino.dataInicio)}</td>
                <td class="table__body-cell">${formatarData(dataFim)}</td>
                <td class="table__body-cell ${statusClass}">${statusText}</td>
                <td class="table__body-cell">
                    <button class="btn btn--primary btn--sm" onclick="exibirDetalhes(${treino.id})">
                        Detalhes
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Exibe os detalhes do treino selecionado
 */
function exibirDetalhes(treinoId) {
    const treino = todosTreinos.find(t => t.id === treinoId);
    if (!treino) return;

    treinoSelecionado = treino;
    document.getElementById('treinoDetalheContainer').style.display = 'block';
    document.getElementById('treinoDetalheTitulo').textContent = `Detalhes - ${treino.nome}`;
    document.getElementById('treinoDetalheDescricao').textContent = treino.descricao || 'Sem descrição';

    // Exibe exercícios
    const exerciciosHtml = treino.exercicios && treino.exercicios.length > 0
        ? treino.exercicios.map((ex, idx) => `
            <div class="exercicio-item">
                <h5>${idx + 1}. ${ex.nome || 'Exercício'}</h5>
                <p><strong>Séries x Reps:</strong> ${ex.seriesReps || '-'}</p>
                <p><strong>Descanso:</strong> ${ex.descanso || '-'}</p>
            </div>
        `).join('')
        : '<p>Nenhum exercício cadastrado</p>';

    document.getElementById('exerciciosLista').innerHTML = exerciciosHtml;

    // Scroll para o detalhe
    document.getElementById('treinoDetalheContainer').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

/**
 * Fecha a visualização de detalhes
 */
function fecharDetalhes() {
    document.getElementById('treinoDetalheContainer').style.display = 'none';
    treinoSelecionado = null;
}

/**
 * Calcula a data de fim do treino (30 dias após data de início)
 */
function calcularDataFim(dataInicio) {
    const data = new Date(dataInicio);
    data.setDate(data.getDate() + 30);
    return data;
}

/**
 * Formata uma data para formato brasileiro (DD/MM/YYYY)
 */
function formatarData(dataString) {
    if (!dataString) return '-';
    const data = new Date(dataString);
    return data.toLocaleDateString('pt-BR');
}
