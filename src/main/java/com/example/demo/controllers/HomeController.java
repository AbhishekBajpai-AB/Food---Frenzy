package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.loginCredentials.AdminLogin;
import com.example.demo.loginCredentials.ContactForm;
import com.example.demo.loginCredentials.UserLogin;
import com.example.demo.services.ProductServices;

@Controller
public class HomeController {
	private final ProductServices productServices;

	public HomeController(ProductServices productServices) {
		this.productServices = productServices;
	}

	@GetMapping({"/home", "/"})
	public String home() { return "Home"; }

	@GetMapping("/products")
	public String products(Model model) {
		model.addAttribute("products", productServices.getAvailableProducts());
		return "Products";
	}

	@GetMapping("/location")
	public String location(Model model) {
		model.addAttribute("contactForm", new ContactForm());
		return "Locate_us";
	}

	@GetMapping("/about")
	public String about() { return "About"; }

	@GetMapping("/login")
	public String login(Model model, @RequestParam(name = "required", required = false) String required) {
		model.addAttribute("adminLogin", new AdminLogin());
		model.addAttribute("userLogin", new UserLogin());
		if ("admin".equals(required)) model.addAttribute("requiredMessage", "Please log in as an administrator to continue.");
		if ("user".equals(required)) model.addAttribute("requiredMessage", "Please log in as a customer to place an order.");
		return "Login";
	}
}
