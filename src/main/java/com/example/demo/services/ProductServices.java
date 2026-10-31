package com.example.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Product;
import com.example.demo.repositories.ProductRepository;

@Service
public class ProductServices {
	private final ProductRepository productRepository;

	public ProductServices(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product addProduct(Product product) {
		normalize(product);
		product.setPid(0);
		return productRepository.save(product);
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public List<Product> getAvailableProducts() {
		return productRepository.findByActiveTrueOrderByCategoryAscPnameAsc();
	}

	public Product getProduct(int id) {
		return productRepository.findById(id).orElseThrow();
	}

	public List<Product> searchProducts(String name) {
		return productRepository.findByPnameContainingIgnoreCaseAndActiveTrue(name.trim());
	}

	public Product getProductByName(String name) {
		return productRepository.findByPnameIgnoreCase(name.trim()).orElse(null);
	}

	public boolean existsByName(String name) {
		return name != null && productRepository.existsByPnameIgnoreCase(name.trim());
	}

	@Transactional
	public void normalizeLegacyProducts() {
		for (Product product : getAllProducts()) {
			boolean legacyRecord = product.getCategory() == null && product.getImageUrl() == null;
			if (product.getCategory() == null || product.getCategory().isBlank()) product.setCategory("Main Course");
			if (product.getImageUrl() == null || product.getImageUrl().isBlank()) product.setImageUrl("/Images/logo.png");
			if (legacyRecord && product.getStockQuantity() == 0) {
				product.setStockQuantity(25);
				product.setActive(true);
			}
			productRepository.save(product);
		}
	}

	@Transactional
	public void updateProduct(Product submitted, int id) {
		Product current = productRepository.findByIdForUpdate(id).orElseThrow();
		current.setPname(submitted.getPname());
		current.setPprice(submitted.getPprice());
		current.setPdescription(submitted.getPdescription());
		current.setCategory(submitted.getCategory());
		current.setImageUrl(submitted.getImageUrl());
		current.setStockQuantity(submitted.getStockQuantity());
		current.setActive(submitted.isActive());
		normalize(current);
		productRepository.save(current);
	}

	@Transactional
	public void deleteProduct(int id) {
		archiveProduct(id);
	}

	@Transactional
	public void archiveProduct(int id) {
		Product product = productRepository.findByIdForUpdate(id).orElseThrow();
		product.setActive(false);
		productRepository.save(product);
	}

	private void normalize(Product product) {
		product.setPname(product.getPname().trim());
		product.setCategory(product.getCategory().trim());
		if (product.getImageUrl() == null || product.getImageUrl().isBlank()) {
			product.setImageUrl("/Images/logo.png");
		} else {
			product.setImageUrl(product.getImageUrl().trim());
			if (!product.getImageUrl().startsWith("/")) product.setImageUrl("/" + product.getImageUrl());
		}
	}
}
