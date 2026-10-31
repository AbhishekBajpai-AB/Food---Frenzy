package com.example.demo.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "product_table")
public class Product
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pid;
	@NotBlank(message = "Product name is required")
	@Size(max = 100)
	@Column(unique = true)
	private String pname;
	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.01", message = "Price must be greater than zero")
	@Column(precision = 10, scale = 2)
	private BigDecimal pprice;
	private String pdescription;
	@NotBlank(message = "Category is required")
	private String category = "Main Course";
	private String imageUrl = "/Images/logo.png";
	@PositiveOrZero(message = "Stock cannot be negative")
	private int stockQuantity = 0;
	private boolean active = true;

	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public BigDecimal getPprice() {
		return pprice;
	}
	public void setPprice(BigDecimal pprice) {
		this.pprice = pprice;
	}
	public String getPdescription() {
		return pdescription;
	}
	public void setPdescription(String pdescription) {
		this.pdescription = pdescription;
	}
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	public String getImageUrl() { return imageUrl; }
	public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
	public int getStockQuantity() { return stockQuantity; }
	public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
	@Override
	public String toString() {
		return "Product [pid=" + pid + ", pname=" + pname + ", pprice=" + pprice
				+ ", category=" + category + ", stockQuantity=" + stockQuantity + "]";
	}



}
