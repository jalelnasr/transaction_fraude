import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LayoutService } from '../../core/services/layout.service';
import { PageHeader } from '../../shared/components/page-header/page-header';
import { Alert } from '../../shared/models/alert.model';
import { Transaction } from '../../shared/models/transaction.model';
import { AlertService } from '../alerts/services/alert.service';
import { TransactionService } from '../transactions/services/transaction.service';

interface CurrencyTotal {
  currency: string;
  total: number;
  count: number;
  percentOfMax: number;
}

interface CountBreakdown {
  label: string;
  count: number;
  percentOfMax: number;
}

interface RadialGauge {
  label: string;
  percent: number;
  color: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, PageHeader],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  alerts = signal<Alert[]>([]);
  transactions = signal<Transaction[]>([]);
  loading = signal(true);
  searchTerm = '';

  constructor(
    private alertService: AlertService,
    private transactionService: TransactionService,
    protected layoutService: LayoutService
  ) {}

  ngOnInit(): void {
    this.alertService.list().subscribe({
      next: (alerts) => {
        this.alerts.set(alerts);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });

    this.transactionService.list(0, 200).subscribe({
      next: (page) => this.transactions.set(page.content),
      error: () => this.transactions.set([]),
    });
  }

  get openCount(): number {
    return this.alerts().filter((a) => a.alertStatus === 'OPEN').length;
  }

  get blockedCount(): number {
    return this.alerts().filter((a) => a.decisionStatus === 'BLOCKED').length;
  }

  get monitoredCount(): number {
    return this.alerts().filter((a) => a.decisionStatus === 'MONITORED').length;
  }

  get recentAlerts(): Alert[] {
    const term = this.searchTerm.trim().toLowerCase();
    const sorted = [...this.alerts()].sort(
      (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    );

    const filtered = term
      ? sorted.filter(
          (a) =>
            a.transactionId.toLowerCase().includes(term) ||
            a.decisionStatus.toLowerCase().includes(term) ||
            a.alertStatus.toLowerCase().includes(term)
        )
      : sorted;

    return filtered.slice(0, term ? filtered.length : 8);
  }

  displayId(id: string): string {
    return this.layoutService.sidebarCollapsed() ? id : `${id.substring(0, 8)}...`;
  }

  get totalTransactionCount(): number {
    return this.transactions().length;
  }

  get currencyBreakdown(): CurrencyTotal[] {
    const totals = new Map<string, { total: number; count: number }>();
    for (const t of this.transactions()) {
      const entry = totals.get(t.currency) ?? { total: 0, count: 0 };
      entry.total += t.amount;
      entry.count += 1;
      totals.set(t.currency, entry);
    }

    const maxTotal = Math.max(...Array.from(totals.values()).map((v) => v.total), 1);

    return Array.from(totals.entries())
      .map(([currency, v]) => ({
        currency,
        total: v.total,
        count: v.count,
        percentOfMax: (v.total / maxTotal) * 100,
      }))
      .sort((a, b) => b.total - a.total);
  }

  get statusBreakdown(): CountBreakdown[] {
    const labels = ['ACCEPTED', 'MONITORED', 'BLOCKED', 'RECEIVED', 'EVALUATING', 'FAILED'];
    const counts = new Map<string, number>();
    for (const t of this.transactions()) {
      counts.set(t.status, (counts.get(t.status) ?? 0) + 1);
    }

    const entries = labels
      .filter((l) => (counts.get(l) ?? 0) > 0)
      .map((l) => ({ label: l, count: counts.get(l) ?? 0 }));

    const maxCount = Math.max(...entries.map((e) => e.count), 1);

    return entries.map((e) => ({ ...e, percentOfMax: (e.count / maxCount) * 100 }));
  }

  get channelBreakdown(): CountBreakdown[] {
    const counts = new Map<string, number>();
    for (const t of this.transactions()) {
      counts.set(t.channel, (counts.get(t.channel) ?? 0) + 1);
    }

    const maxCount = Math.max(...Array.from(counts.values()), 1);

    return Array.from(counts.entries())
      .map(([label, count]) => ({ label, count, percentOfMax: (count / maxCount) * 100 }))
      .sort((a, b) => b.count - a.count);
  }

  private percentOf(count: number, total: number): number {
    return total === 0 ? 0 : Math.round((count / total) * 100);
  }

  get keyGauges(): RadialGauge[] {
    const total = this.totalTransactionCount;
    const accepted = this.transactions().filter((t) => t.status === 'ACCEPTED').length;
    const monitored = this.transactions().filter((t) => t.status === 'MONITORED').length;
    const blocked = this.transactions().filter((t) => t.status === 'BLOCKED').length;
    const totalAlerts = this.alerts().length;
    const resolvedAlerts = this.alerts().filter((a) => a.alertStatus !== 'OPEN').length;

    return [
      { label: 'Acceptees', percent: this.percentOf(accepted, total), color: '#00d9a3' },
      { label: 'Surveillees', percent: this.percentOf(monitored, total), color: '#fb923c' },
      { label: 'Bloquees', percent: this.percentOf(blocked, total), color: '#f87171' },
      { label: 'Alertes traitees', percent: this.percentOf(resolvedAlerts, totalAlerts), color: '#818cf8' },
    ];
  }

  get globalDetectionRate(): number {
    const total = this.totalTransactionCount;
    const flagged = this.transactions().filter(
      (t) => t.status === 'MONITORED' || t.status === 'BLOCKED'
    ).length;
    return this.percentOf(flagged, total);
  }

  get channelBlockRates(): RadialGauge[] {
    const colors = ['#c084fc', '#f472b6', '#38bdf8', '#facc15'];
    const channels = new Map<string, { total: number; blocked: number }>();
    for (const t of this.transactions()) {
      const entry = channels.get(t.channel) ?? { total: 0, blocked: 0 };
      entry.total += 1;
      if (t.status === 'BLOCKED') entry.blocked += 1;
      channels.set(t.channel, entry);
    }

    return Array.from(channels.entries())
      .sort((a, b) => b[1].total - a[1].total)
      .slice(0, 3)
      .map(([label, v], i) => ({
        label,
        percent: this.percentOf(v.blocked, v.total),
        color: colors[i % colors.length],
      }));
  }

  gaugeStyle(gauge: RadialGauge): Record<string, string> {
    return {
      background: `conic-gradient(${gauge.color} ${gauge.percent}%, #14231e ${gauge.percent}% 100%)`,
    };
  }

  get scoreTrendPath(): { area: string; line: string } {
    const sorted = [...this.alerts()].sort(
      (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
    );
    const values = sorted.length ? sorted.map((a) => a.fusedScore) : [0];

    const width = 560;
    const height = 150;
    const stepX = width / (values.length - 1 || 1);
    const points = values.map((v, i) => {
      const x = i * stepX;
      const y = height - Math.min(Math.max(v, 0), 1) * (height - 20) - 10;
      return `${x.toFixed(1)},${y.toFixed(1)}`;
    });

    const line = `M${points.join(' L')}`;
    const area = `${line} L${width},${height} L0,${height} Z`;
    return { area, line };
  }

  get topRiskyCountries(): CountBreakdown[] {
    const counts = new Map<string, number>();
    for (const t of this.transactions()) {
      if (!t.country) continue;
      counts.set(t.country, (counts.get(t.country) ?? 0) + 1);
    }

    const maxCount = Math.max(...Array.from(counts.values()), 1);

    return Array.from(counts.entries())
      .map(([label, count]) => ({ label, count, percentOfMax: (count / maxCount) * 100 }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 6);
  }

  get avgScoreByStatus(): CountBreakdown[] {
    const labels: Array<'ACCEPTED' | 'MONITORED' | 'BLOCKED'> = ['ACCEPTED', 'MONITORED', 'BLOCKED'];
    return labels
      .map((label) => {
        const matching = this.alerts().filter((a) => a.decisionStatus === label);
        const avg = matching.length
          ? matching.reduce((sum, a) => sum + a.fusedScore, 0) / matching.length
          : 0;
        return { label, count: Math.round(avg * 100), percentOfMax: Math.round(avg * 100) };
      })
      .filter((e) => e.count > 0);
  }
}
