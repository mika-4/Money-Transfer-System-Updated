package com.fidelity.mts.service;

import java.util.Base64;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fidelity.mts.dto.LoginRequest;
import com.fidelity.mts.dto.LoginResponse;
import com.fidelity.mts.entity.Account;
import com.fidelity.mts.entity.UserCredential;
import com.fidelity.mts.exceptions.AccountNotFoundException;
import com.fidelity.mts.repo.AccountRepository;
import com.fidelity.mts.repo.UserCredentialRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserCredentialRepository credRepo;

    @Autowired
    private AccountRepository accountRepo;

    /**
     * Injected from PasswordConfig — Spring-managed BCryptPasswordEncoder.
     * DO NOT use "new BCryptPasswordEncoder()" — must be the Spring bean
     * so it is consistent across the entire application context.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for username: '{}'", request.getUsername());

        // 1. Find the credential row
        Optional<UserCredential> credOpt = credRepo.findByUsername(request.getUsername());
        if (credOpt.isEmpty()) {
            log.warn("Username '{}' not found in user_credential table", request.getUsername());
            throw new InvalidCredentialsException();
        }
        UserCredential cred = credOpt.get();
        log.info("Found credential for '{}', accountId={}", cred.getUsername(), cred.getAccountId());

        // 2. BCrypt match — passwordEncoder.matches(rawPassword, storedHash)
        boolean matches = passwordEncoder.matches(request.getPassword(), cred.getPassword());
        log.info("Password match result for '{}': {}", request.getUsername(), matches);

        if (!matches) {
            log.warn("Password mismatch for username '{}'. " +
                     "Re-run data.sql with hashes from GET /api/v1/auth/generate-hash/{password}",
                     request.getUsername());
            throw new InvalidCredentialsException();
        }

        // 3. Load the account
        Account account = accountRepo.findById(cred.getAccountId())
                .orElseThrow(() -> {
                    log.error("accountId={} in user_credential but missing from account table",
                              cred.getAccountId());
                    return new AccountNotFoundException(cred.getAccountId());
                });

        log.info("Login OK for '{}' (accountId={}, holderName='{}')",
                 request.getUsername(), account.getId(), account.getHolderName());

        // 4. Opaque token: base64( accountId:username:timestamp )
        String raw   = account.getId() + ":" + cred.getUsername() + ":" + System.currentTimeMillis();
        String token = Base64.getEncoder().encodeToString(raw.getBytes());

        return new LoginResponse(token, account.getId(), account.getHolderName(), "Login successful");
    }

    /* ── inner exception ── */
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() { super("Invalid username or password"); }
    }
}
