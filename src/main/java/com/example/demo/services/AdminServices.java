package com.example.demo.services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Admin;
import com.example.demo.repositories.AdminRepository;

@Service
public class AdminServices {
	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	public AdminServices(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<Admin> getAll() {
		return adminRepository.findAll();
	}

	public Admin getAdmin(int id) {
		return adminRepository.findById(id).orElseThrow();
	}

	public boolean emailExists(String email) {
		return email != null && adminRepository.emailExistsIgnoreCase(email.trim());
	}

	public Admin authenticate(String email, String rawPassword) {
		if (email == null || rawPassword == null) return null;
		Admin admin = adminRepository.findByEmailIgnoreCase(email.trim()).orElse(null);
		if (admin == null || admin.getAdminPassword() == null) return null;
		String stored = admin.getAdminPassword();
		boolean encoded = stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$");
		boolean matches = encoded ? passwordEncoder.matches(rawPassword, stored) : stored.equals(rawPassword);
		if (matches && !encoded) {
			admin.setAdminPassword(passwordEncoder.encode(rawPassword));
			adminRepository.save(admin);
		}
		return matches ? admin : null;
	}

	public Admin addAdmin(Admin admin) {
		validateProfile(admin, true);
		admin.setAdminId(0);
		admin.setAdminName(admin.getAdminName().trim());
		admin.setAdminEmail(admin.getAdminEmail().trim().toLowerCase());
		admin.setAdminPassword(passwordEncoder.encode(admin.getAdminPassword()));
		return adminRepository.save(admin);
	}

	@Transactional
	public void update(Admin submitted, int id) {
		validateProfile(submitted, false);
		Admin current = getAdmin(id);
		current.setAdminName(submitted.getAdminName().trim());
		current.setAdminEmail(submitted.getAdminEmail().trim().toLowerCase());
		current.setAdminNumber(submitted.getAdminNumber());
		if (submitted.getAdminPassword() != null && !submitted.getAdminPassword().isBlank()) {
			current.setAdminPassword(passwordEncoder.encode(submitted.getAdminPassword()));
		}
		adminRepository.save(current);
	}

	public void delete(int id) {
		adminRepository.deleteById(id);
	}

	public boolean existsByEmail(String email) {
		return emailExists(email);
	}

	private void validateProfile(Admin admin, boolean requirePassword) {
		if (admin.getAdminName() == null || admin.getAdminName().trim().length() < 2 || admin.getAdminName().trim().length() > 80) {
			throw new IllegalArgumentException("Name must be between 2 and 80 characters.");
		}
		if (admin.getAdminEmail() == null || !admin.getAdminEmail().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			throw new IllegalArgumentException("Enter a valid email address.");
		}
		if (admin.getAdminNumber() == null || !admin.getAdminNumber().matches("[0-9]{10}")) {
			throw new IllegalArgumentException("Enter a 10 digit mobile number.");
		}
		String password = admin.getAdminPassword();
		if ((requirePassword || password != null && !password.isBlank())
				&& (password == null || password.length() < 8 || password.length() > 72)) {
			throw new IllegalArgumentException("Password must be between 8 and 72 characters.");
		}
	}
}
