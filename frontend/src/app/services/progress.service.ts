import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardStats, WeeklyProgress } from '../models/progress.model';

@Injectable({
  providedIn: 'root'
})
export class ProgressService {
  private readonly apiUrl = 'http://localhost:8080/api/progress';

  constructor(private http: HttpClient) {}

  getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.apiUrl}/dashboard`);
  }

  getWeeklyProgress(): Observable<WeeklyProgress> {
    return this.http.get<WeeklyProgress>(`${this.apiUrl}/weekly`);
  }
}
