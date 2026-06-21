package com.fidelity.mts.entity;

import java.util.UUID;

import com.fidelity.mts.enums.TransactionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
public class TransactionLog {
	@Id
	private UUID id;
	@Column
	private Long fromAccountid;
	@Column
	private Long toAccountid;
	@Column
	private BigDecimal Amount;
	@Column
	private TransactionStatus status;
	@Column
	private String failureReason;
	@Column
	private String idempotencyKey;
	@Column
	private String note;
	@Column
	private Integer rewardPoints;
	@Column
	private Timestamp createdOn;
	public UUID getId() {
		return id;
	}
	public void setId(UUID string) {
		this.id = string;
	}
	public Long getFromAccountid() {
		return fromAccountid;
	}
	public void setFromAccountid(Long fromAccountid) {
		this.fromAccountid = fromAccountid;
	}
	public Long getToAccountid() {
		return toAccountid;
	}
	public void setToAccountid(Long toAccountid) {
		this.toAccountid = toAccountid;
	}
	public BigDecimal getAmount() {
		return Amount;
	}
	public void setAmount(BigDecimal amount) {
		Amount = amount;
	}
	public TransactionStatus getStatus() {
		return status;
	}
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	public String getFailureReason() {
		return failureReason;
	}
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	public String getIdempotencyKey() {
		return idempotencyKey;
	}
	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}
	public String getNote() { return note; }
	public void setNote(String note) { this.note = note; }

	public Integer getRewardPoints() { return rewardPoints; }
	public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }
	public Timestamp getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

    
}
