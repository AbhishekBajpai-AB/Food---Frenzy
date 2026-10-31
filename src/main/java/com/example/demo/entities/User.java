package com.example.demo.entities;
import java.util.List;
import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user")
public class User 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int u_id;

	@NotBlank(message = "Name is required")
	@Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
	private String uname;
	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email address")
	@Column(unique = true)
	private String uemail;
	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 72, message = "Password must be at least 8 characters")
	private String upassword;
	@NotNull(message = "Mobile number is required")
	@Min(value = 1000000000L, message = "Enter a 10 digit mobile number")
	@Max(value = 9999999999L, message = "Enter a 10 digit mobile number")
	private Long unumber;
	
	@OneToMany(mappedBy = "user")
	private List<Orders> orders;
	
	
	public User() {

}
	public List<Orders> getOrders() {
		return orders;
	}
	public void setOrders(List<Orders> orders) {
		this.orders = orders;
	}
	public int getU_id() {
		return u_id;
	}
	public void setU_id(int u_id) {
		this.u_id = u_id;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;

	}
	public String getUemail() {
		return uemail;
	}
	public void setUemail(String uemail) {
		this.uemail = uemail;
	}
	public String getUpassword() {
		return upassword;
	}
	public void setUpassword(String upassword) {
		this.upassword = upassword;
	}
	public Long getUnumber() {
		return unumber;
	}
	public void setUnumber(Long unumber) {
		this.unumber = unumber;
	}
	@Override
	public String toString() {
		return "User [u_id=" + u_id + ", uname=" + uname + ", uemail=" + uemail
				+ ", unumber=" + unumber + "]";
	}

	 public User(String uemail, String upassword) {
	        this.uemail = uemail;
	        this.upassword = upassword;
	    }

}
