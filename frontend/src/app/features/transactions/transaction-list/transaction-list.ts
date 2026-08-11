import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LayoutService } from '../../../core/services/layout.service';
import { PageHeader } from '../../../shared/components/page-header/page-header';
import { Transaction } from '../../../shared/models/transaction.model';
import { TransactionService } from '../services/transaction.service';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeader],
  templateUrl: './transaction-list.html',
  styleUrl: './transaction-list.scss',
})
export class TransactionList implements OnInit {
  transactions = signal<Transaction[]>([]);
  loading = signal(true);
  totalElements = signal(0);

  constructor(private transactionService: TransactionService, protected layoutService: LayoutService) {}

  ngOnInit(): void {
    this.transactionService.list().subscribe({
      next: (page) => {
        this.transactions.set(page.content);
        this.totalElements.set(page.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  displayId(id: string): string {
    return this.layoutService.sidebarCollapsed() ? id : `${id.substring(0, 8)}...`;
  }
}
