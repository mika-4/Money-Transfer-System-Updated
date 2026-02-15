

## 🔐 How Login Works

This app uses **basic credential-based auth** 

### How it works:
1. You enter username + password on the Login screen
2. Frontend checks credentials against a list of  accounts
3. If credentials match, it makes a GET request to verify the account exists in the backend (`GET /api/v1/accounts/{id}`)
4. A simple token is generated (`base64(accountId:username:timestamp)`) and stored in `localStorage`
5. All subsequent API calls include this token in the `Authorization: Bearer <token>` header
6. On logout, the token is cleared from localStorage and you're redirected to login






## Tech Stack

- **Angular 17** (Standalone Components)
- **Reactive Forms** for validation
- **Angular Router** with lazy loading
- **HTTP Client** with interceptors
- **Pure CSS** (no Material/Bootstrap dependency)
- **DM Sans + DM Mono** fonts
