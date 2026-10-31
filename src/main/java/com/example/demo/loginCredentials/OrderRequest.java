package com.example.demo.loginCredentials;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class OrderRequest {
	@Min(value = 1, message = "Please select a product")
	private int productId;

	@Min(value = 1, message = "Quantity must be at least 1")
	@Max(value = 50, message = "A single order can contain at most 50 items")
	private int quantity = 1;

	public int getProductId() { return productId; }
	public void setProductId(int productId) { this.productId = productId; }
	public int getQuantity() { return quantity; }
	public void setQuantity(int quantity) { this.quantity = quantity; }
}
