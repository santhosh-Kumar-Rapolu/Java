package com.application.qrcodegenerator.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.application.qrcodegenerator.model.User;
import com.application.qrcodegenerator.model.UserDetailsUpdateRequestBody;
import com.application.qrcodegenerator.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder; 
	
	public User createUser(User user) {
		User newUser = new User();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(passwordEncoder.encode(user.getPassword()));
		newUser.setCompanyName(user.getCompanyName());
		newUser.setCompanyAddress(user.getCompanyAddress());
		newUser.setCompanyContactNumber(user.getCompanyContactNumber());
		newUser.setCompanyWebsite(user.getCompanyWebsite());
		newUser.setRoleId(user.getRoleId());
		return userRepository.save(newUser);
	}
	
	public Optional<User> getUserById(Long id) {
		Optional<User> user = userRepository.findById(id);
		if (user != null) {
			user.get().setPassword(null);
			return user;
		} else {
			throw new UsernameNotFoundException("User not found with id: " + id);
		}
		
	}
	
	public User updateUser(UserDetailsUpdateRequestBody details) {
		User existingUser = userRepository.findById(details.getUserId())
	                .orElseThrow(() -> new IllegalArgumentException("user not found with id: " + details.getUserId()));
		existingUser.setCompanyName(details.getCompanyName());
		existingUser.setCompanyAddress(details.getCompanyAddress());
		existingUser.setCompanyContactNumber(details.getCompanyContactNumber());
		existingUser.setCompanyWebsite(details.getCompanyWebsite());
		return userRepository.save(existingUser);
	}
	
}
