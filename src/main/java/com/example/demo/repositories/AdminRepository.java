package com.example.demo.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.Admin;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer>
{
	Optional<Admin> findByAdminEmailIgnoreCase(String email);
	boolean existsByAdminEmailIgnoreCase(String email);
}
