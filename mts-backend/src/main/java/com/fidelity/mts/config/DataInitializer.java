package com.fidelity.mts.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fidelity.mts.entity.Account;
import com.fidelity.mts.entity.UserCredential;
import com.fidelity.mts.enums.AccountStatus;
import com.fidelity.mts.repo.AccountRepository;
import com.fidelity.mts.repo.UserCredentialRepository;

import java.math.BigDecimal;
import java.time.LocalDate;


@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private AccountRepository         accountRepo;
    @Autowired private UserCredentialRepository  credRepo;
    @Autowired private PasswordEncoder           passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("=== DataInitializer: checking seed data ===");

      
//
//        // ── 2. Seed demo credentials if missing ─────────────────────────────
//        seedCredential("anirudh",  "password1", 1L);
//        seedCredential("anamika", "password12", 2L);
//        seedCredential("ananyaa",  "password123", 3L);
//        seedCredential("akshaya",    "password12", 4L);

     
   
    }

    /** Insert account only if it doesn't already exist */
    private void seedAccount(Long id, String holderName, BigDecimal balance) {
        if (!accountRepo.existsById(id)) {
            Account acc = new Account();
            acc.setId(id);
            acc.setHolderName(holderName);
            acc.setBalance(balance);
            acc.setVersion(1);
            acc.setLastUpdated(LocalDate.now());
            acc.setStatus(AccountStatus.ACTIVE);
            accountRepo.save(acc);
            log.info("  Seeded account: {} ({})", holderName, id);
        }
    }

    /** Insert credential only if username doesn't already exist */
    private void seedCredential(String username, String rawPassword, Long accountId) {
        if (credRepo.findByUsername(username).isEmpty()) {
            UserCredential cred = new UserCredential();
            cred.setUsername(username);
            cred.setPassword(passwordEncoder.encode(rawPassword));   // ← LIVE encoder
            cred.setAccountId(accountId);
            credRepo.save(cred);
            log.info("  Seeded credential: {} → accountId={}", username, accountId);
        }
    }

    /**
     * Force-update the password hash for an EXISTING user.
     * Call this for "Anirudh" and "Anamika" whose hashes were wrong.
     * Safe to call repeatedly — it always re-hashes to ensure correctness.
     */
    private void fixPassword(String username, String rawPassword) {
        credRepo.findByUsername(username).ifPresent(cred -> {
            // Check if the current hash is wrong
            boolean currentlyWorks = passwordEncoder.matches(rawPassword, cred.getPassword());
            if (!currentlyWorks) {
                cred.setPassword(passwordEncoder.encode(rawPassword));
                credRepo.save(cred);
                log.info("  Fixed password hash for existing user: '{}'", username);
            } else {
                log.info("  Password hash OK for: '{}'", username);
            }
        });
    }
}
