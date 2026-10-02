import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PageHeader } from '../../../shared/components/page-header/page-header';
import { Rule, RuleType } from '../../../shared/models/rule.model';
import { CreateRuleRequest, RuleService } from '../services/rule.service';

@Component({
  selector: 'app-rule-list',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeader],
  templateUrl: './rule-list.html',
  styleUrl: './rule-list.scss',
})
export class RuleList implements OnInit {
  rules = signal<Rule[]>([]);
  loading = signal(true);
  showForm = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  form: CreateRuleRequest = this.emptyForm();

  constructor(private ruleService: RuleService) {}

  ngOnInit(): void {
    this.fetchRules();
  }

  private fetchRules(): void {
    this.ruleService.list().subscribe({
      next: (rules) => {
        this.rules.set(rules);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  private emptyForm(): CreateRuleRequest {
    return {
      name: '',
      type: 'AMOUNT_THRESHOLD',
      active: true,
      weight: 0.1,
      thresholdAmount: null,
      riskCountries: null,
      maxTransactionsPerWindow: null,
      windowMinutes: null,
      nightStartHour: null,
      nightEndHour: null,
      blacklistedAccounts: null,
      deviationMultiplier: null,
    };
  }

  toggleForm(): void {
    this.showForm.set(!this.showForm());
    this.error.set(null);
    if (this.showForm()) {
      this.form = this.emptyForm();
    }
  }

  submit(): void {
    this.error.set(null);

    if (!this.form.name.trim()) {
      this.error.set('Le nom de la regle est obligatoire.');
      return;
    }
    if (this.form.weight <= 0 || this.form.weight > 1) {
      this.error.set('Le poids doit etre compris entre 0 et 1.');
      return;
    }

    if (this.form.type === 'AMOUNT_THRESHOLD' && !this.form.thresholdAmount) {
      this.error.set('Le seuil de montant est obligatoire pour ce type de regle.');
      return;
    }
    if (this.form.type === 'GEOLOCATION' && !this.form.riskCountries?.trim()) {
      this.error.set('La liste des pays a risque est obligatoire pour ce type de regle.');
      return;
    }
    if (
      (this.form.type === 'VELOCITY' || this.form.type === 'STRUCTURING') &&
      (!this.form.maxTransactionsPerWindow || !this.form.windowMinutes)
    ) {
      this.error.set('Le nombre max de transactions et la fenetre en minutes sont obligatoires.');
      return;
    }
    if (this.form.type === 'TIME_PATTERN' && (this.form.nightStartHour == null || this.form.nightEndHour == null)) {
      this.error.set("L'heure de debut et de fin sont obligatoires pour ce type de regle.");
      return;
    }
    if (this.form.type === 'BLACKLISTED_ACCOUNT' && !this.form.blacklistedAccounts?.trim()) {
      this.error.set('La liste des comptes blacklistes est obligatoire pour ce type de regle.');
      return;
    }
    if (this.form.type === 'AMOUNT_DEVIATION' && !this.form.deviationMultiplier) {
      this.error.set('Le multiplicateur est obligatoire pour ce type de regle.');
      return;
    }

    this.saving.set(true);
    this.ruleService.create(this.form).subscribe({
      next: () => {
        this.saving.set(false);
        this.showForm.set(false);
        this.loading.set(true);
        this.fetchRules();
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err?.error?.message || "Erreur lors de la creation de la regle.");
      },
    });
  }

  onTypeChange(type: RuleType): void {
    this.form.type = type;
  }

  describe(rule: Rule): string {
    switch (rule.type) {
      case 'AMOUNT_THRESHOLD':
        return `Montant > ${rule.thresholdAmount}`;
      case 'GEOLOCATION':
        return `Pays a risque : ${rule.riskCountries}`;
      case 'VELOCITY':
        return `> ${rule.maxTransactionsPerWindow} transactions / ${rule.windowMinutes} min`;
      case 'TIME_PATTERN':
        return `Entre ${rule.nightStartHour}h et ${rule.nightEndHour}h`;
      case 'NEW_BENEFICIARY':
        return 'Premiere transaction vers ce beneficiaire';
      case 'AMOUNT_DEVIATION':
        return `Montant > ${rule.deviationMultiplier}x la moyenne du client`;
      case 'BLACKLISTED_ACCOUNT':
        return `Comptes blacklistes : ${rule.blacklistedAccounts}`;
      case 'STRUCTURING':
        return `> ${rule.maxTransactionsPerWindow} vers le meme beneficiaire / ${rule.windowMinutes} min`;
      case 'LOCATION_CHANGE':
        return 'Changement de pays par rapport a la derniere transaction';
      case 'NETWORK_CYCLE':
        return 'Compte implique dans un cycle de transferts (analyse de graphe)';
      default:
        return '';
    }
  }
}
