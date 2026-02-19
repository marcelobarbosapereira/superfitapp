/* ===== SCRIPT DOS RELATÓRIOS DO PROFESSOR ===== */

let chartPeso = null;
let chartGordura = null;
let chartCircunferencias = null;

document.addEventListener('DOMContentLoaded', () => {
    loadAlunos();
});

/**
 * Carrega lista de alunos do professor
 */
function loadAlunos() {
    fetch('/api/professor/alunos')
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar alunos');
            return response.json();
        })
        .then(alunos => {
            const select = document.getElementById('alunoSelect');
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
        });
}

/**
 * Carrega o relatório do aluno selecionado
 */
function carregarRelatorio() {
    const alunoId = document.getElementById('alunoSelect').value;
    const periodo = document.getElementById('periodoSelect').value;

    if (!alunoId) {
        document.getElementById('relatorioContent').style.display = 'none';
        document.getElementById('mensagemSelecao').style.display = 'block';
        return;
    }

    document.getElementById('mensagemSelecao').style.display = 'none';
    document.getElementById('relatorioContent').style.display = 'block';

    // Carrega dados de medidas do aluno
    fetch(`/api/medidas/aluno/${alunoId}?periodo=${periodo}`)
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar medidas');
            return response.json();
        })
        .then(medidas => {
            if (!medidas || medidas.length === 0) {
                document.getElementById('historicoMedidasTable').style.display = 'none';
                document.getElementById('emptyState').classList.remove('d-none');
                limparGraficos();
                return;
            }

            document.getElementById('historicoMedidasTable').style.display = 'table';
            document.getElementById('emptyState').classList.add('d-none');

            // Ordena medidas por data (mais recente primeiro)
            medidas.sort((a, b) => new Date(b.data) - new Date(a.data));

            // Atualiza resumo de progresso
            atualizarResumoProgresso(medidas);

            // Popula tabela de histórico
            preencherTabelaMedidas(medidas);

            // Cria gráficos
            criarGraficos(medidas);

            // Gera observações
            gerarObservacoes(medidas);
        })
        .catch(error => {
            console.error('Erro:', error);
            showMessage('', '❌ Erro ao carregar relatório', 'error');
        });
}

/**
 * Atualiza o resumo de progresso
 */
function atualizarResumoProgresso(medidas) {
    if (medidas.length < 2) {
        document.getElementById('variacaoPeso').textContent = '-';
        document.getElementById('variacaoGordura').textContent = '-';
        document.getElementById('imcAtual').textContent = '-';
        return;
    }

    const medidasOrdenadas = medidas.sort((a, b) => new Date(a.data) - new Date(b.data));
    const primeira = medidasOrdenadas[0];
    const ultima = medidasOrdenadas[medidasOrdenadas.length - 1];

    // Variação de peso
    const variacaoPeso = ultima.peso - primeira.peso;
    const textoPeso = variacaoPeso >= 0 ? `+${variacaoPeso.toFixed(1)}` : `${variacaoPeso.toFixed(1)}`;
    document.getElementById('variacaoPeso').textContent = textoPeso + ' kg';

    // Variação de gordura
    const variacaoGordura = ultima.percentualGordura - primeira.percentualGordura;
    const textoGordura = variacaoGordura >= 0 ? `+${variacaoGordura.toFixed(1)}` : `${variacaoGordura.toFixed(1)}`;
    document.getElementById('variacaoGordura').textContent = textoGordura + ' %';

    // IMC atual
    const imc = calcularIMC(ultima.peso, ultima.altura);
    document.getElementById('imcAtual').textContent = imc.toFixed(1);
}

/**
 * Calcula IMC
 */
function calcularIMC(peso, altura) {
    const alturaMetros = altura / 100;
    return peso / (alturaMetros * alturaMetros);
}

/**
 * Preenche a tabela de histórico de medidas
 */
function preencherTabelaMedidas(medidas) {
    const tbody = document.getElementById('historicoMedidasBody');
    const medidasOrdenadas = medidas.sort((a, b) => new Date(b.data) - new Date(a.data));

    tbody.innerHTML = medidasOrdenadas.map(medida => `
        <tr class="table__body-row">
            <td class="table__body-cell">${formatarData(medida.data)}</td>
            <td class="table__body-cell">${medida.peso ? medida.peso.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${medida.percentualGordura ? medida.percentualGordura.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${medida.peito ? medida.peito.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${medida.cintura ? medida.cintura.toFixed(1) : '-'}</td>
            <td class="table__body-cell">${((medida.bracoDireito || 0) + (medida.bracoEsquerdo || 0)) / 2 > 0 ? (((medida.bracoDireito || 0) + (medida.bracoEsquerdo || 0)) / 2).toFixed(1) : '-'}</td>
        </tr>
    `).join('');
}

/**
 * Cria os gráficos de evolução
 */
function criarGraficos(medidas) {
    const medidasOrdenadas = medidas.sort((a, b) => new Date(a.data) - new Date(b.data));
    const datas = medidasOrdenadas.map(m => formatarData(m.data));
    
    // Gráfico de Peso
    criarGraficoPeso(datas, medidasOrdenadas);
    
    // Gráfico de Gordura
    criarGraficoGordura(datas, medidasOrdenadas);
    
    // Gráfico de Circunferências
    criarGraficoCircunferencias(datas, medidasOrdenadas);
}

/**
 * Cria gráfico de evolução de peso
 */
