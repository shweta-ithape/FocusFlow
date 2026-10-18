import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ScheduleRequest, SessionStatusUpdateRequest, StudySession } from '../models/study-session.model';

@Injectable({
  providedIn: 'root'
})
export class StudySessionService {
  private readonly apiUrl = 'http://localhost:8080/api/schedule';

  constructor(private http: HttpClient) {}

  generateSchedule(request: ScheduleRequest): Observable<StudySession[]> {
    return this.http.post<StudySession[]>(`${this.apiUrl}/generate`, request);
  }

  getSessionsByDate(date?: string): Observable<StudySession[]> {
    let params = new HttpParams();
    if (date) {
      params = params.set('date', date);
    }
    return this.http.get<StudySession[]>(this.apiUrl, { params });
  }

  updateSessionStatus(sessionId: number, request: SessionStatusUpdateRequest): Observable<StudySession> {
    return this.http.patch<StudySession>(`${this.apiUrl}/sessions/${sessionId}/status`, request);
  }

  deleteSession(sessionId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/sessions/${sessionId}`);
  }
}
