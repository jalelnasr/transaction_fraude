import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Alert, ResolveAlertRequest } from '../../../shared/models/alert.model';

@Injectable({ providedIn: 'root' })
export class AlertService {
  private readonly baseUrl = `${environment.apiUrl}/api/alerts`;

  constructor(private http: HttpClient) {}

  list(): Observable<Alert[]> {
    return this.http.get<Alert[]>(this.baseUrl);
  }

  resolve(alertId: string, request: ResolveAlertRequest): Observable<Alert> {
    return this.http.post<Alert>(`${this.baseUrl}/${alertId}/resolve`, request);
  }
}
