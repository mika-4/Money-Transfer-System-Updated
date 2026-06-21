package com.fidelity.mts.service;

import java.math.BigDecimal;
import java.util.List;


import com.fidelity.mts.entity.Account;
import com.fidelity.mts.entity.TransactionLog;

public interface AccountService {
	String addAccount(Account account);
	Account getAccount(long id);
	BigDecimal getBalance(long id);
	List<TransactionLog> getTransactions(Long accountId);
	boolean verifyMpin(Long accountId, String mpin);
}
