import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Rule } from '../../../shared/models/rule.model';

@Injectable({ providedIn: 'root' })
export class RuleService {
  private readonly baseUrl = `${environment.apiUrl}/api/rules`;

  constructor(private http: HttpClient) {}

  list(): Observable<Rule[]> {
    return this.http.get<Rule[]>(this.baseUrl);
  }
}
