/* ===== SCRIPT DO REGISTRO DE MEDIDAS DO PROFESSOR ===== */

let graficoPeso = null;
let graficoGordura = null;

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
        
        // Guarda o aluno selecionado
        const alunoSelecionado = document.getElementById('aluno').value;
        
        // Reseta o formulário
        document.getElementById('formRegistroMedidas').reset();
        
        // Reseta a data para hoje
        const dataHoje = new Date();
        document.getElementById('data').valueAsDate = dataHoje;
        
        // Recarrega os dados do aluno se estava selecionado
        if (alunoSelecionado) {
            document.getElementById('aluno').value = alunoSelecionado;
            carregarDadosAluno();
        } else {
            loadMedidasRecentes();
        }
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
                    <tr class="table__body-row" id="medida-row-${medida.id}">
                        <td class="table__body-cell">${medida.alunoNome || '-'}</td>
                        <td class="table__body-cell">${formatarData(medida.data)}</td>
                        <td class="table__body-cell">${medida.peso ? medida.peso.toFixed(1) : '-'}</td>
                        <td class="table__body-cell">${medida.percentualGordura ? medida.percentualGordura.toFixed(1) : '-'}</td>
                        <td class="table__body-cell">
                            <div class="table__actions">
                                <button class="btn btn--info btn--sm" onclick="visualizarMedida(${medida.id})">Visualizar</button>
                                <button class="btn btn--danger btn--sm" onclick="deletarMedida(${medida.id})">Deletar</button>
                            </div>
                        </td>
                    </tr>
                    <tr class="table__body-row" id="medida-detalhes-${medida.id}" style="display: none;">
                        <td colspan="5" style="padding: 1rem; background: #f5f5f5;">
                            <div id="detalhes-content-${medida.id}"></div>
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
 * Visualiza os detalhes de uma medida
 */
