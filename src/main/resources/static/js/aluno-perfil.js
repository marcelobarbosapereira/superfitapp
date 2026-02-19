/* ===== SCRIPT DO PERFIL DO ALUNO ===== */

document.addEventListener('DOMContentLoaded', () => {
    carregarPerfil();
    carregarMedidasAtuais();
    configurarFoto();
    configurarFormularioSenha();
});

/**
 * Carrega os dados do perfil do aluno
 */
function carregarPerfil() {
    fetch('/api/alunos/meu-perfil')
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar perfil');
            return response.json();
        })
        .then(dados => {
            document.getElementById('nomePerfil').textContent = dados.nome || '-';
            document.getElementById('emailPerfil').textContent = dados.email || '-';
            document.getElementById('telefonePerfil').textContent = dados.telefone || '-';
            document.getElementById('dataIngresso').textContent = formatarData(dados.dataCriacao) || '-';

            // Carrega foto de perfil se existir
            if (dados.fotoPerfil) {
                exibirFoto(dados.fotoPerfil);
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            showMessage('senhaMessage', '❌ Erro ao carregar perfil', 'error');
        });
}

/**
 * Carrega as medidas atuais do aluno
 */
function carregarMedidasAtuais() {
    fetch('/api/medidas/meu-historico?limite=1')
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar medidas');
            return response.json();
        })
        .then(medidas => {
            if (medidas && medidas.length > 0) {
                const medida = medidas[0];
                
                document.getElementById('pesoAtual').textContent = medida.peso 
                    ? medida.peso.toFixed(1) 
                    : '-';
                document.getElementById('alturaAtual').textContent = medida.altura 
                    ? medida.altura.toFixed(1) 
                    : '-';
                document.getElementById('gorduraAtual').textContent = medida.percentualGordura 
                    ? medida.percentualGordura.toFixed(1) 
                    : '-';

                if (medida.peso && medida.altura) {
                    const imc = calcularIMC(medida.peso, medida.altura);
                    document.getElementById('imcAtual').textContent = imc.toFixed(1);
                }

                document.getElementById('ultimaMedidaData').textContent = formatarData(medida.data) || '-';
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            document.getElementById('pesoAtual').textContent = '-';
        });
}

/**
 * Calcula IMC
 */
function calcularIMC(peso, altura) {
    const alturaMetros = altura / 100;
    return peso / (alturaMetros * alturaMetros);
}

/**
 * Configura eventos da foto de perfil
 */
function configurarFoto() {
    const fotoPerfil = document.getElementById('fotoPerfil');
    const uploadFoto = document.getElementById('uploadFoto');

    // Clique esquerdo: visualizar foto
    fotoPerfil.addEventListener('click', (e) => {
        const img = document.getElementById('fotoImg').src;
        if (img) {
            document.getElementById('fotoModalImg').src = img;
            document.getElementById('fotoModal').style.display = 'flex';
        }
    });

    // Clique direito: upload de foto
    fotoPerfil.addEventListener('contextmenu', (e) => {
        e.preventDefault();
        uploadFoto.click();
    });

    // Evento de upload
    uploadFoto.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            enviarFoto(file);
        }
    });
}

/**
 * Envia a foto para o servidor
 */
function enviarFoto(file) {
    const formData = new FormData();
    formData.append('foto', file);

    fetch('/api/alunos/foto-perfil', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) throw new Error('Erro ao enviar foto');
        return response.blob();
    })
    .then(blob => {
        const url = URL.createObjectURL(blob);
        exibirFoto(url);
        showMessage('senhaMessage', '✅ Foto atualizada com sucesso!', 'success');
    })
    .catch(error => {
        console.error('Erro:', error);
        showMessage('senhaMessage', '❌ Erro ao enviar foto', 'error');
    });
}

/**
 * Exibe a foto de perfil
 */
function exibirFoto(url) {
    const img = document.getElementById('fotoImg');
    const placeholder = document.getElementById('fotoPlaceholder');
    
    img.src = url;
    img.style.display = 'block';
    placeholder.style.display = 'none';
}

/**
 * Fecha o modal da foto
 */
function fecharModalFoto() {
    document.getElementById('fotoModal').style.display = 'none';
}

/**
 * Configura o formulário de alteração de senha
 */
function configurarFormularioSenha() {
    document.getElementById('formAlterarSenha').addEventListener('submit', (e) => {
        e.preventDefault();
        alterarSenha();
    });
}

/**
 * Altera a senha do aluno
 */
function alterarSenha() {
    const senhaAtual = document.getElementById('senhaAtual').value;
    const novaSenha = document.getElementById('novaSenha').value;
    const confirmarSenha = document.getElementById('confirmarSenha').value;

    if (novaSenha !== confirmarSenha) {
        showMessage('senhaMessage', '❌ As senhas não conferem', 'error');
        return;
    }

    if (novaSenha.length < 6) {
        showMessage('senhaMessage', '❌ A nova senha deve ter no mínimo 6 caracteres', 'error');
        return;
    }

    const dados = {
        senhaAtual: senhaAtual,
        novaSenha: novaSenha
    };

    fetch('/api/alunos/alterar-senha', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(dados)
    })
    .then(response => {
        if (!response.ok) throw new Error('Erro ao alterar senha');
        return response.json();
    })
    .then(data => {
        showMessage('senhaMessage', '✅ Senha alterada com sucesso!', 'success');
        
        // Limpa o formulário
        document.getElementById('formAlterarSenha').reset();
        
        // Esconde mensagem após 3 segundos
        setTimeout(() => {
            document.getElementById('senhaMessage').innerHTML = '';
        }, 3000);
    })
    .catch(error => {
        console.error('Erro:', error);
        showMessage('senhaMessage', '❌ Erro ao alterar senha. Verifique sua senha atual', 'error');
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
 * Fecha o modal ao clicar fora da imagem
 */
document.addEventListener('click', (e) => {
    const modal = document.getElementById('fotoModal');
    if (e.target === modal) {
        modal.style.display = 'none';
    }
});
