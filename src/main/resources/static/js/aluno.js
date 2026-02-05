/* ===== SCRIPT DO ALUNO ===== */

document.addEventListener('DOMContentLoaded', () => {
    loadPendenteMensalidades();
});

/**
 * Carrega número de mensalidades pendentes
 */
function loadPendenteMensalidades() {
    const alunoId = localStorage.getItem('alunoId');

    if (!alunoId) {
        console.warn('ID do aluno não encontrado no localStorage');
        document.getElementById('pendentes').textContent = '0';
        return;
    }

    fetch(`/api/mensalidades/aluno/${alunoId}/pendentes/count`)
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar dados');
            return response.json();
        })
        .then(data => {
            document.getElementById('pendentes').textContent = data || '0';
        })
        .catch(error => {
            console.error('Erro ao carregar mensalidades:', error);
            document.getElementById('pendentes').textContent = '0';
        });
}
