package com.fidelity.mts.dto;

import java.math.BigDecimal;
import com.fidelity.mts.enums.AccountStatus;
public class AccountResponse {
	private long accountId;
	private BigDecimal balance;
	private AccountStatus status;
	
	public AccountResponse(long AccountId,BigDecimal balance,AccountStatus status) {
		this.accountId=AccountId;
		this.balance=balance;
		this.status=status;
	}
	public long getAccountId() {
		return accountId;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public AccountStatus getStatus() {
		return status;
	}
 
}
