export type RuleType = 'AMOUNT_THRESHOLD' | 'GEOLOCATION' | 'VELOCITY' | 'TIME_PATTERN';

export interface Rule {
  id: string;
  name: string;
  type: RuleType;
  active: boolean;
  weight: number;
  thresholdAmount: number | null;
  riskCountries: string | null;
  maxTransactionsPerWindow: number | null;
  windowMinutes: number | null;
  nightStartHour: number | null;
  nightEndHour: number | null;
  createdAt: string;
  updatedAt: string | null;
}
