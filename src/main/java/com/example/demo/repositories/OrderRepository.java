package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import com.example.demo.entities.Orders;
import com.example.demo.entities.User;

public interface OrderRepository extends JpaRepository<Orders, Integer>
{
	List<Orders> findOrdersByUser(User user);
	List<Orders> findByUserOrderByOrderDateDesc(User user);

	@Query("select count(o) from Orders o where o.user.u_id = :userId")
	long countForUser(@Param("userId") int userId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select o from Orders o where o.oId = :orderId")
	java.util.Optional<Orders> findByIdForUpdate(@Param("orderId") int orderId);

	@EntityGraph(attributePaths = "user")
	@Query("select o from Orders o order by o.orderDate desc")
	List<Orders> findAllWithUser();
}
