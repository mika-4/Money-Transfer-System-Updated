import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AccountService } from '../../services/account.service';
import { TransactionLog, TransactionStatus } from '../../models';

type FilterType = 'ALL' | 'SENT' | 'RECEIVED';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {
  transactions: TransactionLog[] = [];
  filteredTransactions: TransactionLog[] = [];
  isLoading = true;
  errorMessage = '';
  activeFilter: FilterType = 'ALL';
  currentAccountId: number | null = null;

  filters: FilterType[] = ['ALL', 'SENT', 'RECEIVED'];

  constructor(
    private authService: AuthService,
    private accountService: AccountService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentAccountId = this.authService.getAccountId();
    if (!this.currentAccountId) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.isLoading = true;
    this.accountService.getTransactions(this.currentAccountId!).subscribe({
      next: (transactions) => {
        this.transactions = this.sortByDate(transactions);
        this.applyFilter(this.activeFilter);
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        if (err?.status === 404 || err?.error === 'No transactions found') {
          this.transactions = [];
          this.filteredTransactions = [];
        } else {
          this.errorMessage = 'Could not load transactions. Please check your connection.';
        }
      }
    });
  }

  private sortByDate(txns: TransactionLog[]): TransactionLog[] {
    return txns.sort((a, b) =>
      new Date(b.createdOn).getTime() - new Date(a.createdOn).getTime()
    );
  }

  applyFilter(filter: FilterType): void {
    this.activeFilter = filter;
    switch (filter) {
      case 'SENT':
        this.filteredTransactions = this.transactions.filter(
          t => t.fromAccountid === this.currentAccountId
        );
        break;
      case 'RECEIVED':
        this.filteredTransactions = this.transactions.filter(
          t => t.toAccountid === this.currentAccountId
        );
        break;
      default:
        this.filteredTransactions = [...this.transactions];
    }
  }

  isDebit(t: TransactionLog): boolean {
    return t.fromAccountid === this.currentAccountId;
  }

  getCounterparty(t: TransactionLog): string {
    if (this.isDebit(t)) {
      return `Account ${t.toAccountid}`;
    }
    return `Account ${t.fromAccountid}`;
  }

  getTransactionType(t: TransactionLog): string {
    return this.isDebit(t) ? 'Transfer to' : 'Received from';
  }

  isSuccess(t: TransactionLog): boolean {
    return t.status === TransactionStatus.SUCCESS || (t.status as any) === 'SUCCESS';
  }

  isFailed(t: TransactionLog): boolean {
    return t.status === TransactionStatus.FAILED || (t.status as any) === 'FAILED';
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  get totalSent(): number {
    return this.transactions
      .filter(t => this.isDebit(t) && this.isSuccess(t))
      .reduce((sum, t) => sum + Number(t.amount), 0);
  }

  get totalReceived(): number {
    return this.transactions
      .filter(t => !this.isDebit(t) && this.isSuccess(t))
      .reduce((sum, t) => sum + Number(t.amount), 0);
  }
}
