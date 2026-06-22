import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthUser, LoginCredentials } from '../models';
import { environment } from '../../environments/environment';

interface AddUserRequest {
  username: string;
  password: string;
  mpin: string;
  accountId: number;
}

/** Shape returned by POST /api/v1/auth/login */
interface BackendLoginResponse {
  token:      string;
  accountId:  number;
  holderName: string;
  message:    string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly TOKEN_KEY      = 'mts_token';
  private readonly USER_KEY       = 'mts_user';
  private readonly ACCOUNT_ID_KEY = 'mts_account_id';

  constructor(private http: HttpClient, private router: Router) {}

  /**
   * Calls POST /api/v1/auth/login with { username, password }.
   * On success the backend returns { token, accountId, holderName, message }.
   * We store those in localStorage and the rest of the app uses them.
   */
  login(credentials: LoginCredentials): Observable<BackendLoginResponse> {
    return this.http
      .post<BackendLoginResponse>(`${environment.apiUrl}/auth/login`, {
        username: credentials.username,
        password: credentials.password
      })
      .pipe(
        tap((res) => {
          const authUser: AuthUser = {
            accountId:  res.accountId,
            holderName: res.holderName,
            token:      res.token
          };
          this.storeSession(authUser);
        })
      );
  }

  private storeSession(user: AuthUser): void {
    localStorage.setItem(this.TOKEN_KEY,      user.token);
    localStorage.setItem(this.USER_KEY,       JSON.stringify(user));
    localStorage.setItem(this.ACCOUNT_ID_KEY, user.accountId.toString());
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.ACCOUNT_ID_KEY);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getCurrentUser(): AuthUser | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  getAccountId(): number | null {
    const id = localStorage.getItem(this.ACCOUNT_ID_KEY);
    return id ? parseInt(id, 10) : null;
  }

  addUser(req: AddUserRequest) {
    return this.http.post(`${environment.apiUrl}/auth/add-user`, req);
  }
}
