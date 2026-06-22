package com.fidelity.mts.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.fidelity.mts.dto.AddUserRequest;
import com.fidelity.mts.dto.LoginRequest;
import com.fidelity.mts.dto.LoginResponse;
import com.fidelity.mts.entity.UserCredential;
import com.fidelity.mts.repo.UserCredentialRepository;
import com.fidelity.mts.service.AuthService;
import com.fidelity.mts.service.AuthServiceImpl.InvalidCredentialsException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("http://localhost:4201")
@Validated
public class AuthController {

    @Autowired private AuthService               authService;
    @Autowired private PasswordEncoder           passwordEncoder;
    @Autowired private UserCredentialRepository  credRepo;

    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // GET /api/v1/auth/generate-hash/{password}
    // Open in browser: http://localhost:8080/api/v1/auth/generate-hash/password123
    // Copy the "hash" and run: UPDATE user_credential SET password='<hash>' WHERE username='Anirudh';
    @GetMapping("/generate-hash/{password}")
    public ResponseEntity<Map<String, String>> generateHash(@PathVariable String password) {
        String hash = passwordEncoder.encode(password);
        Map<String, String> result = new HashMap<>();
        result.put("password",   password);
        result.put("hash",       hash);
        result.put("sql_update", "UPDATE user_credential SET password = '" + hash
                                 + "' WHERE username = 'YOUR_USERNAME';");
        return ResponseEntity.ok(result);
    }

    // POST /api/v1/auth/fix-hash?username=Anirudh&newPassword=password123
    // Instantly re-hashes an existing user's password
    @PostMapping("/fix-hash")
    public ResponseEntity<Map<String, String>> fixHash(
            @RequestParam String username,
            @RequestParam String newPassword) {
        return credRepo.findByUsername(username).map(cred -> {
            cred.setPassword(passwordEncoder.encode(newPassword));
            credRepo.save(cred);
            Map<String, String> r = new HashMap<>();
            r.put("message",  "Password hash updated for: " + username);
            r.put("username", username);
            return ResponseEntity.ok(r);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "User not found: " + username)));
    }

    // POST /api/v1/auth/add-user
    // Body: { "username": "newuser", "password": "mypass", "mpin": "123456", "accountId": 5 }
    @PostMapping("/add-user")
    public ResponseEntity<Map<String, String>> addUser(@Valid @RequestBody AddUserRequest req) {
        if (credRepo.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Username already exists: " + req.getUsername()));
        }
        UserCredential cred = new UserCredential();
        cred.setUsername(req.getUsername());
        cred.setPassword(passwordEncoder.encode(req.getPassword()));
        cred.setMpinHash(passwordEncoder.encode(req.getMpin()));
        cred.setAccountId(req.getAccountId());
        credRepo.save(cred);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message",   "User created: " + req.getUsername(),
                             "accountId", req.getAccountId().toString()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorBody> handleBadCreds(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorBody(ex.getMessage()));
    }

    public static class ErrorBody {
        private final String message;
        public ErrorBody(String msg) { this.message = msg; }
        public String getMessage()   { return message; }
    }
}
