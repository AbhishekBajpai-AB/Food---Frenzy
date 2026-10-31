package com.example.demo.entities;
import java.util.Date;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Table(name = "orders")
public class Orders
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int oId;
	private String oName;
	@Column(precision = 10, scale = 2)
	private BigDecimal oPrice;
	private int oQuantity;
	@Temporal(jakarta.persistence.TemporalType.TIMESTAMP)
	private Date orderDate;
	@Column(name = "totalAmmout", precision = 12, scale = 2)
	private BigDecimal totalAmount;
	@Enumerated(EnumType.STRING)
	@Column(length = 24)
	private OrderStatus status = OrderStatus.PLACED;
	
	@ManyToOne
	@JoinColumn(name="user_u_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "product_pid")
	private Product product;

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	

	public int getoId() {
		return oId;
	}

	public void setoId(int oId) {
		this.oId = oId;
	}

	public String getoName() {
		return oName;
	}

	public void setoName(String oName) {
		this.oName = oName;
	}

	public BigDecimal getoPrice() {
		return oPrice;
	}

	public void setoPrice(BigDecimal oPrice) {
		this.oPrice = oPrice;
	}

	public int getoQuantity() {
		return oQuantity;
	}

	public void setoQuantity(int oQuantity) {
		this.oQuantity = oQuantity;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() { return product; }
	public void setProduct(Product product) { this.product = product; }
	

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public OrderStatus getStatus() { return status == null ? OrderStatus.PLACED : status; }
	public void setStatus(OrderStatus status) { this.status = status; }

	@Override
	public String toString() {
		return "Orders [oId=" + oId + ", oName=" + oName + ", oPrice=" + oPrice + ", oQuantity=" + oQuantity
			+ ", orderDate=" + orderDate + ", totalAmount=" + totalAmount + ", status=" + status + "]";
	}


}
