import { Injectable, signal, computed, ApplicationRef, inject } from '@angular/core';

export type PomodoroMode = 'FOCUS' | 'SHORT_BREAK' | 'LONG_BREAK';

@Injectable({
  providedIn: 'root'
})
export class PomodoroService {
  // --- Reactive Signals (drive Angular change detection in zoneless mode) ---
  private _currentMode = signal<PomodoroMode>('FOCUS');
  private _totalSeconds = signal<number>(25 * 60);
  private _remainingSeconds = signal<number>(25 * 60);
  private _isRunning = signal<boolean>(false);
  private _isCompleted = signal<boolean>(false);
  private _completedPomodorosCount = signal<number>(0);

  private timerInterval: any = null;
  private appRef = inject(ApplicationRef);

  constructor() {}

  // --- Public accessors (used by the component template) ---
  get currentMode(): PomodoroMode {
    return this._currentMode();
  }

  get isRunning(): boolean {
    return this._isRunning();
  }

  get isCompleted(): boolean {
    return this._isCompleted();
  }

  get completedPomodorosCount(): number {
    return this._completedPomodorosCount();
  }

  get remainingSeconds(): number {
    return this._remainingSeconds();
  }

  get totalSeconds(): number {
    return this._totalSeconds();
  }

  get formattedTime(): string {
    const remaining = this._remainingSeconds();
    const minutes = Math.floor(remaining / 60);
    const seconds = remaining % 60;
    return `${this.padZero(minutes)}:${this.padZero(seconds)}`;
  }

  get progressPercentage(): number {
    const total = this._totalSeconds();
    if (total <= 0) return 0;
    return Math.round(((total - this._remainingSeconds()) / total) * 100);
  }

  setMode(mode: PomodoroMode): void {
    this.clearTimer();
    this._currentMode.set(mode);
    this._isCompleted.set(false);

    if (mode === 'FOCUS') {
      this._totalSeconds.set(25 * 60);
    } else if (mode === 'SHORT_BREAK') {
      this._totalSeconds.set(5 * 60);
    } else {
      this._totalSeconds.set(15 * 60);
    }

    this._remainingSeconds.set(this._totalSeconds());
    this._isRunning.set(false);
    this.updateDocumentTitle();
  }

  startTimer(): void {
    if (this._isRunning()) return;

    this._isRunning.set(true);
    this._isCompleted.set(false);
    this.updateDocumentTitle();

    this.timerInterval = setInterval(() => {
      if (this._remainingSeconds() > 0) {
        this._remainingSeconds.update(v => v - 1);
        this.updateDocumentTitle();
      } else {
        this.onTimerFinished();
      }
      this.appRef.tick();
    }, 1000);
  }

  pauseTimer(): void {
    this._isRunning.set(false);
    this.clearTimer();
    this.updateDocumentTitle();
  }

  resetTimer(): void {
    this.pauseTimer();
    this._isCompleted.set(false);
    this._remainingSeconds.set(this._totalSeconds());
    this.updateDocumentTitle();
  }

  private onTimerFinished(): void {
    this.clearTimer();
    this._isRunning.set(false);
    this._isCompleted.set(true);

    if (this._currentMode() === 'FOCUS') {
      this._completedPomodorosCount.update(v => v + 1);
    }

    this.playNotificationSound();
    this.updateDocumentTitle();
  }

  private clearTimer(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  }

  private updateDocumentTitle(): void {
    if (typeof document === 'undefined') return;

    if (this._isRunning()) {
      const modeIcon = this._currentMode() === 'FOCUS' ? '🎯' : '☕';
      document.title = `(${this.formattedTime}) ${modeIcon} FocusFlow`;
    } else if (this._remainingSeconds() < this._totalSeconds() && !this._isCompleted()) {
      document.title = `[Paused ${this.formattedTime}] FocusFlow`;
    } else {
      document.title = 'FocusFlow — Smart Study Planner';
    }
  }

  private playNotificationSound(): void {
    try {
      const audioCtx = new (window.AudioContext || (window as any).webkitAudioContext)();
      const osc = audioCtx.createOscillator();
      const gain = audioCtx.createGain();
      osc.type = 'sine';
      osc.frequency.value = 587.33; // D5 note
      gain.gain.setValueAtTime(0.2, audioCtx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.0001, audioCtx.currentTime + 1.2);
      osc.connect(gain);
      gain.connect(audioCtx.destination);
      osc.start();
      osc.stop(audioCtx.currentTime + 1.2);
    } catch (e) {
      // Audio fallback silent
    }
  }

  private padZero(num: number): string {
    return num < 10 ? `0${num}` : `${num}`;
  }
}
