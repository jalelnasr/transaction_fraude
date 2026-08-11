import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PageHeader } from '../../../shared/components/page-header/page-header';
import { AlertStatus } from '../../../shared/models/alert.model';
import { Explanation } from '../../../shared/models/explanation.model';
import { Decision, Transaction } from '../../../shared/models/transaction.model';
import { AlertService } from '../../alerts/services/alert.service';
import { TransactionService } from '../services/transaction.service';

@Component({
  selector: 'app-transaction-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeader],
  templateUrl: './transaction-detail.html',
  styleUrl: './transaction-detail.scss',
})
export class TransactionDetail implements OnInit {
  transaction = signal<Transaction | null>(null);
  decision = signal<Decision | null>(null);
  explanation = signal<Explanation | null>(null);
  loading = signal(true);

  alertId: string | null = null;
  reason = '';
  resolveError = signal<string | null>(null);
  resolved = signal(false);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private transactionService: TransactionService,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
    const transactionId = this.route.snapshot.paramMap.get('id');
    this.alertId = this.route.snapshot.queryParamMap.get('alertId');

    if (!transactionId) {
      this.loading.set(false);
      return;
    }

    this.transactionService.getById(transactionId).subscribe({
      next: (tx) => this.transaction.set(tx),
      error: () => {},
    });

    this.transactionService.getDecision(transactionId).subscribe({
      next: (decision) => {
        this.decision.set(decision);
        this.loading.set(false);

        if (decision.status !== 'ACCEPTED') {
          this.transactionService.getExplanation(transactionId).subscribe({
            next: (exp) => this.explanation.set(exp),
            error: () => {},
          });
        }
      },
      error: () => this.loading.set(false),
    });
  }

  resolve(status: AlertStatus): void {
    if (!this.alertId) return;

    if (status === 'DISMISSED' && !this.reason.trim()) {
      this.resolveError.set('Un motif est requis pour lever une alerte.');
      return;
    }

    this.alertService.resolve(this.alertId, { status, reason: this.reason || undefined }).subscribe({
      next: () => this.resolved.set(true),
      error: () => this.resolveError.set("Erreur lors de la resolution de l'alerte."),
    });
  }

  goBack(): void {
    this.router.navigate(['/alerts']);
  }
}