function visualizarMedida(id) {
    const detalhesRow = document.getElementById(`medida-detalhes-${id}`);
    const contentDiv = document.getElementById(`detalhes-content-${id}`);
    
    // Se já está visível, esconde
    if (detalhesRow.style.display !== 'none') {
        detalhesRow.style.display = 'none';
        return;
    }
    
    // Busca os detalhes da medida
    fetch(`/api/medidas/${id}`, { credentials: 'include' })
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar medida');
            return response.json();
        })
        .then(medida => {
            const imc = calcularIMC(medida.peso, medida.altura);
            
            contentDiv.innerHTML = `
                <h4 style="margin-bottom: 1rem; color: #333;">Detalhes da Medição - ${medida.alunoNome}</h4>
                
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 1rem;">
                    <div style="background: white; padding: 1rem; border-radius: 8px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.875rem; color: #666; margin-bottom: 0.5rem;">Data</div>
                        <div style="font-size: 1.25rem; font-weight: 600; color: #333;">${formatarData(medida.data)}</div>
                    </div>
                    <div style="background: white; padding: 1rem; border-radius: 8px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.875rem; color: #666; margin-bottom: 0.5rem;">Peso</div>
                        <div style="font-size: 1.25rem; font-weight: 600; color: #333;">${medida.peso ? medida.peso.toFixed(1) + ' kg' : '-'}</div>
                    </div>
                    <div style="background: white; padding: 1rem; border-radius: 8px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.875rem; color: #666; margin-bottom: 0.5rem;">Altura</div>
                        <div style="font-size: 1.25rem; font-weight: 600; color: #333;">${medida.altura ? medida.altura.toFixed(1) + ' cm' : '-'}</div>
                    </div>
                    <div style="background: white; padding: 1rem; border-radius: 8px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.875rem; color: #666; margin-bottom: 0.5rem;">IMC</div>
                        <div style="font-size: 1.25rem; font-weight: 600; color: #1976d2;">${imc > 0 ? imc.toFixed(1) : '-'}</div>
                    </div>
                    <div style="background: white; padding: 1rem; border-radius: 8px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.875rem; color: #666; margin-bottom: 0.5rem;">% Gordura</div>
                        <div style="font-size: 1.25rem; font-weight: 600; color: #333;">${medida.percentualGordura ? medida.percentualGordura.toFixed(1) + '%' : '-'}</div>
                    </div>
                </div>
                
                <h5 style="margin: 1.5rem 0 1rem 0; color: #333; font-size: 1rem;">Circunferências (cm)</h5>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 1rem;">
                    <div style="background: white; padding: 0.75rem; border-radius: 6px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.75rem; color: #666;">Peito</div>
                        <div style="font-size: 1rem; font-weight: 600; color: #333;">${medida.peito ? medida.peito.toFixed(1) : '-'}</div>
                    </div>
                    <div style="background: white; padding: 0.75rem; border-radius: 6px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.75rem; color: #666;">Cintura</div>
                        <div style="font-size: 1rem; font-weight: 600; color: #333;">${medida.cintura ? medida.cintura.toFixed(1) : '-'}</div>
                    </div>
                    <div style="background: white; padding: 0.75rem; border-radius: 6px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.75rem; color: #666;">Quadril</div>
                        <div style="font-size: 1rem; font-weight: 600; color: #333;">${medida.quadril ? medida.quadril.toFixed(1) : '-'}</div>
                    </div>
                    <div style="background: white; padding: 0.75rem; border-radius: 6px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.75rem; color: #666;">Braço Direito</div>
                        <div style="font-size: 1rem; font-weight: 600; color: #333;">${medida.bracoDireito ? medida.bracoDireito.toFixed(1) : '-'}</div>
                    </div>
                    <div style="background: white; padding: 0.75rem; border-radius: 6px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.75rem; color: #666;">Braço Esquerdo</div>
                        <div style="font-size: 1rem; font-weight: 600; color: #333;">${medida.bracoEsquerdo ? medida.bracoEsquerdo.toFixed(1) : '-'}</div>
                    </div>
                    <div style="background: white; padding: 0.75rem; border-radius: 6px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.75rem; color: #666;">Coxa Direita</div>
                        <div style="font-size: 1rem; font-weight: 600; color: #333;">${medida.coxaDireita ? medida.coxaDireita.toFixed(1) : '-'}</div>
                    </div>
                    <div style="background: white; padding: 0.75rem; border-radius: 6px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.75rem; color: #666;">Coxa Esquerda</div>
                        <div style="font-size: 1rem; font-weight: 600; color: #333;">${medida.coxaEsquerda ? medida.coxaEsquerda.toFixed(1) : '-'}</div>
                    </div>
                </div>
                
                ${medida.observacoes ? `
                    <div style="margin-top: 1rem; padding: 1rem; background: white; border-radius: 8px; border: 1px solid #e0e0e0;">
                        <div style="font-size: 0.875rem; color: #666; margin-bottom: 0.5rem;">Observações</div>
                        <div style="color: #333;">${medida.observacoes}</div>
                    </div>
                ` : ''}
            `;
            
            detalhesRow.style.display = 'table-row';
        })
        .catch(error => {
            console.error('Erro:', error);
            contentDiv.innerHTML = '<p style="color: #d32f2f;">❌ Erro ao carregar detalhes da medida</p>';
            detalhesRow.style.display = 'table-row';
        });
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
        
        // Recarrega dados do aluno se houver um selecionado
        const alunoId = document.getElementById('aluno').value;
        if (alunoId) {
            carregarDadosAluno();
        } else {
            loadMedidasRecentes();
        }
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

/**
 * Carrega dados do aluno selecionado (métricas e gráficos)
 */
function carregarDadosAluno() {
    const alunoId = document.getElementById('aluno').value;
    
    if (!alunoId) {
        document.getElementById('metricas-card').style.display = 'none';
        loadMedidasRecentes();
        return;
    }
    
    // Busca o nome do aluno selecionado
    const selectAluno = document.getElementById('aluno');
    const nomeAluno = selectAluno.options[selectAluno.selectedIndex].text;
    document.getElementById('nomeAlunoMetricas').textContent = nomeAluno;
    
    // Carrega medidas do aluno
    fetch(`/api/medidas/aluno/${alunoId}`, { credentials: 'include' })
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar medidas do aluno');
            return response.json();
        })
        .then(medidas => {
            if (!medidas || medidas.length === 0) {
                document.getElementById('metricas-card').style.display = 'none';
                alert('Este aluno ainda não possui medidas registradas.');
                return;
            }
            
            // Ordena medidas por data (mais recente primeiro)
            medidas.sort((a, b) => new Date(b.data) - new Date(a.data));
            
            // Mostra card de métricas
            document.getElementById('metricas-card').style.display = 'block';
            
            // Atualiza estatísticas
            atualizarEstatisticas(medidas);
            
            // Atualiza gráficos
            atualizarGraficos(medidas);
            
            // Atualiza tabela com medidas do aluno
            atualizarTabelaMedidasAluno(medidas);
        })
        .catch(error => {
            console.error('Erro:', error);
            document.getElementById('metricas-card').style.display = 'none';
        });
}

