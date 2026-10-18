import { Subject } from './subject.model';

export type Difficulty = 'EASY' | 'MEDIUM' | 'HARD';

export interface Topic {
  id?: number;
  name: string;
  subject: Subject;
  difficulty: Difficulty;
  estimatedMinutes: number;
  completed: boolean;
}
