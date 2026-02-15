package com.fidelity.mts.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.fidelity.mts.enums.AccountStatus;

public class TransferResponse {
	private UUID TransactionId;
	private AccountStatus status;
	private String message;
	private long debitedFrom;
	private long creditedTo;
	private BigDecimal amount;
	
	
	public UUID getTransactionId() {
		return TransactionId;
	}
	public void setTransactionId(UUID transactionId) {
		TransactionId = transactionId;
	}
	public AccountStatus getStatus() {
		return status;
	}
	public void setStatus(AccountStatus status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getDebitedFrom() {
		return debitedFrom;
	}
	public void setDebitedFrom(long debitedFrom) {
		this.debitedFrom = debitedFrom;
	}
	public long getCreditedTo() {
		return creditedTo;
	}
	public void setCreditedTo(long creditedTo) {
		this.creditedTo = creditedTo;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}
