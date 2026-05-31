package com.cviana.app.user;

import java.net.URI;

import org.springframework.expression.AccessException;
import org.springframework.http.MediaType;
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

import com.cviana.app.shared.exception.ExceptionResponseTemplate;
import com.cviana.app.shared.exception.errors.ServerErrorException;
import com.cviana.app.shared.exception.messages.SystemErrorMessages;
import com.cviana.app.user.dto.UserRequestDto;
import com.cviana.app.user.dto.UserResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/users")
@Valid
@Tag(name = "User", description = "CRUD para gerenciamento de usuários")
public class UserController {
	
	private UserService service;
	
	public UserController(UserService service) {
		this.service = service;
	}
	
	@Operation(
        summary = "Criar usuário",
        description = "Gera um novo usuário para acesso e operação do sistema",
        responses = {
            @ApiResponse(
                description = "Created",
                responseCode = "201",
                content = {
                    @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UserResponseDto.class)
                    )
                }
            ),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class))),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExceptionResponseTemplate.class)))
        }
    )
	@PostMapping("/signup")
	public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto payload) {
		User response = service.create(payload);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
		return ResponseEntity.created(uri).body(UserResponseDto.setResponse(response));
	}
	
	@Operation(
        summary = "Atualizar usuário",
        description = "Altera os dados de um usuário existente",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = {
                    @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UserResponseDto.class)
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
	@PutMapping("{id}")
	public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto payload, @AuthenticationPrincipal User currentUser) throws EntityNotFoundException, AccessException {
		User response = service.update(id, payload, currentUser);
		return ResponseEntity.ok(UserResponseDto.setResponse(response));
	}
	
	@Operation(
        summary = "Apagar usuário",
        description = "Remove o registro de um usuário do sistema. Essa ação não pode ser desfeita",
        responses = {
            @ApiResponse(
                description = "No Content",
                responseCode = "204"
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
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
		if( currentUser.getId() == id )
			service.delete(id);
		
		if( service.findById(id) != null ) throw new ServerErrorException(SystemErrorMessages.SERVER_ERROR_DEFAULT);
		return ResponseEntity.noContent().build();
	}
}
