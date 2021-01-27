package com.springdatajpa.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springdatajpa.entity.User;
import com.springdatajpa.exception.ResourceNotFoundException;
import com.springdatajpa.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@GetMapping
	public List<User> showUsers() {
		return userRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public Optional<User> showUser(@PathVariable("id") long id) {
		Optional<User> user = userRepository.findById(id);
		if(!user.isPresent()) {
			throw new IllegalStateException("Id not found");
		}
		return user;
	}
	
	@PostMapping
	public User createUser(@RequestBody User user) {
		Optional<User> findUserByEmail = userRepository.findUserByEmail(user.getEmail());
		if(findUserByEmail.isPresent()) {
			throw new IllegalStateException("Email Taken");
		}
		
		return userRepository.save(user);
	}
	
	@PutMapping("/{id}")
	public User updateUser(@RequestBody User updateData, @PathVariable("id") long id) {
		User user = userRepository.findById(id)
								  .orElseThrow(() -> new ResourceNotFoundException("User not Found"));
		
		Optional<User> findUserByEmail = userRepository.findUserByEmail(updateData.getEmail());
		if(findUserByEmail.isPresent()) {
			throw new IllegalStateException("Email Taken");
		} else {
			user.setFirstName(updateData.getFirstName());
			user.setLastName(updateData.getLastName());
			user.setEmail(updateData.getEmail());
			return userRepository.save(user);
		}	
	}
	
	@DeleteMapping("/{id}") 
	public ResponseEntity<User> deleteUser(@PathVariable("id") long id) {
		User existingUser = userRepository.findById(id)
										  .orElseThrow(() -> new ResourceNotFoundException("User not found"));
		userRepository.delete(existingUser);
		return ResponseEntity.ok().build();
	}
}
