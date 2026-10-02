import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Rule } from '../../../shared/models/rule.model';

export interface CreateRuleRequest {
  name: string;
  type: Rule['type'];
  active: boolean;
  weight: number;
  thresholdAmount?: number | null;
  riskCountries?: string | null;
  maxTransactionsPerWindow?: number | null;
  windowMinutes?: number | null;
  nightStartHour?: number | null;
  nightEndHour?: number | null;
  blacklistedAccounts?: string | null;
  deviationMultiplier?: number | null;
}

@Injectable({ providedIn: 'root' })
export class RuleService {
  private readonly baseUrl = `${environment.apiUrl}/api/rules`;

  constructor(private http: HttpClient) {}

  list(): Observable<Rule[]> {
    return this.http.get<Rule[]>(this.baseUrl);
  }

  create(rule: CreateRuleRequest): Observable<Rule> {
    return this.http.post<Rule>(this.baseUrl, rule);
  }
}
