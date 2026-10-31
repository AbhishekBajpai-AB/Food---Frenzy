package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Orders;
import com.example.demo.loginCredentials.OrderRequest;
import com.example.demo.services.OrderServices;
import com.example.demo.services.ProductServices;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.FieldError;

@Controller
public class OrderController {
	private final OrderServices orderServices;
	private final ProductServices productServices;

	public OrderController(OrderServices orderServices, ProductServices productServices) {
		this.orderServices = orderServices;
		this.productServices = productServices;
	}

	@PostMapping("/product/order")
	public String placeOrder(@Valid @ModelAttribute("orderRequest") OrderRequest request,
			BindingResult result, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			if (request.getProductId() < 1) {
				redirectAttributes.addFlashAttribute("orderError", "Please choose a menu item before placing an order.");
				return "redirect:/products";
			}
			model.addAttribute("product", productServices.getProduct(request.getProductId()));
			model.addAttribute("orders", orderServices.getOrdersForUser((Integer) session.getAttribute("authId")));
			model.addAttribute("orderError", result.getFieldErrors().stream().map(FieldError::getDefaultMessage)
					.findFirst().orElse("Check the quantity and try again."));
			return "BuyProduct";
		}
		try {
			Orders order = orderServices.placeOrder(request.getProductId(), request.getQuantity(),
					(Integer) session.getAttribute("authId"));
			redirectAttributes.addFlashAttribute("order", order);
			return "redirect:/product/order-success";
		} catch (IllegalArgumentException exception) {
			model.addAttribute("product", productServices.getProduct(request.getProductId()));
			model.addAttribute("orders", orderServices.getOrdersForUser((Integer) session.getAttribute("authId")));
			model.addAttribute("orderError", exception.getMessage());
			return "BuyProduct";
		}
	}

	@GetMapping("/product/order-success")
	public String orderSuccess(Model model, HttpSession session, @RequestParam(name = "orderId", required = false) Integer orderId) {
		if (!model.containsAttribute("order")) {
			var orders = orderServices.getOrdersForUser((Integer) session.getAttribute("authId"));
			Orders latest = orders.stream().filter(order -> orderId == null || order.getoId() == orderId).findFirst().orElse(null);
			if (latest == null) return "redirect:/product/orders";
			model.addAttribute("order", latest);
		}
		return "Order_success";
	}

	@GetMapping("/product/orders")
	public String orders(Model model, HttpSession session) {
		model.addAttribute("orders", orderServices.getOrdersForUser((Integer) session.getAttribute("authId")));
		model.addAttribute("orderRequest", new OrderRequest());
		return "BuyProduct";
	}

	@GetMapping("/product/back")
	public String back() { return "redirect:/product/orders"; }
}
