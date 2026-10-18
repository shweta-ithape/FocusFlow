import { Topic } from './topic.model';

export type SessionStatus = 'PLANNED' | 'COMPLETED' | 'INCOMPLETE';

export interface StudySession {
  id?: number;
  topic: Topic;
  scheduledDate: string; // YYYY-MM-DD
  plannedMinutes: number;
  actualMinutes: number;
  status: SessionStatus;
}

export interface ScheduleRequest {
  date: string; // YYYY-MM-DD
  availableHours: number;
}

export interface SessionStatusUpdateRequest {
  status: SessionStatus;
  actualMinutes: number;
}
