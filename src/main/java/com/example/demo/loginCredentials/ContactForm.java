package com.example.demo.loginCredentials;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactForm {
	@NotBlank(message = "Please enter your name.")
	@Size(max = 100, message = "Name must be 100 characters or fewer.")
	private String name;

	@NotBlank(message = "Please enter your email address.")
	@Email(message = "Enter a valid email address.")
	@Size(max = 254, message = "Email address is too long.")
	private String email;

	@NotBlank(message = "Please enter a subject.")
	@Size(max = 120, message = "Subject must be 120 characters or fewer.")
	private String subject;

	@NotBlank(message = "Please enter a message.")
	@Size(min = 10, max = 5000, message = "Message must be between 10 and 5000 characters.")
	private String message;

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getSubject() { return subject; }
	public void setSubject(String subject) { this.subject = subject; }
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
}
