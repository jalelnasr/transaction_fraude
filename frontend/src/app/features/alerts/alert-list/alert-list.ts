import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
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

  constructor(private alertService: AlertService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.alertService.list().subscribe({
      next: (alerts) => {
        this.alerts.set(alerts.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()));
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
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
