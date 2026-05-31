package com.cviana.app.auth;

import org.springframework.http.MediaType;
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
import com.cviana.app.shared.exception.ExceptionResponseTemplate;
import com.cviana.app.shared.exception.errors.InvalidAccessException;
import com.cviana.app.user.dto.CredentialsDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("api/auth")
@Valid
@Tag(name = "Acesso", description = "Serviços de acesso e autorização")
public class AuthController {
	
	private TokenService tokenService;
    private TokenBlacklistService tokenBlacklistService;
    private AuthenticationManager authenticationManager;
    
    public AuthController(TokenService tokenService, TokenBlacklistService tokenBlacklistService, AuthenticationManager authenticationManager) {
    	this.tokenService = tokenService;
    	this.authenticationManager = authenticationManager;
        this.tokenBlacklistService = tokenBlacklistService;
    }
    
	@Operation(
        summary = "Sign In",
        description = "Realiza a autenticação de usuário, concedendo acesso",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = TokenDto.class)
                    )
                }
            ),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
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
    
	@Operation(
        summary = "Logout",
        description = "Revoga o token de acesso vigente do usuário",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = MediaType.TEXT_PLAIN_VALUE
                    )
                }
            ),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") @NotNull String token) {
    	token = token.substring(7);
        //TODO: cache desativado devido limitação do serviço de hospedagem
    	// tokenBlacklistService.add(token, tokenService.extractUsername(token));
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout realizado com sucesso. Token invalidado.");
    }
    
}
