package com.fidelity.mts.service;

import com.fidelity.mts.dto.LoginRequest;
import com.fidelity.mts.dto.LoginResponse;

public interface AuthService {

    /** Validate credentials and return a session token + account info. */
    LoginResponse login(LoginRequest request);
}
