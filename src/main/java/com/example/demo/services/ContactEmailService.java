package com.example.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.loginCredentials.ContactForm;

@Service
public class ContactEmailService {
	private final JavaMailSender mailSender;
	private final String recipient;
	private final String sender;

	public ContactEmailService(JavaMailSender mailSender,
			@Value("${app.contact.email}") String recipient,
			@Value("${spring.mail.username:}") String sender) {
		this.mailSender = mailSender;
		this.recipient = recipient;
		this.sender = sender;
	}

	public void sendContactMessage(ContactForm form) {
		if (sender == null || sender.isBlank()) {
			throw new IllegalStateException("Website email is not configured yet.");
		}
		SimpleMailMessage email = new SimpleMailMessage();
		email.setFrom(sender);
		email.setTo(recipient);
		email.setReplyTo(form.getEmail().trim());
		email.setSubject("FoodFrenzy website message: " + cleanHeader(form.getSubject()));
		email.setText("A visitor sent a message from the FoodFrenzy contact page.\n\n"
				+ "Name: " + form.getName().trim() + "\n"
				+ "Email: " + form.getEmail().trim() + "\n"
				+ "Subject: " + form.getSubject().trim() + "\n\n"
				+ form.getMessage().trim());
		mailSender.send(email);
	}

	private String cleanHeader(String value) {
		return value == null ? "New message" : value.replaceAll("[\\r\\n]", " ").trim();
	}
}