function criarGraficoPeso(datas, medidas) {
    const ctx = document.getElementById('graficoPeso');
    
    if (chartPeso) {
        chartPeso.destroy();
    }

    chartPeso = new Chart(ctx, {
        type: 'line',
        data: {
            labels: datas,
            datasets: [{
                label: 'Peso (kg)',
                data: medidas.map(m => m.peso || 0),
                borderColor: '#6c5ce7',
                backgroundColor: 'rgba(108, 92, 231, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    beginAtZero: false
                }
            }
        }
    });
}

/**
 * Cria gráfico de evolução de % gordura
 */
function criarGraficoGordura(datas, medidas) {
    const ctx = document.getElementById('graficoGordura');
    
    if (chartGordura) {
        chartGordura.destroy();
    }

    chartGordura = new Chart(ctx, {
        type: 'line',
        data: {
            labels: datas,
            datasets: [{
                label: '% Gordura',
                data: medidas.map(m => m.percentualGordura || 0),
                borderColor: '#e17055',
                backgroundColor: 'rgba(225, 112, 85, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    max: 100
                }
            }
        }
    });
}

/**
 * Cria gráfico de evolução de circunferências
 */
function criarGraficoCircunferencias(datas, medidas) {
    const ctx = document.getElementById('graficoCircunferencias');
    
    if (chartCircunferencias) {
        chartCircunferencias.destroy();
    }

    chartCircunferencias = new Chart(ctx, {
        type: 'line',
        data: {
            labels: datas,
            datasets: [
                {
                    label: 'Peito (cm)',
                    data: medidas.map(m => m.peito || 0),
                    borderColor: '#00b894',
                    backgroundColor: 'rgba(0, 184, 148, 0.1)',
                    tension: 0.4
                },
                {
                    label: 'Cintura (cm)',
                    data: medidas.map(m => m.cintura || 0),
                    borderColor: '#fdcb6e',
                    backgroundColor: 'rgba(253, 203, 110, 0.1)',
                    tension: 0.4
                },
                {
                    label: 'Braço (cm)',
                    data: medidas.map(m => ((m.bracoDireito || 0) + (m.bracoEsquerdo || 0)) / 2),
                    borderColor: '#6c5ce7',
                    backgroundColor: 'rgba(108, 92, 231, 0.1)',
                    tension: 0.4
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    beginAtZero: false
                }
            }
        }
    });
}

/**
 * Gera observações e recomendações
 */
function gerarObservacoes(medidas) {
    const medidasOrdenadas = medidas.sort((a, b) => new Date(a.data) - new Date(b.data));
    const primeira = medidasOrdenadas[0];
    const ultima = medidasOrdenadas[medidasOrdenadas.length - 1];

    let observacoes = '<ul>';

    // Análise de peso
    const variacaoPeso = ultima.peso - primeira.peso;
    if (variacaoPeso > 0) {
        observacoes += `<li>Ganho de peso de ${variacaoPeso.toFixed(1)} kg durante o período</li>`;
    } else if (variacaoPeso < 0) {
        observacoes += `<li>Perda de peso de ${Math.abs(variacaoPeso).toFixed(1)} kg durante o período</li>`;
    } else {
        observacoes += '<li>Peso mantido durante o período</li>';
    }

    // Análise de gordura
    if (ultima.percentualGordura && primeira.percentualGordura) {
        const variacaoGordura = ultima.percentualGordura - primeira.percentualGordura;
        if (variacaoGordura > 0) {
            observacoes += `<li>Aumento de ${variacaoGordura.toFixed(1)}% no percentual de gordura</li>`;
        } else if (variacaoGordura < 0) {
            observacoes += `<li>Redução de ${Math.abs(variacaoGordura).toFixed(1)}% no percentual de gordura</li>`;
        }
    }

    // Análise de cintura
    if (ultima.cintura && primeira.cintura) {
        const variacaoCintura = ultima.cintura - primeira.cintura;
        if (variacaoCintura > 0) {
            observacoes += `<li>Aumento de cintura: ${variacaoCintura.toFixed(1)} cm</li>`;
        } else if (variacaoCintura < 0) {
            observacoes += `<li>Redução de cintura: ${Math.abs(variacaoCintura).toFixed(1)} cm</li>`;
        }
    }

    // Análise de braço
    if ((ultima.bracoDireito || ultima.bracoEsquerdo)) {
        const mediaBracoAtual = ((ultima.bracoDireito || 0) + (ultima.bracoEsquerdo || 0)) / 2;
        const mediaInicialBraco = ((primeira.bracoDireito || 0) + (primeira.bracoEsquerdo || 0)) / 2;
        const variacaoBraco = mediaBracoAtual - mediaInicialBraco;
        
        if (variacaoBraco > 0) {
            observacoes += `<li>Ganho de massa nos braços: ${variacaoBraco.toFixed(1)} cm</li>`;
        } else if (variacaoBraco < 0) {
            observacoes += `<li>Redução nos braços: ${Math.abs(variacaoBraco).toFixed(1)} cm</li>`;
        }
    }

    observacoes += '</ul>';
    document.getElementById('observacoesRelatorio').innerHTML = observacoes;
}

/**
 * Limpa os gráficos
 */
function limparGraficos() {
    if (chartPeso) {
        chartPeso.destroy();
        chartPeso = null;
    }
    if (chartGordura) {
        chartGordura.destroy();
        chartGordura = null;
    }
    if (chartCircunferencias) {
        chartCircunferencias.destroy();
        chartCircunferencias = null;
    }
}

/**
 * Formata uma data para formato brasileiro (DD/MM/YYYY)
 */
function formatarData(dataString) {
    if (!dataString) return '-';
    const data = new Date(dataString);
    return data.toLocaleDateString('pt-BR');
}
