package com.example.demo.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Orders;
import com.example.demo.entities.OrderStatus;
import com.example.demo.entities.Product;
import com.example.demo.entities.User;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;

@Service
public class OrderServices {
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public OrderServices(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
	}

	public List<Orders> getOrders() {
		return orderRepository.findAllWithUser();
	}

	public List<Orders> getOrdersForUser(int userId) {
		User user = userRepository.findById(userId).orElseThrow();
		return orderRepository.findByUserOrderByOrderDateDesc(user);
	}

	@Transactional
	public void updateStatus(int orderId, OrderStatus nextStatus) {
		Orders order = orderRepository.findByIdForUpdate(orderId).orElseThrow();
		if (order.getStatus() == null) order.setStatus(OrderStatus.PLACED);
		if (order.getStatus() == nextStatus) return;
		if (!order.getStatus().canMoveTo(nextStatus)) {
			throw new IllegalArgumentException("That order status change is not allowed.");
		}
		if (nextStatus == OrderStatus.CANCELLED && order.getProduct() != null) {
			Product product = productRepository.findByIdForUpdate(order.getProduct().getPid()).orElse(null);
			if (product != null) {
				product.setStockQuantity(product.getStockQuantity() + order.getoQuantity());
				productRepository.save(product);
			}
		}
		order.setStatus(nextStatus);
		orderRepository.save(order);
	}

	@Transactional
	public Orders placeOrder(int productId, int quantity, int userId) {
		Product product = productRepository.findByIdForUpdate(productId)
				.orElseThrow(() -> new IllegalArgumentException("This menu item is no longer available."));
		if (!product.isActive()) throw new IllegalArgumentException("This menu item is no longer available.");
		if (quantity < 1 || quantity > 50) throw new IllegalArgumentException("Choose a quantity between 1 and 50.");
		if (product.getStockQuantity() < quantity) throw new IllegalArgumentException("There is not enough stock for that quantity.");

		User user = userRepository.findById(userId).orElseThrow();
		product.setStockQuantity(product.getStockQuantity() - quantity);
		productRepository.save(product);

		Orders order = new Orders();
		order.setoName(product.getPname());
		order.setoPrice(product.getPprice());
		order.setoQuantity(quantity);
		order.setTotalAmount(product.getPprice().multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP));
		order.setOrderDate(new Date());
		order.setUser(user);
		order.setProduct(product);
		order.setStatus(OrderStatus.PLACED);
		return orderRepository.save(order);
	}
}
