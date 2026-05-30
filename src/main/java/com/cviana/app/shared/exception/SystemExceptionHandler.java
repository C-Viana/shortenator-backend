package com.cviana.app.shared.exception;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cviana.app.shared.exception.errors.BadRequestException;
import com.cviana.app.shared.exception.errors.InvalidAccessException;
import com.cviana.app.shared.exception.errors.NotFoundException;
import com.cviana.app.shared.exception.errors.ServerErrorException;
import com.cviana.app.shared.exception.errors.UrlExpiredException;

@RestControllerAdvice
public class SystemExceptionHandler {
	
	@ExceptionHandler(ServerErrorException.class)
	public ResponseEntity<ExceptionResponseTemplate> serverExceptionHandler(ServerErrorException exception) {
		ExceptionResponseTemplate response = new ExceptionResponseTemplate(
				exception.getMessage(), 
				HttpStatus.INTERNAL_SERVER_ERROR.value(), 
				LocalDateTime.now(ZoneId.systemDefault()));
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ExceptionResponseTemplate> badRequestExceptionHandler(BadRequestException exception) {
		ExceptionResponseTemplate response = new ExceptionResponseTemplate(
				exception.getMessage(), 
				HttpStatus.BAD_REQUEST.value(), 
				LocalDateTime.now(ZoneId.systemDefault()));
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ExceptionResponseTemplate> notFoundExceptionHandler(NotFoundException exception) {
		ExceptionResponseTemplate response = new ExceptionResponseTemplate(
				exception.getMessage(), 
				HttpStatus.NOT_FOUND.value(), 
				LocalDateTime.now(ZoneId.systemDefault()));
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InvalidAccessException.class)
	public ResponseEntity<ExceptionResponseTemplate> invalidAccessExceptionHandler(InvalidAccessException exception) {
		ExceptionResponseTemplate response = new ExceptionResponseTemplate(
				exception.getMessage(), 
				HttpStatus.UNAUTHORIZED.value(), 
				LocalDateTime.now(ZoneId.systemDefault()));
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(UrlExpiredException.class)
	public ResponseEntity<ExceptionResponseTemplate> urlExpiredExceptionHandler(UrlExpiredException exception) {
		ExceptionResponseTemplate response = new ExceptionResponseTemplate(
				exception.getMessage(), 
				HttpStatus.EXPECTATION_FAILED.value(), 
				LocalDateTime.now(ZoneId.systemDefault()));
		return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ExceptionResponseTemplate> usernameNotFoundExceptionHandler(UsernameNotFoundException exception) {
		ExceptionResponseTemplate response = new ExceptionResponseTemplate(
				exception.getMessage(), 
				HttpStatus.BAD_REQUEST.value(), 
				LocalDateTime.now(ZoneId.systemDefault()));
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	
}
