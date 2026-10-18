export interface DashboardStats {
  plannedMinutesToday: number;
  totalSessionsToday: number;
  completedSessionsToday: number;
  pendingSessionsToday: number;
  overallProgressPercentage: number;
}

export interface SubjectProgress {
  subjectName: string;
  completedMinutes: number;
  completedSessions: number;
}

export interface WeeklyProgress {
  totalCompletedMinutes: number;
  totalCompletedSessions: number;
  progressPercentage: number;
  subjectSummaries: SubjectProgress[];
}
