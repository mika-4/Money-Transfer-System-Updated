import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AccountService } from '../../services/account.service';
import { TransferService } from '../../services/transfer.service';
import { Account, TransferRequest, TransferResponse } from '../../models';


@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent implements OnInit {
  transferForm!: FormGroup;
  account: Account | null = null;
  isLoading = false;
  isLoadingAccount = true;
  isCheckingToAccount = false;
  errorMessage = '';
  successMessage = '';
  transferResult: TransferResponse | null = null;
  showConfirmModal = false;
  showMpinField = false;
  showMpin = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private accountService: AccountService,
    private transferService: TransferService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.initForm();
    this.loadAccount();
  }

  private initForm(): void {
    this.transferForm = this.fb.group({
      toAccountId: ['', [
        Validators.required,
        Validators.pattern(/^\d+$/)
      ]],
      amount: ['', [
        Validators.required,
        Validators.min(0.01),
        Validators.max(10000000)
      ]],
      remarks: [''],
      mpin: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]]
    });
  }

  private loadAccount(): void {
    const accountId = this.authService.getAccountId();
    if (!accountId) { this.router.navigate(['/login']); return; }

    this.accountService.getAccount(accountId).subscribe({
      next: (account) => {
        this.account = account;
        this.isLoadingAccount = false;
      },
      error: () => {
        this.isLoadingAccount = false;
        this.errorMessage = 'Could not load account details.';
      }
    });
  }

  get toAccountId() { return this.transferForm.get('toAccountId')!; }
  get amount() { return this.transferForm.get('amount')!; }
  get mpin() { return this.transferForm.get('mpin') as FormControl; }

  openConfirmModal(): void {
    // Validate destination and amount only — MPIN is entered in the confirmation modal.
    const toIdCtrl = this.toAccountId;
    const amountCtrl = this.amount;
    if (toIdCtrl.invalid || amountCtrl.invalid) {
      toIdCtrl.markAsTouched();
      amountCtrl.markAsTouched();
      return;
    }

    // Verify destination account exists before any payment attempts
    const toId = parseInt(this.toAccountId.value, 10);
    this.isCheckingToAccount = true;
    this.accountService.getAccount(toId).subscribe({
      next: () => {
        this.isCheckingToAccount = false;
        // Validate amount
        const amount = parseFloat(this.amount.value);
        if (this.account && amount > this.account.balance) {
          this.errorMessage = `Insufficient balance. Available: ₹${this.account.balance.toFixed(2)}`;
          return;
        }

        if (this.account && toId === this.account.id) {
          this.errorMessage = 'Cannot transfer to your own account.';
          return;
        }

        this.errorMessage = '';
        this.showConfirmModal = true;
      },
      error: () => {
        this.isCheckingToAccount = false;
        this.errorMessage = 'Destination account does not exist.';
      }
    });
  }

  confirmTransfer(): void {
    const mpinCtrl = this.transferForm.get('mpin');
    if (mpinCtrl && mpinCtrl.invalid) {
      mpinCtrl.markAsTouched();
      return;
    }
    // trim MPIN to avoid sending accidental whitespace
    if (mpinCtrl) {
      const trimmed = (mpinCtrl.value || '').toString().trim();
      mpinCtrl.setValue(trimmed);
    }
    this.showConfirmModal = false;
    this.submitTransfer();
  }

  cancelConfirm(): void {
    this.showConfirmModal = false;
  }

  toggleShowMpin(): void {
    this.showMpin = !this.showMpin;
  }

  private submitTransfer(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: TransferRequest = {
      fromAccountid: this.account!.id,
      toAccountId: parseInt(this.toAccountId.value, 10),
      amount: parseFloat(this.amount.value),
      idempotencyKey: this.generateUUID(),
      remarks: this.transferForm.get('remarks')?.value || '',
      mpin: (this.transferForm.get('mpin')?.value || '').toString().trim()
    };

    // debug: log the outgoing transfer payload (trimmed mpin). Remove in production.
    console.debug('Submitting transfer request:', request);
    this.transferService.transfer(request).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.transferResult = response;
        this.successMessage = `Transfer of ₹${response.amount} completed successfully!` + (response.rewardPoints ? ` Earned ${response.rewardPoints} points.` : '');
        // Update balance
        if (this.account) {
          this.account.balance -= request.amount;
        }
        this.transferForm.reset();
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err?.error?.message || err?.error || 'Transfer failed. Please try again.';
      }
    });
  }

  private generateUUID(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }

  resetForm(): void {
    this.transferForm.reset();
    this.errorMessage = '';
    this.successMessage = '';
    this.transferResult = null;
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
}
