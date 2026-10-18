import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudySessionService } from '../../services/study-session.service';
import { StudySession, SessionStatus } from '../../models/study-session.model';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit {
  selectedDate: string = '';
  availableHours: number = 3.0;

  sessions: StudySession[] = [];
  isLoading: boolean = false;
  isGenerating: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private studySessionService: StudySessionService) {}

  ngOnInit(): void {
    const today = new Date();
    this.selectedDate = today.toISOString().split('T')[0];
    this.loadSessions();
  }

  loadSessions(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.studySessionService.getSessionsByDate(this.selectedDate).subscribe({
      next: (data) => {
        this.sessions = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load sessions', err);
        this.errorMessage = 'Could not load sessions from backend.';
        this.isLoading = false;
      }
    });
  }

  generateSchedule(): void {
    if (this.availableHours <= 0) {
      this.errorMessage = 'Please enter a valid study duration greater than 0 hours.';
      return;
    }
    if (!this.selectedDate) {
      this.errorMessage = 'Please select a date for scheduling.';
      return;
    }

    this.isGenerating = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.studySessionService.generateSchedule({
      date: this.selectedDate,
      availableHours: this.availableHours
    }).subscribe({
      next: (generated) => {
        this.isGenerating = false;
        this.sessions = generated;
        if (generated.length === 0) {
          this.errorMessage = 'No topics could be scheduled for this date. Check that topics are incomplete and exam dates have not passed.';
        } else {
          const totalPlannedMinutes = generated.reduce((acc, s) => acc + s.plannedMinutes, 0);
          this.successMessage = `Schedule generated! ${generated.length} sessions scheduled totaling ${totalPlannedMinutes} minutes.`;
        }
      },
      error: (err) => {
        this.isGenerating = false;
        this.errorMessage = err.error?.message || 'Failed to generate schedule.';
      }
    });
  }

  updateStatus(session: StudySession, newStatus: SessionStatus): void {
    if (!session.id) return;

    const actual = newStatus === 'COMPLETED' ? (session.actualMinutes > 0 ? session.actualMinutes : session.plannedMinutes) : 0;

    this.studySessionService.updateSessionStatus(session.id, {
      status: newStatus,
      actualMinutes: actual
    }).subscribe({
      next: (updated) => {
        session.status = updated.status;
        session.actualMinutes = updated.actualMinutes;
      },
      error: (err) => {
        this.errorMessage = 'Failed to update session status.';
      }
    });
  }

  saveActualMinutes(session: StudySession, newMinutes: number): void {
    if (!session.id) return;
    this.studySessionService.updateSessionStatus(session.id, {
      status: session.status,
      actualMinutes: Number(newMinutes)
    }).subscribe({
      next: (updated) => {
        session.actualMinutes = updated.actualMinutes;
        this.successMessage = `Updated actual study time to ${updated.actualMinutes} mins.`;
      },
      error: (err) => {
        this.errorMessage = 'Failed to update actual minutes.';
      }
    });
  }

  deleteSession(sessionId?: number): void {
    if (!sessionId) return;
    if (confirm('Delete this study session?')) {
      this.studySessionService.deleteSession(sessionId).subscribe({
        next: () => {
          this.sessions = this.sessions.filter(s => s.id !== sessionId);
          this.successMessage = 'Study session removed.';
        },
        error: (err) => {
          this.errorMessage = 'Failed to delete session.';
        }
      });
    }
  }

  getTotalPlannedMinutes(): number {
    return this.sessions.reduce((acc, s) => acc + s.plannedMinutes, 0);
  }

  getTotalActualMinutes(): number {
    return this.sessions.reduce((acc, s) => acc + s.actualMinutes, 0);
  }
}
