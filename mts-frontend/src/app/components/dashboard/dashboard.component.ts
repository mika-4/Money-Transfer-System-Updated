import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AccountService } from '../../services/account.service';
import { Account, AuthUser } from '../../models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  account: Account | null = null;
  currentUser: AuthUser | null = null;
  isLoading = true;
  errorMessage = '';
  currentTime = new Date();

  constructor(
    private authService: AuthService,
    private accountService: AccountService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    const accountId = this.authService.getAccountId();

    if (!accountId) {
      this.router.navigate(['/login']);
      return;
    }

    // Ask for 6-digit MPIN before loading account details
    const rawMpin = window.prompt('Enter your 6-digit MPIN to view account details');
    if (!rawMpin) {
      this.router.navigate(['/login']);
      return;
    }
    this.accountService.verifyMpin(accountId, rawMpin).subscribe({
      next: () => this.loadAccount(accountId),
      error: () => {
        this.errorMessage = 'Invalid MPIN. Access denied.';
        this.isLoading = false;
      }
    });

    // Update time every minute
    setInterval(() => { this.currentTime = new Date(); }, 60000);
  }

  loadAccount(accountId: number): void {
    this.isLoading = true;
    this.accountService.getAccount(accountId).subscribe({
      next: (account) => {
        this.account = account;
        this.isLoading = false;
      },
      error: (err) => {
        // Fallback: show user info from session if backend unavailable
        this.isLoading = false;
        this.errorMessage = 'Could not load account data. Please check backend connection.';
        if (this.currentUser) {
          this.account = {
            id: this.currentUser.accountId,
            holderName: this.currentUser.holderName,
            balance: 0,
            version: 1,
            lastUpdated: new Date().toISOString(),
            status: 'ACTIVE' as any
          };
        }
      }
    });
  }

  get greeting(): string {
    const hour = this.currentTime.getHours();
    if (hour < 12) return 'Good Morning';
    if (hour < 17) return 'Good Afternoon';
    return 'Good Evening';
  }

  get greetingEmoji(): string {
    const hour = this.currentTime.getHours();
    if (hour < 12) return '☀️';
    if (hour < 17) return '👋';
    return '🌙';
  }

  get formattedAccountId(): string {
    if (!this.account) return '';
    const id = this.account.id.toString();
    return `XXXX-XXXX-${id.slice(-4)}`;
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  logout(): void {
    this.authService.logout();
  }
}
