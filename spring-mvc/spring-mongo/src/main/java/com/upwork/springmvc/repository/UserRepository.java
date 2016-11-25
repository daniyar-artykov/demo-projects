package com.upwork.springmvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upwork.springmvc.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
    User findByUsername(String username);
}
