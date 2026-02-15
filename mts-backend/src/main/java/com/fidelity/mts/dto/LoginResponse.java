package com.fidelity.mts.dto;

public class LoginResponse {

    private String token;
    private long   accountId;
    private String holderName;
    private String message;

    public LoginResponse() {}

    public LoginResponse(String token, long accountId, String holderName, String message) {
        this.token      = token;
        this.accountId  = accountId;
        this.holderName = holderName;
        this.message    = message;
    }

    public String getToken()                { return token; }
    public void setToken(String token)      { this.token = token; }

    public long getAccountId()              { return accountId; }
    public void setAccountId(long id)       { this.accountId = id; }

    public String getHolderName()           { return holderName; }
    public void setHolderName(String h)     { this.holderName = h; }

    public String getMessage()              { return message; }
    public void setMessage(String m)        { this.message = m; }
}
