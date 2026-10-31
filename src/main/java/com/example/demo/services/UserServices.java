package com.example.demo.services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.OrderRepository;

@Service
public class UserServices {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final OrderRepository orderRepository;

	public UserServices(UserRepository userRepository, PasswordEncoder passwordEncoder, OrderRepository orderRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.orderRepository = orderRepository;
	}

	public List<User> getAllUser() {
		return userRepository.findAll();
	}

	public User getUser(int id) {
		return userRepository.findById(id).orElseThrow();
	}

	public User getUserByEmail(String email) {
		return userRepository.findByUemailIgnoreCase(email).orElse(null);
	}

	public boolean emailExists(String email) {
		return email != null && userRepository.existsByUemailIgnoreCase(email.trim());
	}

	public User register(User user) {
		validateProfile(user, true);
		user.setU_id(0);
		user.setOrders(null);
		user.setUname(user.getUname().trim());
		user.setUemail(user.getUemail().trim().toLowerCase());
		user.setUpassword(passwordEncoder.encode(user.getUpassword()));
		return userRepository.save(user);
	}

	@Transactional
	public void updateUser(User submitted, int id) {
		validateProfile(submitted, false);
		User current = getUser(id);
		current.setUname(submitted.getUname().trim());
		current.setUemail(submitted.getUemail().trim().toLowerCase());
		current.setUnumber(submitted.getUnumber());
		if (submitted.getUpassword() != null && !submitted.getUpassword().isBlank()) {
			current.setUpassword(passwordEncoder.encode(submitted.getUpassword()));
		}
		userRepository.save(current);
	}

	@Transactional
	public void deleteUser(int id) {
		if (orderRepository.countForUser(id) > 0) {
			throw new IllegalStateException("This customer has order history and cannot be deleted.");
		}
		userRepository.deleteById(id);
	}

	private void validateProfile(User user, boolean requirePassword) {
		if (user.getUname() == null || user.getUname().trim().length() < 2 || user.getUname().trim().length() > 80) {
			throw new IllegalArgumentException("Name must be between 2 and 80 characters.");
		}
		if (user.getUemail() == null || !user.getUemail().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			throw new IllegalArgumentException("Enter a valid email address.");
		}
		if (user.getUnumber() == null || user.getUnumber() < 1000000000L || user.getUnumber() > 9999999999L) {
			throw new IllegalArgumentException("Enter a 10 digit mobile number.");
		}
		String password = user.getUpassword();
		if ((requirePassword || password != null && !password.isBlank())
				&& (password == null || password.length() < 8 || password.length() > 72)) {
			throw new IllegalArgumentException("Password must be between 8 and 72 characters.");
		}
	}

	@Transactional
	public User authenticate(String email, String rawPassword) {
		if (email == null || rawPassword == null) return null;
		User user = getUserByEmail(email.trim());
		if (user == null || user.getUpassword() == null) return null;
		String stored = user.getUpassword();
		boolean encoded = stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$");
		boolean matches = encoded ? passwordEncoder.matches(rawPassword, stored) : stored.equals(rawPassword);
		if (!matches) return null;
		if (!encoded) {
			user.setUpassword(passwordEncoder.encode(rawPassword));
			userRepository.save(user);
		}
		return user;
	}
}
