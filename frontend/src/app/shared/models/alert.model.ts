export type DecisionStatus = 'ACCEPTED' | 'MONITORED' | 'BLOCKED';
export type AlertStatus = 'OPEN' | 'VALIDATED' | 'DISMISSED' | 'ESCALATED';

export interface Alert {
  id: string;
  transactionId: string;
  decisionStatus: DecisionStatus;
  fusedScore: number;
  alertStatus: AlertStatus;
  emailSent: boolean;
  resolvedBy: string | null;
  resolutionReason: string | null;
  createdAt: string;
  resolvedAt: string | null;
}

export interface ResolveAlertRequest {
  status: AlertStatus;
  reason?: string;
}
