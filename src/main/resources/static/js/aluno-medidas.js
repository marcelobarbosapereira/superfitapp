/* ===== SCRIPT DE MEDIDAS DO ALUNO ===== */

const meses = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

let todasAsMedidas = [];

document.addEventListener('DOMContentLoaded', () => {
    inicializarSelectors();
    carregarTodasAsMedidas();
});

/**
 * Inicializa os seletores de mês e ano
 */
function inicializarSelectors() {
    const dataHoje = new Date();
    const mesAtual = dataHoje.getMonth();
    const anoAtual = dataHoje.getFullYear();

    // Preenche seletor de mês
    const selectMes = document.getElementById('mesAtual');
    for (let i = 0; i < 12; i++) {
        const option = document.createElement('option');
        option.value = i;
        option.textContent = meses[i];
        if (i === mesAtual) {
            option.selected = true;
        }
        selectMes.appendChild(option);
    }

    // Preenche seletor de ano
    const selectAno = document.getElementById('ano');
    for (let i = anoAtual; i >= anoAtual - 5; i--) {
        const option = document.createElement('option');
        option.value = i;
        option.textContent = i;
        if (i === anoAtual) {
            option.selected = true;
        }
        selectAno.appendChild(option);
    }
}

/**
 * Carrega todas as medidas do aluno
 */
function carregarTodasAsMedidas() {
    fetch('/api/medidas/meu-historico')
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar medidas');
            return response.json();
        })
        .then(medidas => {
            todasAsMedidas = medidas || [];
            atualizarPeriodosAnteriores();
            carregarMedidas();
        })
        .catch(error => {
            console.error('Erro:', error);
            showMessage('', '❌ Erro ao carregar medidas', 'error');
        });
}

/**
 * Atualiza a lista de períodos anteriores com base nas medidas disponíveis
 */
function atualizarPeriodosAnteriores() {
    const periodosUnicos = new Set();

    todasAsMedidas.forEach(medida => {
        const data = new Date(medida.data);
        const mes = data.getMonth();
        const ano = data.getFullYear();
        const chave = `${ano}-${mes}`;
        periodosUnicos.add(chave);
    });

    // Ordena os períodos em ordem decrescente
    const periodosOrdenados = Array.from(periodosUnicos).sort().reverse();

    const container = document.getElementById('periodosAnteriores');
    container.innerHTML = '';

    periodosOrdenados.forEach(chave => {
        const [ano, mes] = chave.split('-').map(Number);
        const mesNome = meses[mes];
        const anoNumero = parseInt(ano);

        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'btn btn--secondary btn--sm periodo-btn';
        button.textContent = `${mesNome} ${anoNumero}`;
        button.onclick = () => {
            document.getElementById('mesAtual').value = mes;
            document.getElementById('ano').value = anoNumero;
            carregarMedidas();
        };

        container.appendChild(button);
    });
}

/**
 * Carrega as medidas do mês/ano selecionado
 */
function carregarMedidas() {
    const mes = parseInt(document.getElementById('mesAtual').value);
    const ano = parseInt(document.getElementById('ano').value);

    if (isNaN(mes) || isNaN(ano)) {
        document.getElementById('mensagemSelecao').style.display = 'block';
        document.getElementById('medidasContent').style.display = 'none';
        return;
    }

    // Filtra medidas do mês/ano selecionado
    const medidasFiltradas = todasAsMedidas.filter(medida => {
        const data = new Date(medida.data);
        return data.getMonth() === mes && data.getFullYear() === ano;
    });

    if (medidasFiltradas.length === 0) {
        document.getElementById('mensagemSelecao').style.display = 'block';
        document.getElementById('medidasContent').style.display = 'none';
        return;
    }

    document.getElementById('mensagemSelecao').style.display = 'none';
    document.getElementById('medidasContent').style.display = 'block';

    // Atualiza título
    document.getElementById('tituloMedidas').textContent = `Medidas de ${meses[mes]} de ${ano}`;

    // Ordena por data (mais recente primeiro)
    medidasFiltradas.sort((a, b) => new Date(b.data) - new Date(a.data));

    // Atualiza resumo com a medida mais recente
    atualizarResumo(medidasFiltradas[0]);

    // Preenche tabelas
    preencherTabelaPrincipal(medidasFiltradas);
    preencherTabelaExpandida(medidasFiltradas);
}

/**
 * Atualiza o resumo com a medida mais recente
 */
function atualizarResumo(medida) {
    document.getElementById('pesoAtual').textContent = medida.peso ? medida.peso.toFixed(1) : '-';
    document.getElementById('gorduraAtual').textContent = medida.percentualGordura ? medida.percentualGordura.toFixed(1) : '-';

    if (medida.peso && medida.altura) {
        const imc = calcularIMC(medida.peso, medida.altura);
        document.getElementById('imcAtual').textContent = imc.toFixed(1);
    } else {
        document.getElementById('imcAtual').textContent = '-';
    }
}

/**
 * Calcula IMC
 */
function calcularIMC(peso, altura) {
    const alturaMetros = altura / 100;
    return peso / (alturaMetros * alturaMetros);
}

/**
 * Preenche a tabela principal
 */
function preencherTabelaPrincipal(medidas) {
    const tbody = document.getElementById('medidasBody');
    const emptyState = document.getElementById('emptyState');
    const table = document.getElementById('medidasTable');

    if (!medidas || medidas.length === 0) {
        tbody.innerHTML = '';
        emptyState.classList.remove('d-none');
        table.style.display = 'none';
        return;
    }

    emptyState.classList.add('d-none');
    table.style.display = 'table';

    tbody.innerHTML = medidas.map(medida => `
        <tr class="table__body-row">
            <td class="table__body-cell">${formatarData(medida.data)}</td>
            <td class="table__body-cell">${medida.peso ? medida.peso.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${medida.altura ? medida.altura.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${medida.percentualGordura ? medida.percentualGordura.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${medida.peito ? medida.peito.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${medida.cintura ? medida.cintura.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${medida.quadril ? medida.quadril.toFixed(1) : '-'}</td>
        </tr>
    `).join('');
}

/**
 * Preenche a tabela expandida com detalhes
 */
function preencherTabelaExpandida(medidas) {
    const tbody = document.getElementById('medidasExpandidaBody');

    tbody.innerHTML = medidas.map(medida => `
        <tr class="table__body-row">
            <td class="table__body-cell">${formatarData(medida.data)}</td>
            <td class="table__body-cell">${medida.bracoDireito ? medida.bracoDireito.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${medida.bracoEsquerdo ? medida.bracoEsquerdo.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${medida.coxaDireita ? medida.coxaDireita.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${medida.coxaEsquerda ? medida.coxaEsquerda.toFixed(1) : '-'}</td>
            <td class="table__body-cell" style="max-width: 200px; word-wrap: break-word;">${medida.observacoes || '-'}</td>
        </tr>
    `).join('');
}

/**
 * Formata uma data para formato brasileiro (DD/MM/YYYY)
 */
function formatarData(dataString) {
    if (!dataString) return '-';
    const data = new Date(dataString);
    return data.toLocaleDateString('pt-BR');
}
