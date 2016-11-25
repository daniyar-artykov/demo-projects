package com.upwork.springmvc.service;

public interface SecurityService {
	
    String findLoggedInUsername();

    void autologin(String username, String password);
}
