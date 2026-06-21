package com.fidelity.mts.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
public class TransferRequest {
	@NotNull
	private long fromAccountid;
	@NotNull
	private long toAccountId;
	@NotNull
	@DecimalMin(value="0.01",inclusive=true)
	private BigDecimal amount;
	@NotNull
	private String idempotencyKey;

	private String remarks;
	
	public void setFromAccountid(long fromAccountid) {
		this.fromAccountid = fromAccountid;
	}
	public void setToAccountId(long toAccountId) {
		this.toAccountId = toAccountId;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}
	public long getFromAccountid() {
		return fromAccountid;
	}
	public long getToAccountId() {
		return toAccountId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public String getRemarks() { return remarks; }
	public void setRemarks(String remarks) { this.remarks = remarks; }

 
}
