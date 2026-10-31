package com.example.demo;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class Exceptions {
	private static final Logger log = LoggerFactory.getLogger(Exceptions.class);

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String notFound(Model model) {
		model.addAttribute("errorTitle", "We couldn't find that page.");
		model.addAttribute("errorMessage", "The item may have been removed or the address may be out of date.");
		return "exception";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handler(Exception exception, Model model) {
		log.error("Unexpected application error", exception);
		model.addAttribute("errorTitle", "Something went wrong.");
		model.addAttribute("errorMessage", "Please return to the menu or try again in a moment.");
		return "exception";
	}
}
