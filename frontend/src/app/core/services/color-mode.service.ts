import { Injectable, effect, signal } from '@angular/core';

export type ColorMode = 'light' | 'dark';

const STORAGE_KEY = 'color-mode';

@Injectable({ providedIn: 'root' })
export class ColorModeService {
  readonly mode = signal<ColorMode>(this.readStoredMode());

  constructor() {
    effect(() => {
      const mode = this.mode();
      document.body.setAttribute('data-color-mode', mode);
      localStorage.setItem(STORAGE_KEY, mode);
    });
  }

  toggle(): void {
    this.mode.set(this.mode() === 'dark' ? 'light' : 'dark');
  }

  private readStoredMode(): ColorMode {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored === 'light' ? 'light' : 'dark';
  }
}
