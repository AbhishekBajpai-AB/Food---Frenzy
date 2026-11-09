package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer>
{
	@Query("select u from User u where lower(u.uemail) = lower(:email)")
	Optional<User> findByEmailIgnoreCase(@Param("email") String email);

	@Query("select case when count(u) > 0 then true else false end from User u where lower(u.uemail) = lower(:email)")
	boolean emailExistsIgnoreCase(@Param("email") String email);
}
