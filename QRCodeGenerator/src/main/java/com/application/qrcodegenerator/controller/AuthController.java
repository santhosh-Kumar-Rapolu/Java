package com.application.qrcodegenerator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.application.qrcodegenerator.model.JwtRequest;
import com.application.qrcodegenerator.model.JwtResponse;
import com.application.qrcodegenerator.model.ResponseBody;
import com.application.qrcodegenerator.model.User;
import com.application.qrcodegenerator.repository.UserRepository;
import com.application.qrcodegenerator.security.JwtHelper;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/auth")
public class AuthController {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationManager manager;

	@Autowired
	private JwtHelper helper;

	@Autowired
	private UserRepository userRepository;
	private Logger logger = LoggerFactory.getLogger(AuthController.class);

	@RequestMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
		this.doAuthenticate(request.getEmail(), request.getPassword());
		UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
		User user = userRepository.findByUsername(request.getEmail());
		String token = this.helper.generateToken(userDetails);
		JwtResponse response = JwtResponse.builder()
				.jwtToken(token)
				.username(userDetails.getUsername())
				.userId(user.getId())
				.roleId(user.getRoleId()).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	private void doAuthenticate(String email, String password) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
		try {
			manager.authenticate(authentication);
		} catch (BadCredentialsException e) {
			throw new BadCredentialsException(" Invalid Username or Password  !!");
		}
	}

	@ExceptionHandler(BadCredentialsException.class)
	public String exceptionHandler() {
		return "Credentials Invalid !!";
	}

	
	@PostMapping("/logout")
    public ResponseEntity<ResponseBody> logout(@RequestHeader("Authorization") String token) {
		ResponseBody responseBody= new ResponseBody();
		try {
			String jwtToken = token.substring(7); 
			helper.invalidateToken(jwtToken);
			responseBody.setMessage("Logout successful");
			responseBody.setError(null);
			return new ResponseEntity<>(responseBody, HttpStatus.OK);
		} catch (Exception e) {
			responseBody.setMessage("Logout unsuccessful");
			responseBody.setError(e.getMessage());
			return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	

}
