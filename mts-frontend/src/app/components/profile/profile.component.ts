import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AccountService } from '../../services/account.service';
import { AuthService } from '../../services/auth.service';
import { Account } from '../../models';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  account: Account | null = null;
  isLoading = true;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private accountService: AccountService,
    public router: Router
  ) {}

  ngOnInit(): void {
    const accountId = this.authService.getAccountId();
    if (!accountId) { this.router.navigate(['/login']); return; }
    this.loadAccount(accountId);
  }

  private loadAccount(accountId: number): void {
    this.isLoading = true;
    this.accountService.getAccount(accountId).subscribe({
      next: (a) => { this.account = a; this.isLoading = false; },
      error: () => { this.errorMessage = 'Could not load account details.'; this.isLoading = false; }
    });
  }
}
