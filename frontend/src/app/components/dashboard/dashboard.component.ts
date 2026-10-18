import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { StudySessionService } from '../../services/study-session.service';
import { ProgressService } from '../../services/progress.service';
import { DashboardStats } from '../../models/progress.model';
import { StudySession } from '../../models/study-session.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = {
    plannedMinutesToday: 0,
    totalSessionsToday: 0,
    completedSessionsToday: 0,
    pendingSessionsToday: 0,
    overallProgressPercentage: 0
  };

  todaySessions: StudySession[] = [];
  todayDateStr: string = '';
  availableHours: number = 3.0;

  isLoading: boolean = false;
  isGenerating: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private studySessionService: StudySessionService,
    private progressService: ProgressService
  ) {}

  ngOnInit(): void {
    const today = new Date();
    this.todayDateStr = today.toISOString().split('T')[0];
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Fetch KPI stats
    this.progressService.getDashboardStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching dashboard stats:', err);
        this.errorMessage = 'Could not connect to backend server. Please verify MySQL and Spring Boot are running.';
        this.isLoading = false;
      }
    });

    // Fetch Today's Study Sessions
    this.loadTodaySessions();
  }

  loadTodaySessions(): void {
    this.studySessionService.getSessionsByDate(this.todayDateStr).subscribe({
      next: (sessions) => {
        this.todaySessions = sessions;
      },
      error: (err) => {
        console.error('Error fetching sessions:', err);
      }
    });
  }

  generateSchedule(): void {
    if (this.availableHours <= 0) {
      this.errorMessage = 'Please enter a valid study duration greater than 0 hours.';
      return;
    }

    this.isGenerating = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.studySessionService.generateSchedule({
      date: this.todayDateStr,
      availableHours: this.availableHours
    }).subscribe({
      next: (sessions) => {
        this.isGenerating = false;
        if (sessions.length === 0) {
          this.successMessage = 'No incomplete topics available to schedule for today.';
        } else {
          this.successMessage = `Successfully generated ${sessions.length} study session(s) using greedy algorithm!`;
        }
        this.loadDashboardData();
      },
      error: (err) => {
        this.isGenerating = false;
        this.errorMessage = err.error?.message || 'Failed to generate schedule. Ensure topics exist and have valid exam dates.';
      }
    });
  }

  markSessionCompleted(session: StudySession): void {
    if (!session.id) return;
    this.studySessionService.updateSessionStatus(session.id, {
      status: 'COMPLETED',
      actualMinutes: session.plannedMinutes
    }).subscribe({
      next: () => {
        this.loadDashboardData();
      },
      error: (err) => {
        this.errorMessage = 'Failed to update session status.';
      }
    });
  }

  markSessionIncomplete(session: StudySession): void {
    if (!session.id) return;
    this.studySessionService.updateSessionStatus(session.id, {
      status: 'INCOMPLETE',
      actualMinutes: 0
    }).subscribe({
      next: () => {
        this.loadDashboardData();
      },
      error: (err) => {
        this.errorMessage = 'Failed to update session status.';
      }
    });
  }
}
