package com.fidelity.mts.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
 
@ControllerAdvice
public class MTSGlobalExceptionHandler{
	@ExceptionHandler
	public ResponseEntity<String> actNotActive(AccountNotActiveException e){
		return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler
	public ResponseEntity<String> actNotFound(AccountNotFoundException e){
		return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler
	public ResponseEntity<String> dupliTransfer(DuplicateTransferException e){
		return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler
	public ResponseEntity<String> insufBalance(InsufficientBalanceException e){
		return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
	}
}

