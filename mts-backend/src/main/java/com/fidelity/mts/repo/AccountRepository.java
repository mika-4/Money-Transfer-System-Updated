package com.fidelity.mts.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fidelity.mts.entity.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{

}