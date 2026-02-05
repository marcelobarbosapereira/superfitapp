/* ===== UTILITÁRIOS GERAIS ===== */

/**
 * Mostra uma mensagem na tela
 */
function showMessage(elementId, message, type, timeout = 5000) {
    const element = document.getElementById(elementId);
    if (!element) return;

    element.innerHTML = message;
    element.className = `message message--${type} message--show`;

    if (timeout > 0) {
        setTimeout(() => {
            element.classList.remove('message--show');
            element.innerHTML = '';
        }, timeout);
    }
}

/**
 * Realiza logout
 */
function logout() {
    localStorage.removeItem('jwtToken');
    window.location.href = '/logout';
}

/**
 * Decodifica JWT
 */
function decodeToken(token) {
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload;
    } catch (error) {
        console.error('Erro ao decodificar token:', error);
        return null;
    }
}

/**
 * Valida email
 */
function isValidEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

/**
 * Formata data
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
}

/**
 * Abre modal
 */
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('modal--show');
    }
}

/**
 * Fecha modal
 */
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('modal--show');
    }
}

/**
 * Confirma ação
 */
function confirmAction(message = 'Tem certeza que deseja realizar esta ação?') {
    return confirm(message);
}
