package com.zingplay.controllers;

import com.zingplay.payload.request.SignupRequest;
import com.zingplay.payload.response.MessageResponse;
import com.zingplay.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
    JwtUtils jwtUtils;

	//@PostMapping("/signin")
	//public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    //
	//	Authentication authentication = authenticationManager.authenticate(
	//			new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    //
	//	SecurityContextHolder.getContext().setAuthentication(authentication);
	//	String jwt = jwtUtils.generateJwtToken(authentication);
    //
	//	UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	//	List<String> roles = userDetails.getAuthorities().stream()
	//			.map(item -> item.getAuthority())
	//			.collect(Collectors.toList());
    //
	//	return ResponseEntity.ok(new JwtResponse(jwt,
	//											 userDetails.getId(),
	//											 userDetails.getUsername(),
	//											 userDetails.getEmail(),
	//											 roles));
	//}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
}
