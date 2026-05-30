package com.cviana.app.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cviana.app.auth.dto.TokenDto;
import com.cviana.app.shared.exception.errors.InvalidAccessException;
import com.cviana.app.user.dto.CredentialsDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/auth")
@Valid
public class AuthController {
	
	private TokenService tokenService;
    private TokenBlacklistService tokenBlacklistService;
    private AuthenticationManager authenticationManager;
    
    public AuthController(TokenService tokenService, TokenBlacklistService tokenBlacklistService, AuthenticationManager authenticationManager) {
    	this.tokenService = tokenService;
    	this.authenticationManager = authenticationManager;
        this.tokenBlacklistService = tokenBlacklistService;
    }
    
    @PostMapping("/signin")
    public TokenDto getToken(@RequestBody CredentialsDto credentials) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password())
        );
        
        if (authentication.isAuthenticated()) {
            return tokenService.generateToken(credentials.email());
        }
        else {
            throw new InvalidAccessException("Invalid user request");
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") @NotNull String token) {
    	token = token.substring(7);
    	tokenBlacklistService.add(token, tokenService.extractUsername(token));
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout realizado com sucesso. Token invalidado.");
    }
    
}
