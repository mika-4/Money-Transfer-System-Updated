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
  showBalance = false;
  showDetails = false;
  isDark = false;
  totalRewardPoints: number | null = null;

  constructor(
    private authService: AuthService,
    private accountService: AccountService,
    private router: Router
  ) {}

  toggleDetails(): void {
    this.showDetails = !this.showDetails;
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    const accountId = this.authService.getAccountId();

    if (!accountId) {
      this.router.navigate(['/login']);
      return;
    }
    // Load account details without asking for MPIN on dashboard
    this.loadAccount(accountId);

    // Update time every minute
    setInterval(() => { this.currentTime = new Date(); }, 60000);
    // apply theme from localStorage
    const t = localStorage.getItem('theme');
    this.isDark = t === 'dark';
    if (this.isDark) document.body.classList.add('dark-theme');
  }

  loadAccount(accountId: number): void {
    this.isLoading = true;
    this.accountService.getAccount(accountId).subscribe({
      next: (account) => {
        this.account = account;
        this.isLoading = false;
        this.loadRewardPoints(accountId);
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

  private loadRewardPoints(accountId: number): void {
    this.totalRewardPoints = null;
    this.accountService.getTransactions(accountId).subscribe({
      next: (txs: any[]) => {
        const sum = txs.reduce((acc: number, t: any) => acc + (t.rewardPoints || 0), 0);
        this.totalRewardPoints = sum;
      },
      error: () => {
        this.totalRewardPoints = 0;
      }
    });
  }

  get greeting(): string {
    const hour = this.currentTime.getHours();
    if (hour < 12) return 'Good Morning';
    if (hour < 17) return 'Good Afternoon';
    return 'Good Evening';
  }

  get greetingName(): string {
    return this.currentUser?.holderName || 'User';
  }

  get formattedAccountId(): string {
    if (!this.account) return '';
    const id = this.account.id.toString();
    return `XXXX-XXXX-${id.slice(-4)}`;
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  toggleBalance(): void {
    this.showBalance = !this.showBalance;
  }

  toggleTheme(): void {
    this.isDark = !this.isDark;
    if (this.isDark) {
      document.body.classList.add('dark-theme');
      localStorage.setItem('theme', 'dark');
    } else {
      document.body.classList.remove('dark-theme');
      localStorage.setItem('theme', 'light');
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
