package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Product;
import com.example.demo.loginCredentials.OrderRequest;
import com.example.demo.services.OrderServices;
import com.example.demo.services.ProductServices;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ProductController {
	private final ProductServices productServices;
	private final OrderServices orderServices;

	public ProductController(ProductServices productServices, OrderServices orderServices) {
		this.productServices = productServices;
		this.orderServices = orderServices;
	}

	@GetMapping("/product/buy/{productId}")
	public String buy(@PathVariable("productId") int productId, Model model, HttpSession session) {
		model.addAttribute("product", productServices.getProduct(productId));
		model.addAttribute("orderRequest", new OrderRequest());
		model.addAttribute("orders", orderServices.getOrdersForUser((Integer) session.getAttribute("authId")));
		return "BuyProduct";
	}

	@PostMapping("/product/search")
	public String search(@RequestParam("productName") String name, Model model, HttpSession session) {
		model.addAttribute("searchTerm", name);
		model.addAttribute("searchResults", productServices.searchProducts(name));
		model.addAttribute("orders", orderServices.getOrdersForUser((Integer) session.getAttribute("authId")));
		model.addAttribute("orderRequest", new OrderRequest());
		return "BuyProduct";
	}

	@GetMapping("/addProduct")
	public String addProductPage(Model model) {
		model.addAttribute("product", new Product());
		return "Add_Product";
	}

	@PostMapping("/addingProduct")
	public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result) {
		if (product.getPname() != null && productServices.existsByName(product.getPname())) {
			result.rejectValue("pname", "duplicate", "A menu item with this name already exists.");
		}
		if (result.hasErrors()) return "Add_Product";
		productServices.addProduct(product);
		return "redirect:/admin/services";
	}

	@GetMapping("/updateProduct/{productId}")
	public String updateProductPage(@PathVariable("productId") int productId, Model model) {
		model.addAttribute("product", productServices.getProduct(productId));
		return "Update_Product";
	}

	@PostMapping("/updatingProduct/{productId}")
	public String updateProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
			@PathVariable("productId") int productId) {
		if (product.getPname() != null && productServices.existsByName(product.getPname())
				&& !productServices.getProduct(productId).getPname().equalsIgnoreCase(product.getPname())) {
			result.rejectValue("pname", "duplicate", "A menu item with this name already exists.");
		}
		if (result.hasErrors()) {
			product.setPid(productId);
			return "Update_Product";
		}
		productServices.updateProduct(product, productId);
		return "redirect:/admin/services";
	}

	@PostMapping("/deleteProduct/{productId}")
	public String deleteProduct(@PathVariable("productId") int productId) {
		productServices.deleteProduct(productId);
		return "redirect:/admin/services";
	}
}
