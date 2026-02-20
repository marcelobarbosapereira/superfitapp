/* ===== SCRIPT DE MEDIDAS DO ALUNO ===== */

const meses = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

let todasAsMedidas = [];

document.addEventListener('DOMContentLoaded', () => {
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
            
            // Ordena por data (mais recente primeiro)
            todasAsMedidas.sort((a, b) => new Date(b.data) - new Date(a.data));
            
            // Exibe medida mais recente e últimas 3 medidas
            if (todasAsMedidas.length > 0) {
                exibirMedidaRecente(todasAsMedidas[0]);
                exibirUltimasMedidas(todasAsMedidas.slice(0, 3));
                document.getElementById('semMedidasMsg').style.display = 'none';
            } else {
                document.getElementById('medidaRecenteCard').style.display = 'none';
                document.getElementById('ultimasMedidasCard').style.display = 'none';
                document.getElementById('semMedidasMsg').style.display = 'block';
            }
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
 * Exibe a medida mais recente expandida
 */
function exibirMedidaRecente(medida) {
    const card = document.getElementById('medidaRecenteCard');
    const content = document.getElementById('medidaRecenteContent');
    
    const imc = medida.peso && medida.altura ? calcularIMC(medida.peso, medida.altura) : null;
    
    content.innerHTML = `
        <div style="background: #f5f5f5; padding: 20px; border-radius: 8px;">
            <h4 style="margin: 0 0 20px 0; color: #333; font-size: 1.1rem;">Detalhes da Medição - ${formatarData(medida.data)}</h4>
            
            <!-- Métricas Principais -->
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 15px; margin-bottom: 20px;">
                <div style="background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.85rem; color: #666; margin-bottom: 5px;">Data</div>
                    <div style="font-size: 1.3rem; font-weight: bold; color: #1976d2;">${formatarData(medida.data)}</div>
                </div>
                <div style="background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.85rem; color: #666; margin-bottom: 5px;">Peso</div>
                    <div style="font-size: 1.3rem; font-weight: bold; color: #1976d2;">${medida.peso ? medida.peso.toFixed(1) + ' kg' : '-'}</div>
                </div>
                <div style="background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.85rem; color: #666; margin-bottom: 5px;">Altura</div>
                    <div style="font-size: 1.3rem; font-weight: bold; color: #1976d2;">${medida.altura ? medida.altura.toFixed(1) + ' cm' : '-'}</div>
                </div>
                <div style="background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.85rem; color: #666; margin-bottom: 5px;">IMC</div>
                    <div style="font-size: 1.3rem; font-weight: bold; color: #1976d2;">${imc ? imc.toFixed(1) : '-'}</div>
                </div>
                <div style="background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.85rem; color: #666; margin-bottom: 5px;">% Gordura</div>
                    <div style="font-size: 1.3rem; font-weight: bold; color: #1976d2;">${medida.percentualGordura ? medida.percentualGordura.toFixed(1) + '%' : '-'}</div>
                </div>
            </div>
            
            <!-- Circunferências -->
            <h5 style="margin: 20px 0 10px 0; color: #555; font-size: 1rem;">Circunferências (cm)</h5>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(120px, 1fr)); gap: 12px;">
                <div style="background: white; padding: 12px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.8rem; color: #666; margin-bottom: 3px;">Peito</div>
                    <div style="font-size: 1.1rem; font-weight: bold; color: #333;">${medida.peito ? medida.peito.toFixed(1) : '-'}</div>
                </div>
                <div style="background: white; padding: 12px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.8rem; color: #666; margin-bottom: 3px;">Cintura</div>
                    <div style="font-size: 1.1rem; font-weight: bold; color: #333;">${medida.cintura ? medida.cintura.toFixed(1) : '-'}</div>
                </div>
                <div style="background: white; padding: 12px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.8rem; color: #666; margin-bottom: 3px;">Quadril</div>
                    <div style="font-size: 1.1rem; font-weight: bold; color: #333;">${medida.quadril ? medida.quadril.toFixed(1) : '-'}</div>
                </div>
                <div style="background: white; padding: 12px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.8rem; color: #666; margin-bottom: 3px;">Braço Direito</div>
                    <div style="font-size: 1.1rem; font-weight: bold; color: #333;">${medida.bracoDireito ? medida.bracoDireito.toFixed(1) : '-'}</div>
                </div>
                <div style="background: white; padding: 12px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.8rem; color: #666; margin-bottom: 3px;">Braço Esquerdo</div>
                    <div style="font-size: 1.1rem; font-weight: bold; color: #333;">${medida.bracoEsquerdo ? medida.bracoEsquerdo.toFixed(1) : '-'}</div>
                </div>
                <div style="background: white; padding: 12px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.8rem; color: #666; margin-bottom: 3px;">Coxa Direita</div>
                    <div style="font-size: 1.1rem; font-weight: bold; color: #333;">${medida.coxaDireita ? medida.coxaDireita.toFixed(1) : '-'}</div>
                </div>
                <div style="background: white; padding: 12px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                    <div style="font-size: 0.8rem; color: #666; margin-bottom: 3px;">Coxa Esquerda</div>
                    <div style="font-size: 1.1rem; font-weight: bold; color: #333;">${medida.coxaEsquerda ? medida.coxaEsquerda.toFixed(1) : '-'}</div>
                </div>
            </div>
            
            ${medida.observacoes ? `
                <div style="margin-top: 20px; padding: 15px; background: white; border-radius: 6px; border-left: 4px solid #1976d2;">
                    <div style="font-size: 0.85rem; color: #666; margin-bottom: 5px;">Observações</div>
                    <div style="color: #333;">${medida.observacoes}</div>
                </div>
            ` : ''}
        </div>
    `;
    
    card.style.display = 'block';
}

/**
 * Exibe as últimas medidas em formato de tabela
 */
function exibirUltimasMedidas(medidas) {
    const card = document.getElementById('ultimasMedidasCard');
    const tbody = document.getElementById('ultimasMedidasBody');
    
    tbody.innerHTML = medidas.map((medida, index) => {
        const imc = medida.peso && medida.altura ? calcularIMC(medida.peso, medida.altura) : null;
        const isRecente = index === 0;
        
        return `
            <tr class="table__body-row" id="medida-row-${medida.id}">
                <td class="table__body-cell">${formatarData(medida.data)}${isRecente ? ' <span style="color: #1976d2; font-weight: bold;">(Mais recente)</span>' : ''}</td>
                <td class="table__body-cell">${medida.peso ? medida.peso.toFixed(1) + ' kg' : '-'}</td>
                <td class="table__body-cell">${medida.percentualGordura ? medida.percentualGordura.toFixed(1) + '%' : '-'}</td>
                <td class="table__body-cell">${imc ? imc.toFixed(1) : '-'}</td>
                <td class="table__body-cell">
                    <button class="btn btn--primary btn--sm" onclick="visualizarMedidaDetalhada(${medida.id})">
                        Visualizar Detalhes
                    </button>
                </td>
            </tr>
            <tr id="medida-detalhes-${medida.id}" style="display: none;">
                <td colspan="5">
                    <div id="detalhes-content-${medida.id}" style="padding: 15px; background: #f9f9f9;"></div>
                </td>
            </tr>
        `;
    }).join('');
    
    card.style.display = 'block';
}

/**
 * Visualiza os detalhes de uma medida específica (toggle)
 */
function visualizarMedidaDetalhada(id) {
    const detalhesRow = document.getElementById(`medida-detalhes-${id}`);
    const contentDiv = document.getElementById(`detalhes-content-${id}`);
    
    // Toggle: se já está visível, esconde
    if (detalhesRow.style.display !== 'none') {
        detalhesRow.style.display = 'none';
        return;
    }
    
    // Busca os detalhes da medida
    fetch(`/api/medidas/${id}`)
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar medida');
            return response.json();
        })
        .then(medida => {
            const imc = medida.peso && medida.altura ? calcularIMC(medida.peso, medida.altura) : null;
            
            contentDiv.innerHTML = `
                <div style="background: white; padding: 15px; border-radius: 6px;">
                    <h5 style="margin: 0 0 15px 0; color: #333;">Detalhes Completos</h5>
                    
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 12px; margin-bottom: 15px;">
                        <div style="padding: 10px; background: #f5f5f5; border-radius: 4px;">
                            <div style="font-size: 0.8rem; color: #666;">Data</div>
                            <div style="font-weight: bold; color: #333;">${formatarData(medida.data)}</div>
                        </div>
                        <div style="padding: 10px; background: #f5f5f5; border-radius: 4px;">
                            <div style="font-size: 0.8rem; color: #666;">Peso</div>
                            <div style="font-weight: bold; color: #333;">${medida.peso ? medida.peso.toFixed(1) + ' kg' : '-'}</div>
                        </div>
                        <div style="padding: 10px; background: #f5f5f5; border-radius: 4px;">
                            <div style="font-size: 0.8rem; color: #666;">Altura</div>
                            <div style="font-weight: bold; color: #333;">${medida.altura ? medida.altura.toFixed(1) + ' cm' : '-'}</div>
                        </div>
                        <div style="padding: 10px; background: #f5f5f5; border-radius: 4px;">
                            <div style="font-size: 0.8rem; color: #666;">IMC</div>
                            <div style="font-weight: bold; color: #333;">${imc ? imc.toFixed(1) : '-'}</div>
                        </div>
                        <div style="padding: 10px; background: #f5f5f5; border-radius: 4px;">
                            <div style="font-size: 0.8rem; color: #666;">% Gordura</div>
                            <div style="font-weight: bold; color: #333;">${medida.percentualGordura ? medida.percentualGordura.toFixed(1) + '%' : '-'}</div>
                        </div>
                    </div>
                    
                    <h6 style="margin: 15px 0 10px 0; color: #555; font-size: 0.95rem;">Circunferências (cm)</h6>
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(100px, 1fr)); gap: 10px;">
                        <div style="padding: 8px; background: #f5f5f5; border-radius: 4px; text-align: center;">
                            <div style="font-size: 0.75rem; color: #666;">Peito</div>
                            <div style="font-weight: bold;">${medida.peito ? medida.peito.toFixed(1) : '-'}</div>
                        </div>
                        <div style="padding: 8px; background: #f5f5f5; border-radius: 4px; text-align: center;">
                            <div style="font-size: 0.75rem; color: #666;">Cintura</div>
                            <div style="font-weight: bold;">${medida.cintura ? medida.cintura.toFixed(1) : '-'}</div>
                        </div>
                        <div style="padding: 8px; background: #f5f5f5; border-radius: 4px; text-align: center;">
                            <div style="font-size: 0.75rem; color: #666;">Quadril</div>
                            <div style="font-weight: bold;">${medida.quadril ? medida.quadril.toFixed(1) : '-'}</div>
                        </div>
                        <div style="padding: 8px; background: #f5f5f5; border-radius: 4px; text-align: center;">
                            <div style="font-size: 0.75rem; color: #666;">Braço D</div>
                            <div style="font-weight: bold;">${medida.bracoDireito ? medida.bracoDireito.toFixed(1) : '-'}</div>
                        </div>
                        <div style="padding: 8px; background: #f5f5f5; border-radius: 4px; text-align: center;">
                            <div style="font-size: 0.75rem; color: #666;">Braço E</div>
                            <div style="font-weight: bold;">${medida.bracoEsquerdo ? medida.bracoEsquerdo.toFixed(1) : '-'}</div>
                        </div>
                        <div style="padding: 8px; background: #f5f5f5; border-radius: 4px; text-align: center;">
                            <div style="font-size: 0.75rem; color: #666;">Coxa D</div>
                            <div style="font-weight: bold;">${medida.coxaDireita ? medida.coxaDireita.toFixed(1) : '-'}</div>
                        </div>
                        <div style="padding: 8px; background: #f5f5f5; border-radius: 4px; text-align: center;">
                            <div style="font-size: 0.75rem; color: #666;">Coxa E</div>
                            <div style="font-weight: bold;">${medida.coxaEsquerda ? medida.coxaEsquerda.toFixed(1) : '-'}</div>
                        </div>
                    </div>
                    
                    ${medida.observacoes ? `
                        <div style="margin-top: 15px; padding: 12px; background: #e3f2fd; border-radius: 4px; border-left: 3px solid #1976d2;">
                            <div style="font-size: 0.8rem; color: #666; margin-bottom: 3px;">Observações</div>
                            <div style="color: #333;">${medida.observacoes}</div>
                        </div>
                    ` : ''}
                </div>
            `;
            
            detalhesRow.style.display = 'table-row';
        })
        .catch(error => {
            console.error('Erro:', error);
            contentDiv.innerHTML = '<p style="color: #d32f2f; padding: 10px;">Erro ao carregar detalhes da medida</p>';
            detalhesRow.style.display = 'table-row';
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
