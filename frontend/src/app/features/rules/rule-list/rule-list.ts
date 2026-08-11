import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { PageHeader } from '../../../shared/components/page-header/page-header';
import { Rule } from '../../../shared/models/rule.model';
import { RuleService } from '../services/rule.service';

@Component({
  selector: 'app-rule-list',
  standalone: true,
  imports: [CommonModule, PageHeader],
  templateUrl: './rule-list.html',
  styleUrl: './rule-list.scss',
})
export class RuleList implements OnInit {
  rules = signal<Rule[]>([]);
  loading = signal(true);

  constructor(private ruleService: RuleService) {}

  ngOnInit(): void {
    this.ruleService.list().subscribe({
      next: (rules) => {
        this.rules.set(rules);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
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
      default:
        return '';
    }
  }
}
