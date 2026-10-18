import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PomodoroService, PomodoroMode } from '../../services/pomodoro.service';

@Component({
  selector: 'app-pomodoro',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pomodoro.component.html',
  styleUrls: ['./pomodoro.component.css']
})
export class PomodoroComponent {
  constructor(public pomodoroService: PomodoroService) {}

  get currentMode(): PomodoroMode {
    return this.pomodoroService.currentMode;
  }

  get formattedTime(): string {
    return this.pomodoroService.formattedTime;
  }

  get progressPercentage(): number {
    return this.pomodoroService.progressPercentage;
  }

  get isRunning(): boolean {
    return this.pomodoroService.isRunning;
  }

  get isCompleted(): boolean {
    return this.pomodoroService.isCompleted;
  }

  get completedPomodorosCount(): number {
    return this.pomodoroService.completedPomodorosCount;
  }

  setMode(mode: PomodoroMode): void {
    this.pomodoroService.setMode(mode);
  }

  startTimer(): void {
    this.pomodoroService.startTimer();
  }

  pauseTimer(): void {
    this.pomodoroService.pauseTimer();
  }

  resetTimer(): void {
    this.pomodoroService.resetTimer();
  }
}
