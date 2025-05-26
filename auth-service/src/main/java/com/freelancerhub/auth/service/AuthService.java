package com.freelancerhub.auth.service;
import com.freelancerhub.auth.domain.User;
import com.freelancerhub.auth.domain.Role;
import com.freelancerhub.auth.repository.UserRepository;
import com.freelancerhub.auth.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public String register(String username, String email, String password) {
		if (userRepository.findByEmail(email).isPresent()) {
			throw new RuntimeException("El email ya está registrado.");
		}

		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password)); // encripta
		user.setRoles(Collections.singletonList(Role.USER)); // asigna rol por defecto

		userRepository.save(user);

		return jwtUtil.generateToken(email);
	}

	public String login(String email, String password) {
		Optional<User> userOpt = userRepository.findByEmail(email);

		if (userOpt.isEmpty()) {
			throw new RuntimeException("Usuario no encontrado.");
		}

		User user = userOpt.get();

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new RuntimeException("Contraseña incorrecta.");
		}

		return jwtUtil.generateToken(email);
	}
}