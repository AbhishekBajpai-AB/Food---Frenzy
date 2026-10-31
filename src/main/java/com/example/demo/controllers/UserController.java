package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.User;
import com.example.demo.services.UserServices;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class UserController {
	private final UserServices userServices;

	public UserController(UserServices userServices) { this.userServices = userServices; }

	@GetMapping("/addUser")
	public String addUserPage(Model model) {
		model.addAttribute("user", new User());
		return "Add_User";
	}

	@PostMapping("/addingUser")
	public String addUser(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {
		if (user.getUemail() != null && userServices.emailExists(user.getUemail())) {
			result.rejectValue("uemail", "duplicate", "A customer with this email already exists.");
		}
		if (result.hasErrors()) return "Add_User";
		userServices.register(user);
		redirectAttributes.addFlashAttribute("successMessage", "Customer account created.");
		return "redirect:/admin/services";
	}

	@GetMapping("/updateUser/{userId}")
	public String updateUserPage(@PathVariable("userId") int userId, Model model) {
		model.addAttribute("user", userServices.getUser(userId));
		return "Update_User";
	}

	@PostMapping("/updatingUser/{id}")
	public String updateUser(@ModelAttribute("user") User user, @PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		if (user.getUemail() != null && userServices.emailExists(user.getUemail())
				&& !userServices.getUser(id).getUemail().equalsIgnoreCase(user.getUemail())) {
			redirectAttributes.addFlashAttribute("errorMessage", "Another customer already uses that email.");
			return "redirect:/updateUser/" + id;
		}
		try {
			userServices.updateUser(user, id);
		} catch (IllegalArgumentException exception) {
			redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
			return "redirect:/updateUser/" + id;
		}
		return "redirect:/admin/services";
	}

	@PostMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
		if (Integer.valueOf(id).equals(session.getAttribute("authId")) && "USER".equals(session.getAttribute("authRole"))) {
			redirectAttributes.addFlashAttribute("errorMessage", "You cannot remove the customer account currently in use.");
			return "redirect:/admin/services";
		}
		try {
			userServices.deleteUser(id);
		} catch (IllegalStateException exception) {
			redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
		}
		return "redirect:/admin/services";
	}
}
