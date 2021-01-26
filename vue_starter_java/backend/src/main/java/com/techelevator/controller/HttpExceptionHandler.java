package com.techelevator.controller;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.techelevator.controller.response.Response;
import com.techelevator.controller.response.ResponseError;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class HttpExceptionHandler extends ResponseEntityExceptionHandler {
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return new ResponseEntity<>(new Response<>(new ResponseError("Method Not Supported")), status);
	}
	
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(
			NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return new ResponseEntity<>(new Response<>(new ResponseError("Endpoint Not Found")), status);
	}
	@Override
	protected ResponseEntity<Object> handleNoSuchRequestHandlingMethod(org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return new ResponseEntity<>(new Response<>(new ResponseError("Endpoint Not Found")), status);
	}
}
