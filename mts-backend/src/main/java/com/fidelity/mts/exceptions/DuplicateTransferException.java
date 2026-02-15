package com.fidelity.mts.exceptions;
 
@SuppressWarnings("serial")
public class DuplicateTransferException extends RuntimeException{
 
	public DuplicateTransferException(String key) {
		super("Duplicate transfer with idempotency key "+key);
	}
 
}