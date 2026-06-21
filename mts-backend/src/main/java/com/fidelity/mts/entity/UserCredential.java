package com.fidelity.mts.entity;

import jakarta.persistence.*;

/**
 * Stores login credentials linked to an Account.
 * Table: user_credential in mts database.
 */
@Entity
@Table(name = "user_credential")
public class UserCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;   // stored as BCrypt hash
    
    @Column(name = "mpin_hash", length = 255)
    private String mpinHash; // BCrypt hash of 6-digit MPIN

    @Column(name = "account_id", nullable = false, unique = true)
    private Long accountId;    // FK → account.id (logical, no hard FK to keep it simple)

    public UserCredential() {}

    public UserCredential(String username, String password, Long accountId) {
        this.username  = username;
        this.password  = password;
        this.accountId = accountId;
    }

    /* ── getters / setters ─────────────────────────────────────── */

    public Long getId()                     { return id; }
    public void setId(Long id)              { this.id = id; }

    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }

    public String getPassword()             { return password; }
    public void setPassword(String p)       { this.password = p; }

    public String getMpinHash()              { return mpinHash; }
    public void setMpinHash(String mpinHash) { this.mpinHash = mpinHash; }

    public Long getAccountId()              { return accountId; }
    public void setAccountId(Long aid)      { this.accountId = aid; }
}
