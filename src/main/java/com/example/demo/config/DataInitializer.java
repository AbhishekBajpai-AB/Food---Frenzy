package com.example.demo.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.entities.Admin;
import com.example.demo.entities.Product;
import com.example.demo.services.AdminServices;
import com.example.demo.services.ProductServices;

@Configuration
public class DataInitializer {
	@Bean
	CommandLineRunner seedDemoData(AdminServices adminServices, ProductServices productServices,
			@Value("${app.demo-admin.email:}") String adminEmail,
			@Value("${app.demo-admin.password:}") String adminPassword) {
		return args -> {
			if (adminServices.getAll().isEmpty()) {
				if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
					throw new IllegalStateException("No administrator account exists. Set DEMO_ADMIN_EMAIL and DEMO_ADMIN_PASSWORD to create the first administrator.");
				}
				Admin admin = new Admin();
				admin.setAdminName("FoodFrenzy Administrator");
				admin.setAdminEmail(adminEmail);
				admin.setAdminNumber("9999999999");
				admin.setAdminPassword(adminPassword);
				adminServices.addAdmin(admin);
			}
			if (productServices.getAllProducts().isEmpty()) {
				for (Product product : starterMenu()) productServices.addProduct(product);
			}
			productServices.normalizeLegacyProducts();
		};
	}

	private List<Product> starterMenu() {
		return List.of(
				product("Hyderabadi Chicken Biryani", "Biryani", 410, "Fragrant basmati rice with aromatic spices.", "/Images/biryani/Biryani_of_Hyderabadi.jpg"),
				product("Veg Dum Biryani", "Biryani", 280, "Slow-cooked rice with vegetables and whole spices.", "/Images/biryani/veg-dum-biryani.jpeg"),
				product("Egg Biryani", "Biryani", 320, "Spiced basmati rice served with egg.", "/Images/biryani/Egg-biryani.jpg"),
				product("Paneer Butter Masala", "Vegetarian", 290, "Paneer simmered in a creamy tomato gravy.", "/Images/paneer/paneer-butter-masala.jpg"),
				product("Kadai Paneer", "Vegetarian", 300, "Paneer tossed with peppers and kadai spices.", "/Images/paneer/Kadai-Paneer.jpg"),
				product("Paneer Do Pyaza", "Vegetarian", 270, "Paneer cooked with onions and Indian spices.", "/Images/paneer/paneer-do-pyaza.jpg"),
				product("Afghani Chicken Curry", "Chicken", 410, "A rich, mildly spiced chicken curry.", "/Images/chicken/Afgani-chicken-curry.jpg"),
				product("Butter Chicken", "Chicken", 380, "Tandoori chicken in a buttery tomato sauce.", "/Images/chicken/butter-chicken.jpg"),
				product("Chicken Tikka", "Chicken", 310, "Yogurt-marinated chicken roasted with spices.", "/Images/chicken/Tandoori-chicken-tikka.jpg"),
				product("Veg Momos", "Chinese", 150, "Steamed dumplings served with house chutney.", "/Images/chinese/momo.webp"),
				product("Chowmein", "Chinese", 150, "Stir-fried noodles with vegetables.", "/Images/chinese/chowmein.jpg"),
				product("Gulab Jamun", "Dessert", 50, "Soft milk dumplings in rose-scented syrup.", "/Images/north-india-food/Gulab Jamun.jpg"));
	}

	private Product product(String name, String category, int price, String description, String imageUrl) {
		Product product = new Product();
		product.setPname(name);
		product.setCategory(category);
		product.setPprice(BigDecimal.valueOf(price));
		product.setPdescription(description);
		product.setImageUrl(imageUrl);
		product.setStockQuantity(25);
		product.setActive(true);
		return product;
	}
}
