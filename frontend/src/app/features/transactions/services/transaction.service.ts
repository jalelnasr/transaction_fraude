import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Explanation } from '../../../shared/models/explanation.model';
import { Decision, Page, Transaction } from '../../../shared/models/transaction.model';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private readonly baseUrl = `${environment.apiUrl}/api/transactions`;

  constructor(private http: HttpClient) {}

  list(page = 0, size = 20): Observable<Page<Transaction>> {
    return this.http.get<Page<Transaction>>(this.baseUrl, {
      params: { page, size, sort: 'timestamp,desc' },
    });
  }

  getById(transactionId: string): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.baseUrl}/${transactionId}`);
  }

  getDecision(transactionId: string): Observable<Decision> {
    return this.http.get<Decision>(`${this.baseUrl}/${transactionId}/decision`);
  }

  getExplanation(transactionId: string): Observable<Explanation> {
    return this.http.get<Explanation>(`${this.baseUrl}/${transactionId}/explanation`);
  }
}
