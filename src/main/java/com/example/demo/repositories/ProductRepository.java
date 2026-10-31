package com.example.demo.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Product;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product,Integer>
{
	Optional<Product> findByPnameIgnoreCase(String name);
	List<Product> findByActiveTrueOrderByCategoryAscPnameAsc();
	List<Product> findByPnameContainingIgnoreCaseAndActiveTrue(String name);
	boolean existsByPnameIgnoreCase(String name);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Product p where p.pid = :id")
	Optional<Product> findByIdForUpdate(@Param("id") int id);

}
