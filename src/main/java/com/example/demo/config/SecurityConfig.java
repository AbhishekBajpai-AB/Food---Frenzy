package com.example.demo.config;

import jakarta.servlet.http.HttpSession;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/actuator/health").permitAll()
				.requestMatchers("/admin/**", "/addAdmin", "/addingAdmin", "/updateAdmin/**", "/updatingAdmin/**", "/deleteAdmin/**",
						"/addProduct", "/addingProduct", "/updateProduct/**", "/updatingProduct/**", "/deleteProduct/**",
						"/addUser", "/addingUser", "/updateUser/**", "/updatingUser/**", "/deleteUser/**")
				.access(hasSessionRole("ADMIN"))
				.requestMatchers("/product/**").access(hasSessionRole("USER"))
				.anyRequest().permitAll());
		http.exceptionHandling(errors -> errors
				.authenticationEntryPoint((request, response, exception) -> redirectToSignIn(request.getRequestURI(), request.getContextPath(), response))
				.accessDeniedHandler((request, response, exception) -> redirectToSignIn(request.getRequestURI(), request.getContextPath(), response)));
		http.formLogin(form -> form.disable());
		http.httpBasic(basic -> basic.disable());
		http.logout(logout -> logout.logoutSuccessUrl("/home").invalidateHttpSession(true).clearAuthentication(true));
		return http.build();
	}

	private AuthorizationManager<RequestAuthorizationContext> hasSessionRole(String expectedRole) {
		return (authentication, context) -> {
			HttpSession session = context.getRequest().getSession(false);
			return new AuthorizationDecision(session != null && expectedRole.equals(session.getAttribute("authRole")));
		};
	}

	private void redirectToSignIn(String path, String contextPath, jakarta.servlet.http.HttpServletResponse response)
			throws java.io.IOException {
		String requiredRole = path.startsWith(contextPath + "/admin/") || path.contains("Admin")
				|| path.contains("Product") || path.contains("User") ? "admin" : "user";
		response.sendRedirect(contextPath + "/login?required=" + requiredRole);
	}
}
