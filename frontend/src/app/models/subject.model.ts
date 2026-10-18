export type Importance = 'LOW' | 'MEDIUM' | 'HIGH';

export interface Subject {
  id?: number;
  name: string;
  examDate: string; // ISO date string (YYYY-MM-DD)
  importance: Importance;
}
