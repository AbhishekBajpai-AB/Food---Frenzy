package com.example.demo.controllers;

import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.loginCredentials.ContactForm;
import com.example.demo.services.ContactEmailService;

import jakarta.validation.Valid;

@Controller
public class ContactController {
	private final ContactEmailService contactEmailService;

	public ContactController(ContactEmailService contactEmailService) {
		this.contactEmailService = contactEmailService;
	}

	@PostMapping("/contact")
	public String send(@Valid @ModelAttribute("contactForm") ContactForm form,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) return locationWithError(model);

		try {
			contactEmailService.sendContactMessage(form);
			redirectAttributes.addFlashAttribute("contactSuccess", "Thanks! Your message has been sent successfully.");
		} catch (IllegalStateException | MailException exception) {
			model.addAttribute("contactError", "Your message could not be sent right now. Please email t04829970@gmail.com directly.");
			return "Locate_us";
		}
		return "redirect:/location";
	}

	private String locationWithError(Model model) {
		model.addAttribute("contactError", "Please check the highlighted fields and try again.");
		return "Locate_us";
	}
}
