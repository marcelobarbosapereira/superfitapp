/* ===== RELATÓRIOS (GESTOR) ===== */

document.addEventListener('DOMContentLoaded', () => {
    // Carrega todos os relatórios ao carregar a página
    carregarReceitaMensal();
    carregarAlunosAtivos();
    carregarInadimplencia();
    carregarProfessores();
});

async function carregarReceitaMensal() {
    const container = document.getElementById('receitaMensalContainer');
    if (!container) return;

    try {
        const response = await fetch('/api/relatorios/receita-mensal', { credentials: 'include' });
        if (!response.ok) throw new Error(`Status ${response.status}`);
        
        const data = await response.json();
        
        container.innerHTML = `
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                <div class="stat-card" style="padding: 1rem; background: #e8f5e9; border-radius: 8px;">
                    <div style="font-size: 0.85rem; color: #2e7d32; font-weight: 600;">Receita Realizada</div>
                    <div style="font-size: 1.5rem; font-weight: 700; color: #1b5e20; margin-top: 0.5rem;">R$ ${data.receitaRealizada.toFixed(2)}</div>
                </div>
                <div class="stat-card" style="padding: 1rem; background: #fff3e0; border-radius: 8px;">
                    <div style="font-size: 0.85rem; color: #e65100; font-weight: 600;">Receita Pendente</div>
                    <div style="font-size: 1.5rem; font-weight: 700; color: #bf360c; margin-top: 0.5rem;">R$ ${data.receitaPendente.toFixed(2)}</div>
                </div>
                <div class="stat-card" style="padding: 1rem; background: #e3f2fd; border-radius: 8px;">
                    <div style="font-size: 0.85rem; color: #0277bd; font-weight: 600;">Receita Prevista</div>
                    <div style="font-size: 1.5rem; font-weight: 700; color: #01579b; margin-top: 0.5rem;">R$ ${data.receitaPrevista.toFixed(2)}</div>
                </div>
                <div class="stat-card" style="padding: 1rem; background: #f3e5f5; border-radius: 8px;">
                    <div style="font-size: 0.85rem; color: #6a1b9a; font-weight: 600;">Taxa de Adimplência</div>
                    <div style="font-size: 1.5rem; font-weight: 700; color: #4a148c; margin-top: 0.5rem;">${data.taxaAdimplencia.toFixed(1)}%</div>
                </div>
            </div>
            <div style="margin-top: 1rem; display: flex; gap: 2rem; flex-wrap: wrap;">
                <div>
                    <span style="font-weight: 600;">Total de Mensalidades:</span> ${data.totalMensalidades}
                </div>
                <div>
                    <span style="font-weight: 600;">Pagas:</span> <span style="color: #2e7d32;">${data.totalPagas}</span>
                </div>
                <div>
                    <span style="font-weight: 600;">Pendentes:</span> <span style="color: #d32f2f;">${data.totalPendentes}</span>
                </div>
            </div>
        `;
    } catch (error) {
        console.error('Erro ao carregar receita mensal:', error);
        container.innerHTML = '<p style="color: #d32f2f;">Erro ao carregar dados.</p>';
    }
}

async function carregarAlunosAtivos() {
    const container = document.getElementById('alunosAtivosContainer');
    if (!container) return;

    try {
        const response = await fetch('/api/relatorios/alunos-ativos', { credentials: 'include' });
        if (!response.ok) throw new Error(`Status ${response.status}`);
        
        const data = await response.json();
        
        container.innerHTML = `
            <div style="margin-bottom: 1rem;">
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 1rem; margin-bottom: 1rem;">
                    <div style="padding: 1rem; background: #e8f5e9; border-radius: 8px; text-align: center;">
                        <div style="font-size: 2rem; font-weight: 700; color: #2e7d32;">${data.alunosAtivos}</div>
                        <div style="font-size: 0.9rem; color: #1b5e20;">Alunos Ativos</div>
                    </div>
                    <div style="padding: 1rem; background: #e3f2fd; border-radius: 8px; text-align: center;">
                        <div style="font-size: 2rem; font-weight: 700; color: #0277bd;">${data.totalAlunos}</div>
                        <div style="font-size: 0.9rem; color: #01579b;">Total de Alunos</div>
                    </div>
                    <div style="padding: 1rem; background: #f3e5f5; border-radius: 8px; text-align: center;">
                        <div style="font-size: 2rem; font-weight: 700; color: #6a1b9a;">${data.percentualAtivos.toFixed(1)}%</div>
                        <div style="font-size: 0.9rem; color: #4a148c;">Percentual Ativos</div>
                    </div>
                </div>
                ${data.listaAtivos.length > 0 ? `
                    <details>
                        <summary style="cursor: pointer; font-weight: 600; padding: 0.5rem; background: #f5f5f5; border-radius: 4px;">
                            Ver lista de alunos ativos (${data.listaAtivos.length})
                        </summary>
                        <div style="margin-top: 1rem; max-height: 300px; overflow-y: auto;">
                            ${data.listaAtivos.map(aluno => `
                                <div style="padding: 0.75rem; border-bottom: 1px solid #e0e0e0;">
                                    <div style="font-weight: 600;">${aluno.nome}</div>
                                    <div style="font-size: 0.85rem; color: #666;">${aluno.email} | ${aluno.telefone || 'N/A'}</div>
                                </div>
                            `).join('')}
                        </div>
                    </details>
                ` : '<p>Nenhum aluno ativo no momento.</p>'}
            </div>
        `;
    } catch (error) {
        console.error('Erro ao carregar alunos ativos:', error);
        container.innerHTML = '<p style="color: #d32f2f;">Erro ao carregar dados.</p>';
    }
}

