package com.superfit.superfitapp.service;

import com.superfit.superfitapp.dto.admin.ChangePasswordDTO;
import com.superfit.superfitapp.model.Role;
import com.superfit.superfitapp.model.User;
import com.superfit.superfitapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class GestorService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public GestorService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void alterarSenha(String emailGestor, ChangePasswordDTO dto) {
		User gestor = userRepository.findByEmail(emailGestor)
				.orElseThrow(() -> new RuntimeException("Gestor não encontrado"));

		if (gestor.getRole() != Role.ROLE_GESTOR) {
			throw new RuntimeException("Usuário não é GESTOR");
		}

		if (!passwordEncoder.matches(dto.senhaAtual(), gestor.getPassword())) {
			throw new RuntimeException("Senha atual inválida");
		}

		gestor.setPassword(passwordEncoder.encode(dto.novaSenha()));
		userRepository.save(gestor);
	}
}
