package com.cviana.app.user;

import org.springframework.expression.AccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cviana.app.shared.exception.errors.InvalidAccessException;
import com.cviana.app.user.dto.UserRequestDto;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService implements UserDetailsService {
	
	private UserRepository repository;
	private PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = repository.findUserByEmail(email).orElseThrow(
				() -> new UsernameNotFoundException("No user were found with e-mail "+email)
				);
		return user;
	}
	
	public User findByEmail(String email) {
		return repository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("No user found with the required e-mail"));
	}
	
	public User findById(Long id) {
		return repository.findById(id).orElse(null);
	}
	
	public User create(UserRequestDto request) {
		User user = new User();
		user.setName(request.name());
		user.setEmail(request.email());
		user.setPassword(passwordEncoder.encode(request.password()));
		return repository.save(user);
	}
	
	public User update(Long id, UserRequestDto request, User currentUser) throws EntityNotFoundException, AccessException {
		User user = repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("No user were found matching the requested data"));
		if( !user.getEmail().equals(currentUser.getEmail()) ) throw new InvalidAccessException("You are not authorized to access this resource");
		
		user.setName(request.name());
		user.setEmail(request.email());
		user.setPassword(passwordEncoder.encode(request.password()));
		return repository.save(user);
	}
	
	public void delete(Long id) {
		repository.deleteById(id);
	}
}
