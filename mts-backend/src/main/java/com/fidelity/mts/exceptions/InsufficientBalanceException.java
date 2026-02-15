package com.fidelity.mts.exceptions;


import java.math.BigDecimal;
 
@SuppressWarnings("serial")
public class InsufficientBalanceException extends RuntimeException{
 
	public InsufficientBalanceException(BigDecimal balance, BigDecimal amount) {
		super("insufficient balance. Balance "+balance+ " Amount "+amount);
	}
 
}
