/* ===== INTERCEPTOR DE TOKEN ===== */

// Interceptor global para adicionar token em requisições AJAX
const originalFetch = window.fetch;
window.fetch = function(...args) {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        if (!args[1]) args[1] = {};
        if (!args[1].headers) args[1].headers = {};
        args[1].headers['Authorization'] = `Bearer ${token}`;
        args[1].credentials = 'include'; // Incluir cookies
    }
    return originalFetch.apply(this, args);
};
