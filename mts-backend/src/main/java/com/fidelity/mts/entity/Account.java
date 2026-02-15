package com.fidelity.mts.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fidelity.mts.enums.AccountStatus;
import com.fidelity.mts.exceptions.InsufficientBalanceException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity

public class Account {
	@Id
    private Long id;
	@Column
	private String holderName;
	@Column
	private BigDecimal balance;
	@Column
	private Integer version;
	@Column
	private LocalDate lastUpdated;
	@Column
	private AccountStatus status;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getHolderName() {
		return holderName;
	}
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public LocalDate getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(LocalDate lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public AccountStatus getStatus() {
		return status;
	}
	public void setStatus(AccountStatus status) {
		this.status = status;
	}
	
	public void debit(BigDecimal amount) {
		if(balance.compareTo(amount)<0) {
			throw new InsufficientBalanceException(balance,amount);
		}
		balance=balance.subtract(amount);
		lastUpdated=LocalDate.now();

	}
	public void credit(BigDecimal amount) {
		balance=balance.add(amount);
		lastUpdated=LocalDate.now();

	}
	public boolean isActive() {
		// TODO Auto-generated method stub
		return status==AccountStatus.ACTIVE;
	}
}
