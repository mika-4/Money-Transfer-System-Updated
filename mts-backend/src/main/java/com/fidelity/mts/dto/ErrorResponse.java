package com.fidelity.mts.dto;



public class ErrorResponse {
	private String ErrorCode;
	private String message;
	public ErrorResponse(String ErrorCode,String message) {
		this.ErrorCode=ErrorCode;
		this.message=message;
		
	}
	public String getErrorCode() {
		return ErrorCode;
	}
	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	} 
}
