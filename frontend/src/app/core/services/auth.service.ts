import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

interface TokenResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
}

interface DecodedToken {
  preferred_username: string;
  realm_access?: { roles: string[] };
  exp: number;
}

const TOKEN_KEY = 'fraud_platform_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenUrl = `${environment.keycloak.url}/realms/${environment.keycloak.realm}/protocol/openid-connect/token`;

  readonly isAuthenticated = signal<boolean>(this.hasValidToken());
  readonly username = signal<string | null>(this.decodeToken()?.preferred_username ?? null);
  readonly roles = signal<string[]>(this.decodeToken()?.realm_access?.roles ?? []);

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<TokenResponse> {
    const body = new URLSearchParams();
    body.set('client_id', environment.keycloak.clientId);
    body.set('grant_type', 'password');
    body.set('username', username);
    body.set('password', password);

    return this.http
      .post<TokenResponse>(this.tokenUrl, body.toString(), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      })
      .pipe(
        tap((response) => {
          localStorage.setItem(TOKEN_KEY, response.access_token);
          const decoded = this.decodeToken();
          this.isAuthenticated.set(true);
          this.username.set(decoded?.preferred_username ?? null);
          this.roles.set(decoded?.realm_access?.roles ?? []);
        })
      );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.isAuthenticated.set(false);
    this.username.set(null);
    this.roles.set([]);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  hasRole(role: string): boolean {
    return this.roles().includes(role);
  }

  private hasValidToken(): boolean {
    const decoded = this.decodeToken();
    if (!decoded) return false;
    return decoded.exp * 1000 > Date.now();
  }

  private decodeToken(): DecodedToken | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    } catch {
      return null;
    }
  }
}
