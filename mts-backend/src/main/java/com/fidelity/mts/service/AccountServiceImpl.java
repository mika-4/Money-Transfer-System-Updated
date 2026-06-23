package com.fidelity.mts.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fidelity.mts.entity.Account;
import com.fidelity.mts.entity.TransactionLog;
import com.fidelity.mts.exceptions.AccountNotFoundException;
import com.fidelity.mts.repo.AccountRepository;
import com.fidelity.mts.repo.TransactionLogRepository;

@Service
public class AccountServiceImpl implements AccountService{
	
	@Autowired
	private AccountRepository accountrepo;
	@Autowired
	private TransactionLogRepository transactionLogRepository;
    @Autowired
    private com.fidelity.mts.repo.UserCredentialRepository userCredentialRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

	@Override
	public String addAccount(Account account) {
		accountrepo.save(account);
		return "Account added";
	}

	@Override
	public Account getAccount(long id) {
		Optional<Account> optional=accountrepo.findById(id);
		if(!optional.isPresent()) 
			throw new AccountNotFoundException(id);
		return optional.get();
	}

	@Override
	public BigDecimal getBalance(long id) {
		Optional<Account> optional=accountrepo.findById(id);
		if(!optional.isPresent()) 
			throw new AccountNotFoundException(id);
		return optional.get().getBalance();
	}

	
	@Override
	public List<TransactionLog> getTransactions(Long accountId) {
		// ensure the account exists first
		accountrepo.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));

		Optional<List<TransactionLog>> debitTransactions = transactionLogRepository.findByFromAccountid(accountId);
		Optional<List<TransactionLog>> creditTransactions = transactionLogRepository.findByToAccountid(accountId);
		List<TransactionLog> transactions = new ArrayList<>();
		// debit (sender) transactions keep their rewardPoints
		debitTransactions.ifPresent(transactions::addAll);
		// credit (recipient) transactions should not carry reward points — zero them for the returned list
		creditTransactions.ifPresent(list -> {
			list.forEach(t -> t.setRewardPoints(0));
			transactions.addAll(list);
		});
		// return empty list if there are no transactions for the account
		return transactions;
	}

	@Override
	public boolean verifyMpin(Long accountId, String mpin) {
	return userCredentialRepository.findByAccountId(accountId)
		.map(uc -> passwordEncoder.matches(mpin, uc.getMpinHash()))
		.orElse(false);
    }



	
}
