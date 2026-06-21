package com.fidelity.mts.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fidelity.mts.dto.TransferRequest;
import com.fidelity.mts.dto.TransferResponse;
import com.fidelity.mts.entity.Account;
import com.fidelity.mts.entity.TransactionLog;
import com.fidelity.mts.enums.AccountStatus;
import com.fidelity.mts.enums.TransactionStatus;
import com.fidelity.mts.exceptions.AccountNotActiveException;
import com.fidelity.mts.exceptions.AccountNotFoundException;
import com.fidelity.mts.exceptions.DuplicateTransferException;
import com.fidelity.mts.exceptions.InsufficientBalanceException;
import com.fidelity.mts.repo.AccountRepository;
import com.fidelity.mts.repo.TransactionLogRepository;


@Service
public class TransferServiceImpl implements TransferService {

	@Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Override
    @Transactional
    public TransferResponse transfer(TransferRequest transferRequest) { 
        try {
            validateTransfer(transferRequest);
            checkIdempotency(transferRequest.getIdempotencyKey());
            Account fromAccount = accountRepository.findById(transferRequest.getFromAccountid())
                    .orElseThrow(() -> new AccountNotFoundException(transferRequest.getFromAccountid()));
            Account toAccount = accountRepository.findById(transferRequest.getToAccountId())
                    .orElseThrow(() -> new AccountNotFoundException(transferRequest.getToAccountId()));
            executeTransfer(fromAccount, toAccount, transferRequest.getAmount()); 
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);
            // compute reward points: sender receives floor(amount/100) points for transfers >= 100 to another user
            Integer points = 0;
            if (transferRequest.getFromAccountid() != transferRequest.getToAccountId()) {
                if (transferRequest.getAmount() != null && transferRequest.getAmount().compareTo(new java.math.BigDecimal(100)) >= 0) {
                    points = transferRequest.getAmount().divideToIntegralValue(new java.math.BigDecimal(100)).intValue();
                }
            }
            TransactionLog transactionLog = logTransaction(
                    transferRequest.getFromAccountid(),
                    transferRequest.getToAccountId(),
                    transferRequest.getAmount(),
                    TransactionStatus.SUCCESS,
                    null,
                    transferRequest.getIdempotencyKey(),
                    transferRequest.getRemarks(),
                    points
            );  
            return buildSuccessResponse(transactionLog, transferRequest);
        } catch (Exception e) {  
            logTransaction(
                    transferRequest.getFromAccountid(),
                    transferRequest.getToAccountId(),
                    transferRequest.getAmount(),
                    TransactionStatus.FAILED,
                    e.getMessage(),
                    transferRequest.getIdempotencyKey(),
                    transferRequest.getRemarks(),
                    0
            );
               
            throw e;
        }
    }
  
    @Override
    public void validateTransfer(TransferRequest transferRequest) {
        
        if (transferRequest.getFromAccountid()==transferRequest.getToAccountId()) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }
        if (transferRequest.getAmount() == null || transferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }      
        Account fromAccount = accountRepository.findById(transferRequest.getFromAccountid())
                .orElseThrow(() -> new AccountNotFoundException(transferRequest.getFromAccountid()));
        Account toAccount = accountRepository.findById(transferRequest.getToAccountId())
                .orElseThrow(() -> new AccountNotFoundException(transferRequest.getToAccountId()));       
        if (!fromAccount.isActive()) {
            throw new AccountNotActiveException(transferRequest.getFromAccountid());
        }   
        if (!toAccount.isActive()) {
            throw new AccountNotActiveException(transferRequest.getToAccountId());
        }   
        if (fromAccount.getBalance().compareTo(transferRequest.getAmount()) < 0) {
            throw new InsufficientBalanceException(fromAccount.getBalance(), transferRequest.getAmount());
        }    
    }

    
    @Override
    @Transactional
    public void executeTransfer(Account fromAccount, Account toAccount, BigDecimal amount) {    
        fromAccount.debit(amount);
        toAccount.credit(amount);      
    }
 
    private void checkIdempotency(String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.trim().isEmpty()) {
            if (transactionLogRepository.existsByIdempotencyKey(idempotencyKey)) {
                
                throw new DuplicateTransferException(idempotencyKey);
            }
        }
    }   
    private TransactionLog logTransaction(Long fromAccountId, Long toAccountId, BigDecimal amount,
                                          TransactionStatus status, String failureReason, String idempotencyKey, String note, Integer rewardPoints) {
        TransactionLog log = new TransactionLog();
        log.setId(UUID.randomUUID());
        log.setFromAccountid(fromAccountId);
        log.setToAccountid(toAccountId);
        log.setAmount(amount);
        log.setStatus(status);
        log.setFailureReason(failureReason);
        log.setIdempotencyKey(idempotencyKey);
        log.setNote(note);
        log.setRewardPoints(rewardPoints);
        log.setCreatedOn(Timestamp.valueOf(LocalDateTime.now()));
        TransactionLog savedLog = transactionLogRepository.save(log);
        return savedLog;
    }
    private TransferResponse buildSuccessResponse(TransactionLog transactionLog, TransferRequest request) {
        TransferResponse response = new TransferResponse();
        response.setTransactionId(transactionLog.getId());
        response.setStatus(AccountStatus.ACTIVE);
        response.setMessage("Transfer completed");
        response.setDebitedFrom(request.getFromAccountid());
        response.setCreditedTo(request.getToAccountId());
        response.setAmount(request.getAmount());
        response.setRewardPoints(transactionLog.getRewardPoints());
        response.setNote(transactionLog.getNote());
        return response;
    }
}