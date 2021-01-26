package com.techelevator.controller.response;

public class ResponseError {
	private String error;
	
	public ResponseError(String error) {
		this.error = error;
	}
	
	public String getError() { return error; }
}