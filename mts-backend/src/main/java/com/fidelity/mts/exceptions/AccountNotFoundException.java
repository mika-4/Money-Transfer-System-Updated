package com.fidelity.mts.exceptions;
 
@SuppressWarnings("serial")
public class AccountNotFoundException extends RuntimeException{
	 
	public AccountNotFoundException(Long accountId) {
		super("Account not found with ID: "+accountId);
	}

public AccountNotFoundException() {
        super("Transactions not found");
    }

    

 
}