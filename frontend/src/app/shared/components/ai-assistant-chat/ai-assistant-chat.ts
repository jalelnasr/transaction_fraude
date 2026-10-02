import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, ViewChild, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { timeout } from 'rxjs';
import { environment } from '../../../../environments/environment';

interface ChatMessage {
  role: 'user' | 'assistant';
  text: string;
}

@Component({
  selector: 'app-ai-assistant-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-assistant-chat.html',
  styleUrl: './ai-assistant-chat.scss',
})
export class AiAssistantChat {
  open = signal(false);
  loading = signal(false);
  error = signal<string | null>(null);
  messages = signal<ChatMessage[]>([]);
  input = '';

  private readonly sessionId = crypto.randomUUID();

  @ViewChild('scrollAnchor') private scrollAnchor?: ElementRef<HTMLDivElement>;

  constructor(private http: HttpClient) {}

  toggle(): void {
    this.open.set(!this.open());
  }

  send(): void {
    const text = this.input.trim();
    if (!text || this.loading()) {
      return;
    }

    this.messages.update((msgs) => [...msgs, { role: 'user', text }]);
    this.input = '';
    this.error.set(null);
    this.loading.set(true);
    this.scrollToBottom();

    this.http
      .post<{ output: string }>(environment.aiAssistant.chatUrl, {
        chatInput: text,
        sessionId: this.sessionId,
      })
      .pipe(timeout(180000))
      .subscribe({
        next: (response) => {
          this.messages.update((msgs) => [
            ...msgs,
            { role: 'assistant', text: response.output ?? "Pas de reponse de l'assistant." },
          ]);
          this.loading.set(false);
          this.scrollToBottom();
        },
        error: () => {
          this.error.set("L'assistant IA n'a pas repondu. Verifiez qu'il est bien demarre.");
          this.loading.set(false);
        },
      });
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      this.scrollAnchor?.nativeElement.scrollIntoView({ behavior: 'smooth' });
    }, 50);
  }
}
