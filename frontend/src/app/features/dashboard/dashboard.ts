import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LayoutService } from '../../core/services/layout.service';
import { PageHeader } from '../../shared/components/page-header/page-header';
import { Alert } from '../../shared/models/alert.model';
import { AlertService } from '../alerts/services/alert.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeader, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  alerts = signal<Alert[]>([]);
  loading = signal(true);
  searchTerm = '';

  constructor(private alertService: AlertService, protected layoutService: LayoutService) {}

  ngOnInit(): void {
    this.alertService.list().subscribe({
      next: (alerts) => {
        this.alerts.set(alerts);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
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
}
