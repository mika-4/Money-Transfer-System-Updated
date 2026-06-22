package com.fidelity.mts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddUserRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String mpin;

    @NotNull
    private Long accountId;

    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }

    public String getPassword()             { return password; }
    public void setPassword(String p)       { this.password = p; }

    public String getMpin()                 { return mpin; }
    public void setMpin(String m)           { this.mpin = m; }

    public Long getAccountId()              { return accountId; }
    public void setAccountId(Long id)       { this.accountId = id; }
}
