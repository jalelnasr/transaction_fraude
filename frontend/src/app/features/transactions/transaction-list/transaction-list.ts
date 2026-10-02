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
  totalPages = signal(0);
  currentPage = signal(0);
  readonly pageSize = 20;

  constructor(private transactionService: TransactionService, protected layoutService: LayoutService) {}

  ngOnInit(): void {
    this.load(0);
  }

  load(page: number): void {
    this.loading.set(true);
    this.transactionService.list(page, this.pageSize).subscribe({
      next: (result) => {
        this.transactions.set(result.content);
        this.totalElements.set(result.totalElements);
        this.totalPages.set(result.totalPages);
        this.currentPage.set(page);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.load(this.currentPage() - 1);
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.load(this.currentPage() + 1);
    }
  }

  displayId(id: string): string {
    return this.layoutService.sidebarCollapsed() ? id : `${id.substring(0, 8)}...`;
  }
}
