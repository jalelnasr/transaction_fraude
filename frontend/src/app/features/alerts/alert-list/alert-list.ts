import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PageHeader } from '../../../shared/components/page-header/page-header';
import { Alert, AlertStatus } from '../../../shared/models/alert.model';
import { AlertService } from '../services/alert.service';

@Component({
  selector: 'app-alert-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PageHeader],
  templateUrl: './alert-list.html',
  styleUrl: './alert-list.scss',
})
export class AlertList implements OnInit {
  alerts = signal<Alert[]>([]);
  loading = signal(true);
  activeAlertId = signal<string | null>(null);
  reason = '';
  error = signal<string | null>(null);

  currentPage = signal(0);
  readonly pageSize = 15;

  totalPages = computed(() => Math.max(1, Math.ceil(this.alerts().length / this.pageSize)));
  pagedAlerts = computed(() => {
    const start = this.currentPage() * this.pageSize;
    return this.alerts().slice(start, start + this.pageSize);
  });

  constructor(private alertService: AlertService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.alertService.list().subscribe({
      next: (alerts) => {
        this.alerts.set(alerts.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()));
        this.currentPage.set(0);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update((p) => p - 1);
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update((p) => p + 1);
    }
  }

  startResolve(alertId: string): void {
    this.activeAlertId.set(alertId);
    this.reason = '';
    this.error.set(null);
  }

  cancelResolve(): void {
    this.activeAlertId.set(null);
  }

  resolve(status: AlertStatus): void {
    const alertId = this.activeAlertId();
    if (!alertId) return;

    if (status === 'DISMISSED' && !this.reason.trim()) {
      this.error.set('Un motif est requis pour lever une alerte.');
      return;
    }

    this.alertService.resolve(alertId, { status, reason: this.reason || undefined }).subscribe({
      next: () => {
        this.activeAlertId.set(null);
        this.load();
      },
      error: () => this.error.set("Erreur lors de la resolution de l'alerte."),
    });
  }
}
