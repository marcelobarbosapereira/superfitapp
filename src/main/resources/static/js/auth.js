/* ===== AUTENTICAÇÃO ===== */

// Log para verificar se o script foi carregado
console.log('auth.js carregado');

document.addEventListener('DOMContentLoaded', () => {
    console.log('DOMContentLoaded disparado');
    
    const loginForm = document.getElementById('loginForm');
    const responseMessage = document.getElementById('responseMessage');

    console.log('loginForm:', loginForm);
    console.log('responseMessage:', responseMessage);

    if (loginForm) {
        console.log('Registrando listener no formulário');
        
        loginForm.addEventListener('submit', async function(e) {
            console.log('Submit event disparado', e);
            e.preventDefault();
            e.stopPropagation();
            
            console.log('Form submit interceptado');

            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value.trim();

            console.log('Email:', email, 'Password length:', password.length);

            if (!email || !password) {
                responseMessage.style.display = 'block';
                responseMessage.className = 'login__message login__message--error login__message--show';
                responseMessage.textContent = 'Email e senha são obrigatórios.';
                return false;
            }

            try {
                console.log('Iniciando requisição de login...');
                const response = await fetch('/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    credentials: 'include',
                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                });

                console.log('Resposta recebida:', response.status);
                const data = await response.json();

                if (response.ok) {
                    // Decodificar JWT para extrair o role
                    const token = data.token;
                    const payload = JSON.parse(atob(token.split('.')[1]));
                    const role = payload.role;

                    // Guardar token no localStorage
                    localStorage.setItem('jwtToken', token);

                    // Fallback: gravar cookie no browser para navegação de páginas
                    document.cookie = `jwtToken=${encodeURIComponent(token)}; path=/; max-age=86400; SameSite=Lax`;

                    responseMessage.style.display = 'block';
                    responseMessage.className = 'login__message login__message--success login__message--show';
                    responseMessage.textContent = '✅ Login realizado com sucesso! Redirecionando...';
                    console.log('Login bem-sucedido. Role:', role);

                    // Redirecionar conforme o role
                    setTimeout(() => {
                        if (role === 'ROLE_ADMIN') {
                            window.location.href = '/admin/dashboard';
                        } else if (role === 'ROLE_GESTOR') {
                            window.location.href = '/gestor/dashboard';
                        } else if (role === 'ROLE_PROFESSOR') {
                            window.location.href = '/professor/dashboard';
                        } else if (role === 'ROLE_ALUNO') {
                            window.location.href = '/aluno/dashboard';
                        } else {
                            window.location.href = '/home';
                        }
                    }, 800);
                } else {
                    responseMessage.style.display = 'block';
                    responseMessage.className = 'login__message login__message--error login__message--show';
                    responseMessage.textContent = `Falha no login. Status: ${response.status} - Verifique usuário e senha.`;
                    console.error('Login falhou. Status:', response.status, 'Resposta:', data);
                }
            } catch (error) {
                responseMessage.style.display = 'block';
                responseMessage.className = 'login__message login__message--error login__message--show';
                responseMessage.textContent = 'Erro ao conectar ao servidor.';
                console.error('Erro completo:', error);
            }

            return false;
        });
    } else {
        console.error('Formulário não encontrado!');
    }

    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', () => {
            const isHidden = passwordInput.type === 'password';
            passwordInput.type = isHidden ? 'text' : 'password';
            togglePassword.setAttribute('aria-label', isHidden ? 'Ocultar senha' : 'Mostrar senha');
        });
    }
});
