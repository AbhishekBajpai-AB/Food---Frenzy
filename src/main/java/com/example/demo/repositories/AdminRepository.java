package com.example.demo.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Admin;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer>
{
	@Query("select a from Admin a where lower(a.adminEmail) = lower(:email)")
	Optional<Admin> findByEmailIgnoreCase(@Param("email") String email);

	@Query("select case when count(a) > 0 then true else false end from Admin a where lower(a.adminEmail) = lower(:email)")
	boolean emailExistsIgnoreCase(@Param("email") String email);
}
