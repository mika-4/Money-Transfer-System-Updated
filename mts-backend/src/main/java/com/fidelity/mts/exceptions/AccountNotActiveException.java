package com.fidelity.mts.exceptions;

@SuppressWarnings("serial")
public class AccountNotActiveException extends RuntimeException {
 
	    public AccountNotActiveException(long l) {
	        super("Account not active with ID: " + l);
	    }

		
}
