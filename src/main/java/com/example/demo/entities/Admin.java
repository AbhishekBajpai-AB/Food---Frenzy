package com.example.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "admin")
public class Admin
{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adminId;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 80)
    private String adminName;

    @NotBlank(message = "Email is required")
    @Email
    @Column(unique = true)
    private String adminEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be at least 8 characters")
    private String adminPassword;

    @jakarta.validation.constraints.NotBlank(message = "Phone number is required")
    @jakarta.validation.constraints.Pattern(regexp = "[0-9]{10}", message = "Enter a 10 digit mobile number")
    private String adminNumber;
public int getAdminId() {
	return adminId;
}
public void setAdminId(int adminId) {
	this.adminId = adminId;
}
public String getAdminName() {
	return adminName;
}
public void setAdminName(String adminName) {
	this.adminName = adminName;
}
public String getAdminEmail() {
	return adminEmail;
}
public void setAdminEmail(String adminEmail) {
	this.adminEmail = adminEmail;
}
public String getAdminPassword() {
	return adminPassword;
}
public void setAdminPassword(String adminPassword) {
	this.adminPassword = adminPassword;
}
public String getAdminNumber() {
	return adminNumber;
}
public void setAdminNumber(String adminNumber) {
	this.adminNumber = adminNumber;
}
@Override
public String toString() {
	return "Admin [adminId=" + adminId + ", adminName=" + adminName + ", adminEmail=" + adminEmail
			+ ", adminNumber=" + adminNumber + "]";
}





}