async function carregarInadimplencia() {
    const container = document.getElementById('inadimplenciaContainer');
    if (!container) return;

    try {
        const response = await fetch('/api/relatorios/inadimplencia', { credentials: 'include' });
        if (!response.ok) throw new Error(`Status ${response.status}`);
        
        const data = await response.json();
        
        container.innerHTML = `
            <div style="margin-bottom: 1rem;">
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 1rem; margin-bottom: 1rem;">
                    <div style="padding: 1rem; background: #ffebee; border-radius: 8px; text-align: center;">
                        <div style="font-size: 2rem; font-weight: 700; color: #c62828;">${data.quantidadeAlunos}</div>
                        <div style="font-size: 0.9rem; color: #b71c1c;">Alunos Inadimplentes</div>
                    </div>
                    <div style="padding: 1rem; background: #fff3e0; border-radius: 8px; text-align: center;">
                        <div style="font-size: 2rem; font-weight: 700; color: #e65100;">${data.quantidadeMensalidades}</div>
                        <div style="font-size: 0.9rem; color: #bf360c;">Mensalidades Pendentes</div>
                    </div>
                    <div style="padding: 1rem; background: #fce4ec; border-radius: 8px; text-align: center;">
                        <div style="font-size: 2rem; font-weight: 700; color: #ad1457;">R$ ${data.totalInadimplencia.toFixed(2)}</div>
                        <div style="font-size: 0.9rem; color: #880e4f;">Total em Atraso</div>
                    </div>
                </div>
                ${data.mensalidades.length > 0 ? `
                    <details>
                        <summary style="cursor: pointer; font-weight: 600; padding: 0.5rem; background: #f5f5f5; border-radius: 4px;">
                            Ver detalhes (${data.mensalidades.length} mensalidades)
                        </summary>
                        <div style="margin-top: 1rem; max-height: 300px; overflow-y: auto;">
                            ${data.mensalidades.map(m => `
                                <div style="padding: 0.75rem; border-bottom: 1px solid #e0e0e0;">
                                    <div style="font-weight: 600;">${m.alunoNome}</div>
                                    <div style="font-size: 0.85rem; color: #666;">
                                        ${m.mesReferencia}/${m.anoReferencia} - R$ ${m.valor.toFixed(2)} - 
                                        Venc: ${formatarData(m.dataVencimento)} 
                                        <span style="color: #d32f2f; font-weight: 600;">(${m.diasAtraso} dias de atraso)</span>
                                    </div>
                                </div>
                            `).join('')}
                        </div>
                    </details>
                ` : '<p style="color: #2e7d32;">✅ Nenhuma inadimplência no momento!</p>'}
            </div>
        `;
    } catch (error) {
        console.error('Erro ao carregar inadimplência:', error);
        container.innerHTML = '<p style="color: #d32f2f;">Erro ao carregar dados.</p>';
    }
}

async function carregarProfessores() {
    const container = document.getElementById('professoresContainer');
    if (!container) return;

    try {
        const response = await fetch('/api/relatorios/professores', { credentials: 'include' });
        if (!response.ok) throw new Error(`Status ${response.status}`);
        
        const data = await response.json();
        
        container.innerHTML = `
            <div style="margin-bottom: 1rem;">
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 1rem; margin-bottom: 1rem;">
                    <div style="padding: 1rem; background: #e8f5e9; border-radius: 8px; text-align: center;">
                        <div style="font-size: 2rem; font-weight: 700; color: #2e7d32;">${data.totalProfessores}</div>
                        <div style="font-size: 0.9rem; color: #1b5e20;">Total de Professores</div>
                    </div>
                    <div style="padding: 1rem; background: #e3f2fd; border-radius: 8px; text-align: center;">
                        <div style="font-size: 2rem; font-weight: 700; color: #0277bd;">${data.totalAlunos}</div>
                        <div style="font-size: 0.9rem; color: #01579b;">Total de Alunos</div>
                    </div>
                    <div style="padding: 1rem; background: #f3e5f5; border-radius: 8px; text-align: center;">
                        <div style="font-size: 2rem; font-weight: 700; color: #6a1b9a;">${data.mediaAlunosPorProfessor.toFixed(1)}</div>
                        <div style="font-size: 0.9rem; color: #4a148c;">Média Alunos/Prof</div>
                    </div>
                </div>
                ${data.listaProfessores.length > 0 ? `
                    <table class="table" style="width: 100%;">
                        <thead class="table__head">
                            <tr>
                                <th class="table__header-cell">Professor</th>
                                <th class="table__header-cell">Email</th>
                                <th class="table__header-cell">Quantidade de Alunos</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${data.listaProfessores.map(prof => `
                                <tr class="table__body-row">
                                    <td class="table__body-cell">${prof.professorNome}</td>
                                    <td class="table__body-cell">${prof.professorEmail}</td>
                                    <td class="table__body-cell"><strong>${prof.quantidadeAlunos}</strong></td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                ` : '<p>Nenhum professor cadastrado.</p>'}
            </div>
        `;
    } catch (error) {
        console.error('Erro ao carregar professores:', error);
        container.innerHTML = '<p style="color: #d32f2f;">Erro ao carregar dados.</p>';
    }
}

function formatarData(dataStr) {
    if (!dataStr) return 'N/A';
    const [ano, mes, dia] = dataStr.split('-');
    return `${dia}/${mes}/${ano}`;
}
