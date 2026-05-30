package com.cviana.app.user;

import java.net.URI;

import org.springframework.expression.AccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cviana.app.shared.exception.errors.ServerErrorException;
import com.cviana.app.shared.exception.messages.SystemErrorMessages;
import com.cviana.app.user.dto.UserRequestDto;
import com.cviana.app.user.dto.UserResponseDto;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/users")
@Valid
public class UserController {
	
	private UserService service;
	
	public UserController(UserService service) {
		this.service = service;
	}
	
	@PostMapping("/signup")
	public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto payload) {
		User response = service.create(payload);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
		return ResponseEntity.created(uri).body(UserResponseDto.setResponse(response));
	}
	
	@PutMapping("{id}")
	public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto payload, @AuthenticationPrincipal User currentUser) throws EntityNotFoundException, AccessException {
		User response = service.update(id, payload, currentUser);
		return ResponseEntity.ok(UserResponseDto.setResponse(response));
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
		if( currentUser.getId() == id )
			service.delete(id);
		
		if( service.findById(id) != null ) throw new ServerErrorException(SystemErrorMessages.SERVER_ERROR_DEFAULT);
		return ResponseEntity.noContent().build();
	}
}
