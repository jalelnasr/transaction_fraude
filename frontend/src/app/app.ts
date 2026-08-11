import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { LayoutService } from './core/services/layout.service';

const KNOWN_ROLES = ['ANALYST', 'ADMINISTRATOR'];

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  constructor(protected authService: AuthService, protected layoutService: LayoutService, private router: Router) {}

  get displayRole(): string {
    const role = this.authService.roles().find((r) => KNOWN_ROLES.includes(r));
    return role ?? '';
  }

  toggleSidebar(): void {
    this.layoutService.toggleSidebar();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
