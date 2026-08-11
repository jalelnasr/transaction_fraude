import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { Login } from './features/auth/login/login';

export const routes: Routes = [
  { path: 'login', component: Login },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard),
  },
  {
    path: 'transactions',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/transactions/transaction-list/transaction-list').then((m) => m.TransactionList),
  },
  {
    path: 'transactions/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/transactions/transaction-detail/transaction-detail').then((m) => m.TransactionDetail),
  },
  {
    path: 'alerts',
    canActivate: [authGuard],
    loadComponent: () => import('./features/alerts/alert-list/alert-list').then((m) => m.AlertList),
  },
  {
    path: 'rules',
    canActivate: [authGuard],
    loadComponent: () => import('./features/rules/rule-list/rule-list').then((m) => m.RuleList),
  },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' },
];
