package com.example.demo.loginCredentials;

import com.example.demo.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRegistrationForm {
	@NotBlank(message = "Name is required")
	@Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
	private String uname;

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email address")
	private String uemail;

	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 72, message = "Password must be at least 8 characters")
	private String upassword;

	@NotNull(message = "Mobile number is required")
	@Min(value = 1000000000L, message = "Enter a 10 digit mobile number")
	@Max(value = 9999999999L, message = "Enter a 10 digit mobile number")
	private Long unumber;

	public String getUname() { return uname; }
	public void setUname(String uname) { this.uname = uname; }
	public String getUemail() { return uemail; }
	public void setUemail(String uemail) { this.uemail = uemail; }
	public String getUpassword() { return upassword; }
	public void setUpassword(String upassword) { this.upassword = upassword; }
	public Long getUnumber() { return unumber; }
	public void setUnumber(Long unumber) { this.unumber = unumber; }

	public User toUser() {
		User user = new User();
		user.setUname(uname);
		user.setUemail(uemail);
		user.setUpassword(upassword);
		user.setUnumber(unumber);
		return user;
	}
}
