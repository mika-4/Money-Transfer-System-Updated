package com.fidelity.mts.controller;

import java.math.BigDecimal;
import java.util.List;

import com.fidelity.mts.entity.TransactionLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fidelity.mts.entity.Account;
import com.fidelity.mts.service.AccountService;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
	
	@Autowired
	private AccountService accountservice;
	
	@GetMapping("/{id}")
	public Account getAccount(@PathVariable long id) {
		return accountservice.getAccount(id);
	}
	
	@GetMapping("/{id}/balance")
	public BigDecimal getBalance(@PathVariable long id) {
		return accountservice.getBalance(id);
	}
	
	@PostMapping("/addAccount")
	public ResponseEntity<String> addAccount(@RequestBody Account account) {
		return ResponseEntity.status(HttpStatus.OK).body(accountservice.addAccount(account));
	}
	 @GetMapping("/{id}/transactions")
	    public ResponseEntity<List<TransactionLog>> getTransactions(@PathVariable long id) {
	        List<TransactionLog> transactions = accountservice.getTransactions(id);
	        return ResponseEntity.ok(transactions);
	    }
	
}