/**
 * Atualiza estatísticas do aluno (IMC, variação de gordura, etc)
 */
function atualizarEstatisticas(medidas) {
    if (!medidas || medidas.length === 0) return;
    
    const medidaAtual = medidas[0]; // Mais recente
    
    // IMC Atual
    if (medidaAtual.peso && medidaAtual.altura) {
        const imc = calcularIMC(medidaAtual.peso, medidaAtual.altura);
        document.getElementById('imcAluno').textContent = imc.toFixed(1);
    } else {
        document.getElementById('imcAluno').textContent = '-';
    }
    
    // Peso Atual
    if (medidaAtual.peso) {
        document.getElementById('pesoAtualAluno').textContent = medidaAtual.peso.toFixed(1) + ' kg';
    } else {
        document.getElementById('pesoAtualAluno').textContent = '-';
    }
    
    // % Gordura Atual
    if (medidaAtual.percentualGordura) {
        document.getElementById('gorduraAtualAluno').textContent = medidaAtual.percentualGordura.toFixed(1) + '%';
    } else {
        document.getElementById('gorduraAtualAluno').textContent = '-';
    }
    
    // Variação de Gordura (comparando primeira com última medida)
    if (medidas.length >= 2) {
        const medidaAntiga = medidas[medidas.length - 1]; // Mais antiga
        
        if (medidaAtual.percentualGordura && medidaAntiga.percentualGordura) {
            const variacao = medidaAtual.percentualGordura - medidaAntiga.percentualGordura;
            const sinal = variacao > 0 ? '+' : '';
            const cor = variacao > 0 ? '#d32f2f' : '#388e3c';
            
            const elem = document.getElementById('variacaoGordura');
            elem.textContent = `${sinal}${variacao.toFixed(1)}%`;
            elem.style.color = cor;
        } else {
            document.getElementById('variacaoGordura').textContent = '-';
        }
    } else {
        document.getElementById('variacaoGordura').textContent = '-';
    }
}

/**
 * Calcula IMC (Índice de Massa Corporal)
 */
function calcularIMC(peso, altura) {
    if (!peso || !altura || altura === 0) return 0;
    const alturaMetros = altura / 100;
    return peso / (alturaMetros * alturaMetros);
}

/**
 * Atualiza gráficos de evolução
 */
function atualizarGraficos(medidas) {
    if (!medidas || medidas.length === 0) return;
    
    // Ordena por data (mais antiga primeiro para o gráfico)
    const medidasOrdenadas = [...medidas].sort((a, b) => new Date(a.data) - new Date(b.data));
    
    const labels = medidasOrdenadas.map(m => formatarData(m.data));
    const dadosPeso = medidasOrdenadas.map(m => m.peso || 0);
    const dadosGordura = medidasOrdenadas.map(m => m.percentualGordura || 0);
    
    // Gráfico de Peso
    const ctxPeso = document.getElementById('graficoPeso').getContext('2d');
    
    if (graficoPeso) {
        graficoPeso.destroy();
    }
    
    graficoPeso = new Chart(ctxPeso, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Peso (kg)',
                data: dadosPeso,
                borderColor: '#1976d2',
                backgroundColor: 'rgba(25, 118, 210, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    ticks: {
                        callback: function(value) {
                            return value.toFixed(1) + ' kg';
                        }
                    }
                }
            }
        }
    });
    
    // Gráfico de % Gordura
    const ctxGordura = document.getElementById('graficoGordura').getContext('2d');
    
    if (graficoGordura) {
        graficoGordura.destroy();
    }
    
    graficoGordura = new Chart(ctxGordura, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: '% Gordura',
                data: dadosGordura,
                borderColor: '#f57c00',
                backgroundColor: 'rgba(245, 124, 0, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    ticks: {
                        callback: function(value) {
                            return value.toFixed(1) + '%';
                        }
                    }
                }
            }
        }
    });
}

/**
 * Atualiza tabela com medidas do aluno selecionado
 */
function atualizarTabelaMedidasAluno(medidas) {
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
                        <button class="btn btn--info btn--sm" onclick="visualizarMedida(${medida.id})">Visualizar</button>
                        <button class="btn btn--danger btn--sm" onclick="deletarMedida(${medida.id})">Deletar</button>
                    </div>
                </td>
            </tr>
            <tr class="table__body-row" id="medida-detalhes-${medida.id}" style="display: none;">
                <td colspan="5" style="padding: 1rem; background: #f5f5f5;">
                    <div id="detalhes-content-${medida.id}"></div>
                </td>
            </tr>
        `).join('');
    }
}
