/* ===== SCRIPT DO PROFESSOR ===== */

document.addEventListener('DOMContentLoaded', () => {
    loadEstatisticas();
});

/**
 * Carrega estatísticas
 */
function loadEstatisticas() {
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

