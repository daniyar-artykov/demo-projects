package com.upwork.springmvc.service;

import com.upwork.springmvc.model.User;

public interface UserService {
	
    void save(User user);

    User findByUsername(String username);
}
