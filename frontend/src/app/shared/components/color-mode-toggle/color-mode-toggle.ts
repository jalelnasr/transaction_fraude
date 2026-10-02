import { Component } from '@angular/core';
import { ColorModeService } from '../../../core/services/color-mode.service';

@Component({
  selector: 'app-color-mode-toggle',
  standalone: true,
  templateUrl: './color-mode-toggle.html',
  styleUrl: './color-mode-toggle.scss',
})
export class ColorModeToggle {
  constructor(protected colorModeService: ColorModeService) {}
}
