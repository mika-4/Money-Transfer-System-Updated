package com.fidelity.mts.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidelity.mts.entity.TransactionLog;

@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, UUID>{

	boolean existsByIdempotencyKey(String idempotencyKey);
    Optional<List<TransactionLog>> findByFromAccountid(Long accountId); 
    Optional<List<TransactionLog>> findByToAccountid(Long accountId); 

}
