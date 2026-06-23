import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account, TransactionLog } from '../models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private baseUrl = `${environment.apiUrl}/accounts`;

  constructor(private http: HttpClient) {}

  getAccount(id: number): Observable<Account> {
    return this.http.get<Account>(`${this.baseUrl}/${id}`);
  }

  getBalance(id: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/${id}/balance`);
  }

  getTransactions(id: number): Observable<TransactionLog[]> {
    return this.http.get<TransactionLog[]>(`${this.baseUrl}/${id}/transactions`);
  }

  addAccount(account: Partial<Account>): Observable<string> {
    // The backend returns a plain text message for this endpoint in some setups.
    // Request the response as text to avoid Angular trying to parse non-JSON.
    return this.http.post<string>(`${this.baseUrl}/addAccount`, account, { responseType: 'text' as 'json' });
  }

  verifyMpin(id: number, mpin: string): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}/${id}/verify-mpin`, { mpin });
  }
}
