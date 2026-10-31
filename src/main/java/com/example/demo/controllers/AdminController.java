package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Admin;
import com.example.demo.entities.OrderStatus;
import com.example.demo.services.AdminServices;
import com.example.demo.services.OrderServices;
import com.example.demo.services.ProductServices;
import com.example.demo.services.UserServices;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {
	private final UserServices userServices;
	private final AdminServices adminServices;
	private final ProductServices productServices;
	private final OrderServices orderServices;

	public AdminController(UserServices userServices, AdminServices adminServices,
			ProductServices productServices, OrderServices orderServices) {
		this.userServices = userServices;
		this.adminServices = adminServices;
		this.productServices = productServices;
		this.orderServices = orderServices;
	}

	@GetMapping("/admin/services")
	public String dashboard(Model model) {
		model.addAttribute("users", userServices.getAllUser());
		model.addAttribute("admins", adminServices.getAll());
		model.addAttribute("products", productServices.getAllProducts());
		model.addAttribute("orders", orderServices.getOrders());
		return "Admin_Page";
	}

	@GetMapping("/addAdmin")
	public String addAdminPage(Model model) {
		model.addAttribute("admin", new Admin());
		return "Add_Admin";
	}

	@GetMapping("/updateAdmin/{adminId}")
	public String updateAdminPage(@PathVariable("adminId") int adminId, Model model) {
		model.addAttribute("admin", adminServices.getAdmin(adminId));
		return "Update_Admin";
	}

	@PostMapping("/addingAdmin")
	public String addAdmin(@Valid @ModelAttribute("admin") Admin admin, BindingResult result) {
		if (admin.getAdminEmail() != null && adminServices.emailExists(admin.getAdminEmail())) {
			result.rejectValue("adminEmail", "duplicate", "An administrator with this email already exists.");
		}
		if (result.hasErrors()) return "Add_Admin";
		adminServices.addAdmin(admin);
		return "redirect:/admin/services";
	}

	@PostMapping("/updatingAdmin/{id}")
	public String updateAdmin(@ModelAttribute("admin") Admin admin, @PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		if (admin.getAdminEmail() != null && adminServices.emailExists(admin.getAdminEmail())
				&& !adminServices.getAdmin(id).getAdminEmail().equalsIgnoreCase(admin.getAdminEmail())) {
			redirectAttributes.addFlashAttribute("errorMessage", "Another administrator already uses that email.");
			return "redirect:/updateAdmin/" + id;
		}
		try {
			adminServices.update(admin, id);
		} catch (IllegalArgumentException exception) {
			redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
			return "redirect:/updateAdmin/" + id;
		}
		return "redirect:/admin/services";
	}

	@PostMapping("/deleteAdmin/{id}")
	public String deleteAdmin(@PathVariable("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
		if (Integer.valueOf(id).equals(session.getAttribute("authId"))) {
			redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete the administrator account currently in use.");
			return "redirect:/admin/services";
		}
		adminServices.delete(id);
		return "redirect:/admin/services";
	}

	@PostMapping("/admin/orders/{id}/status")
	public String updateOrderStatus(@PathVariable("id") int id, @RequestParam("status") OrderStatus status, RedirectAttributes redirectAttributes) {
		try {
			orderServices.updateStatus(id, status);
			redirectAttributes.addFlashAttribute("successMessage", "Order status updated.");
		} catch (IllegalArgumentException exception) {
			redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
		}
		return "redirect:/admin/services#orders";
	}
}
