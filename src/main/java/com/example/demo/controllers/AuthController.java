package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Admin;
import com.example.demo.entities.User;
import com.example.demo.loginCredentials.AdminLogin;
import com.example.demo.loginCredentials.UserLogin;
import com.example.demo.loginCredentials.UserRegistrationForm;
import com.example.demo.services.AdminServices;
import com.example.demo.services.UserServices;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {
	private final AdminServices adminServices;
	private final UserServices userServices;

	public AuthController(AdminServices adminServices, UserServices userServices) {
		this.adminServices = adminServices;
		this.userServices = userServices;
	}

	@GetMapping("/register")
	public String registerPage(Model model) {
		model.addAttribute("registration", new UserRegistrationForm());
		return "register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("registration") UserRegistrationForm registration, BindingResult result,
			HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if (registration.getUemail() != null && userServices.emailExists(registration.getUemail().trim())) {
			result.rejectValue("uemail", "duplicate", "An account with this email already exists.");
		}
		if (result.hasErrors()) return "register";

		User saved = userServices.register(registration.toUser());
		startSession(request, "USER", saved.getU_id(), saved.getUname());
		redirectAttributes.addFlashAttribute("successMessage", "Your account is ready. Welcome to FoodFrenzy!");
		return "redirect:/product/orders";
	}

	@PostMapping("/adminLogin")
	public String adminLogin(@ModelAttribute("adminLogin") AdminLogin login, HttpServletRequest request, Model model) {
		Admin admin = adminServices.authenticate(login.getEmail(), login.getPassword());
		if (admin == null) {
			model.addAttribute("error", "Invalid administrator email or password.");
			model.addAttribute("userLogin", new UserLogin());
			return "Login";
		}
		startSession(request, "ADMIN", admin.getAdminId(), admin.getAdminName());
		return "redirect:/admin/services";
	}

	@PostMapping("/userLogin")
	public String userLogin(@ModelAttribute("userLogin") UserLogin login, HttpServletRequest request, Model model) {
		User user = userServices.authenticate(login.getUserEmail(), login.getUserPassword());
		if (user == null) {
			model.addAttribute("error2", "Invalid customer email or password.");
			model.addAttribute("adminLogin", new AdminLogin());
			return "Login";
		}
		startSession(request, "USER", user.getU_id(), user.getUname());
		return "redirect:/product/orders";
	}

	private void startSession(HttpServletRequest request, String role, int id, String name) {
		request.getSession(true);
		request.changeSessionId();
		HttpSession session = request.getSession(true);
		session.setAttribute("authRole", role);
		session.setAttribute("authId", id);
		session.setAttribute("authName", name);
	}
}
