import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { SubjectsComponent } from './components/subjects/subjects.component';
import { TopicsComponent } from './components/topics/topics.component';
import { ScheduleComponent } from './components/schedule/schedule.component';
import { PomodoroComponent } from './components/pomodoro/pomodoro.component';
import { ProgressComponent } from './components/progress/progress.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'subjects', component: SubjectsComponent },
  { path: 'topics', component: TopicsComponent },
  { path: 'schedule', component: ScheduleComponent },
  { path: 'pomodoro', component: PomodoroComponent },
  { path: 'progress', component: ProgressComponent },
  { path: '**', redirectTo: 'dashboard' }
];
