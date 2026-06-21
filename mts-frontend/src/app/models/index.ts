// Account model
export interface Account {
  id: number;
  holderName: string;
  balance: number;
  version: number;
  lastUpdated: string;
  status: AccountStatus;
}

export enum AccountStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED'
}

// Transaction models
export interface TransactionLog {
  id: string;
  fromAccountid: number;
  toAccountid: number;
  amount: number;
  status: TransactionStatus;
  failureReason?: string;
  idempotencyKey: string;
  createdOn: string;
  note?: string;
  rewardPoints?: number;
}

export enum TransactionStatus {
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
  PENDING = 'PENDING'
}

// Transfer models
export interface TransferRequest {
  fromAccountid: number;
  toAccountId: number;
  amount: number;
  idempotencyKey: string;
  remarks?: string;
}

export interface TransferResponse {
  transactionId: string;
  status: AccountStatus;
  message: string;
  debitedFrom: number;
  creditedTo: number;
  amount: number;
  rewardPoints?: number;
  note?: string;
}

// Auth models
export interface LoginCredentials {
  username: string;
  password: string;
}

export interface AuthUser {
  accountId: number;
  holderName: string;
  token: string;
}
