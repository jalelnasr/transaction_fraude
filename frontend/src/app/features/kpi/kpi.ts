import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, signal } from '@angular/core';
import { environment } from '../../../environments/environment';
import { PageHeader } from '../../shared/components/page-header/page-header';
import { Alert } from '../../shared/models/alert.model';
import { Transaction } from '../../shared/models/transaction.model';
import { AlertService } from '../alerts/services/alert.service';
import { TransactionService } from '../transactions/services/transaction.service';

interface ModelMetadata {
  model_version: string;
  dataset: string;
  trained_at: string;
  training_rows: number;
  test_rows: number;
  roc_auc: number;
}

interface CurrencyAmount {
  currency: string;
  total: number;
}

@Component({
  selector: 'app-kpi',
  standalone: true,
  imports: [CommonModule, PageHeader],
  templateUrl: './kpi.html',
  styleUrl: './kpi.scss',
})
export class Kpi implements OnInit {
  alerts = signal<Alert[]>([]);
  transactions = signal<Transaction[]>([]);
  modelMetadata = signal<ModelMetadata | null>(null);
  loading = signal(true);

  constructor(
    private alertService: AlertService,
    private transactionService: TransactionService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.alertService.list().subscribe({
      next: (alerts) => this.alerts.set(alerts),
      error: () => this.alerts.set([]),
    });

    this.transactionService.list(0, 200).subscribe({
      next: (page) => {
        this.transactions.set(page.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });

    this.http.get<ModelMetadata>(`${environment.apiUrl}/api/models/current`).subscribe({
      next: (meta) => this.modelMetadata.set(meta),
      error: () => this.modelMetadata.set(null),
    });
  }

  get avgProcessingSeconds(): number | null {
    const txById = new Map(this.transactions().map((t) => [t.transactionId, t]));
    const diffs: number[] = [];

    for (const alert of this.alerts()) {
      const tx = txById.get(alert.transactionId);
      if (!tx) continue;
      const diff = new Date(alert.createdAt).getTime() - new Date(tx.timestamp).getTime();
      if (diff >= 0) diffs.push(diff);
    }

    if (!diffs.length) return null;
    return diffs.reduce((sum, d) => sum + d, 0) / diffs.length / 1000;
  }

  get resolvedAlertsCount(): number {
    return this.alerts().filter((a) => a.alertStatus !== 'OPEN').length;
  }

  get falsePositiveRate(): number | null {
    const resolved = this.resolvedAlertsCount;
    if (resolved === 0) return null;
    const dismissed = this.alerts().filter((a) => a.alertStatus === 'DISMISSED').length;
    return (dismissed / resolved) * 100;
  }

  get blockedAmountByCurrency(): CurrencyAmount[] {
    const totals = new Map<string, number>();
    for (const t of this.transactions()) {
      if (t.status !== 'BLOCKED') continue;
      totals.set(t.currency, (totals.get(t.currency) ?? 0) + t.amount);
    }
    return Array.from(totals.entries())
      .map(([currency, total]) => ({ currency, total }))
      .sort((a, b) => b.total - a.total);
  }

  get staleOpenAlerts(): Alert[] {
    const cutoff = Date.now() - 24 * 60 * 60 * 1000;
    return this.alerts().filter(
      (a) => a.alertStatus === 'OPEN' && new Date(a.createdAt).getTime() < cutoff
    );
  }

  get biggestBlockedTransaction(): Transaction | null {
    const blocked = this.transactions().filter((t) => t.status === 'BLOCKED');
    if (!blocked.length) return null;
    return blocked.reduce((max, t) => (t.amount > max.amount ? t : max), blocked[0]);
  }

  get distinctCountriesCount(): number {
    const countries = new Set(this.transactions().map((t) => t.country).filter(Boolean));
    return countries.size;
  }
}
