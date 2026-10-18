import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProgressService } from '../../services/progress.service';
import { WeeklyProgress } from '../../models/progress.model';

@Component({
  selector: 'app-progress',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './progress.component.html',
  styleUrls: ['./progress.component.css']
})
export class ProgressComponent implements OnInit {
  weeklyData: WeeklyProgress = {
    totalCompletedMinutes: 0,
    totalCompletedSessions: 0,
    progressPercentage: 0,
    subjectSummaries: []
  };

  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(private progressService: ProgressService) {}

  ngOnInit(): void {
    this.loadWeeklyProgress();
  }

  loadWeeklyProgress(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.progressService.getWeeklyProgress().subscribe({
      next: (data) => {
        this.weeklyData = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load weekly progress', err);
        this.errorMessage = 'Could not load weekly analytics from backend.';
        this.isLoading = false;
      }
    });
  }

  getMaxSubjectMinutes(): number {
    if (!this.weeklyData.subjectSummaries || this.weeklyData.subjectSummaries.length === 0) {
      return 1;
    }
    return Math.max(...this.weeklyData.subjectSummaries.map(s => s.completedMinutes), 1);
  }

  getSubjectBarPercentage(minutes: number): number {
    const max = this.getMaxSubjectMinutes();
    return Math.round((minutes / max) * 100);
  }
}
