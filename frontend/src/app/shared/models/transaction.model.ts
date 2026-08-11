export type TransactionStatus = 'RECEIVED' | 'EVALUATING' | 'ACCEPTED' | 'MONITORED' | 'BLOCKED' | 'FAILED';

export interface Transaction {
  transactionId: string;
  amount: number;
  currency: string;
  sourceAccountId: string;
  destinationAccountId: string;
  channel: string;
  country: string | null;
  status: TransactionStatus;
  timestamp: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface Decision {
  transactionId: string;
  status: 'ACCEPTED' | 'MONITORED' | 'BLOCKED';
  fusedScore: number;
  ruleScore: number | null;
  mlScore: number | null;
  degradedMode: boolean;
  decidedAt: string;
}
