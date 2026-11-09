package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entities.Product;
import com.example.demo.entities.User;
import com.example.demo.entities.Admin;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.services.AdminServices;
import com.example.demo.services.ProductServices;
import com.example.demo.services.UserServices;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:foodfrenzytest;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=USER",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=validate",
		"spring.flyway.enabled=true",
		"app.demo-admin.email=test-admin@foodfrenzy.local",
		"app.demo-admin.password=TestAdminPass123!",
		"spring.mail.host=localhost",
		"spring.mail.port=25",
		"spring.mail.username=",
		"app.contact.email=contact-test@example.com",
		"spring.devtools.add-properties=false"
})
@AutoConfigureMockMvc
@ActiveProfiles("prod")
@Transactional
class FoodFrenzyApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductServices productServices;

	@Autowired
	private UserServices userServices;

	@Autowired
	private AdminServices adminServices;

	@Autowired
	private OrderRepository orderRepository;

	@Test
	void publicPagesRenderAndProtectedPagesRedirect() throws Exception {
		mockMvc.perform(get("/products"))
				.andExpect(status().isOk());
		mockMvc.perform(get("/location"))
				.andExpect(status().isOk());
		mockMvc.perform(get("/actuator/health"))
				.andExpect(status().isOk());
		mockMvc.perform(get("/admin/services"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?required=admin"));
		mockMvc.perform(get("/product/orders"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?required=user"));
	}

	@Test
	void customerCanRegisterThroughThePublicForm() throws Exception {
		mockMvc.perform(post("/register")
				.with(csrf())
				.param("uname", "Registered Test Customer")
				.param("uemail", "registered-test@example.com")
				.param("upassword", "CustomerPass123!")
				.param("unumber", "7786000002"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/product/orders"));

		User registered = userServices.getUserByEmail("registered-test@example.com");
		assertNotNull(registered);
		assertNotEquals("CustomerPass123!", registered.getUpassword());

		mockMvc.perform(post("/register")
				.with(csrf())
				.param("uname", "Duplicate Test Customer")
				.param("uemail", "registered-test@example.com")
				.param("upassword", "CustomerPass123!")
				.param("unumber", "7786000004"))
				.andExpect(status().isOk())
				.andExpect(model().attributeHasFieldErrors("registration", "uemail"));
	}

	@Test
	void administratorCanLogInAndOpenTheDashboard() throws Exception {
		Admin admin = new Admin();
		admin.setAdminName("Deployment Test Admin");
		admin.setAdminEmail("deployment-admin@example.com");
		admin.setAdminNumber("7786000003");
		admin.setAdminPassword("AdminPass123!");
		admin = adminServices.addAdmin(admin);
		assertTrue(adminServices.emailExists("DEPLOYMENT-ADMIN@example.com"));

		MvcResult login = mockMvc.perform(post("/adminLogin")
				.with(csrf())
				.param("email", admin.getAdminEmail())
				.param("password", "AdminPass123!"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin/services"))
				.andReturn();

		MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
		mockMvc.perform(get("/admin/services").session(session))
				.andExpect(status().isOk());
	}

	@Test
	void customerCanLogInAndPlaceAnOrderAtTheDatabasePrice() throws Exception {
		User customer = new User();
		customer.setUname("Deployment Test Customer");
		customer.setUemail("deployment-test@example.com");
		customer.setUnumber(7786000000L);
		customer.setUpassword("CustomerPass123!");
		customer = userServices.register(customer);

		MvcResult login = mockMvc.perform(post("/userLogin")
				.with(csrf())
				.param("userEmail", customer.getUemail())
				.param("userPassword", "CustomerPass123!"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/product/orders"))
				.andReturn();
		MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

		Product product = productServices.getAvailableProducts().get(0);
		assertTrue(productServices.existsByName(product.getPname()));
		assertEquals(product.getPid(), productServices.getProductByName(product.getPname()).getPid());
		assertEquals(1, productServices.searchProducts(product.getPname()).size());
		int initialStock = product.getStockQuantity();
		BigDecimal expectedTotal = product.getPprice().multiply(BigDecimal.valueOf(2)).setScale(2);

		mockMvc.perform(post("/product/order")
				.session(session)
				.with(csrf())
				.param("productId", Integer.toString(product.getPid()))
				.param("quantity", "2")
				.param("price", "0.01"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/product/order-success"));

		var orders = orderRepository.findByUserOrderByOrderDateDesc(customer);
		assertEquals(1, orders.size());
		assertEquals(0, orders.get(0).getTotalAmount().compareTo(expectedTotal));
		assertEquals(initialStock - 2, productServices.getProduct(product.getPid()).getStockQuantity());
	}
}
