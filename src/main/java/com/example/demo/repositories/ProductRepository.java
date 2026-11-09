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
	@Query("select p from Product p where lower(p.pname) = lower(:name)")
	Optional<Product> findByNameIgnoreCase(@Param("name") String name);
	List<Product> findByActiveTrueOrderByCategoryAscPnameAsc();
	@Query("select p from Product p where p.active = true and lower(p.pname) like lower(concat(concat('%', :name), '%'))")
	List<Product> searchAvailableByName(@Param("name") String name);

	@Query("select case when count(p) > 0 then true else false end from Product p where lower(p.pname) = lower(:name)")
	boolean nameExistsIgnoreCase(@Param("name") String name);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Product p where p.pid = :id")
	Optional<Product> findByIdForUpdate(@Param("id") int id);

}
