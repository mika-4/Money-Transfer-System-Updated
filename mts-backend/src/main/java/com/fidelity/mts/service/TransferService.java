package com.fidelity.mts.service;

import java.math.BigDecimal;

import com.fidelity.mts.dto.TransferRequest;
import com.fidelity.mts.dto.TransferResponse;
import com.fidelity.mts.entity.Account;

public interface TransferService {
	public TransferResponse transfer(TransferRequest transReq);	
	void executeTransfer(Account fromAccount, Account toAccount, BigDecimal amount);
	void validateTransfer(TransferRequest transferRequest);
}
