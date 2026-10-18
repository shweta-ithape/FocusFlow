import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Topic } from '../models/topic.model';

@Injectable({
  providedIn: 'root'
})
export class TopicService {
  private readonly apiUrl = 'http://localhost:8080/api/topics';

  constructor(private http: HttpClient) {}

  getAllTopics(subjectId?: number): Observable<Topic[]> {
    let params = new HttpParams();
    if (subjectId) {
      params = params.set('subjectId', subjectId.toString());
    }
    return this.http.get<Topic[]>(this.apiUrl, { params });
  }

  getTopicById(id: number): Observable<Topic> {
    return this.http.get<Topic>(`${this.apiUrl}/${id}`);
  }

  createTopic(topic: Topic): Observable<Topic> {
    return this.http.post<Topic>(this.apiUrl, topic);
  }

  updateTopic(id: number, topic: Topic): Observable<Topic> {
    return this.http.put<Topic>(`${this.apiUrl}/${id}`, topic);
  }

  toggleCompletion(id: number): Observable<Topic> {
    return this.http.patch<Topic>(`${this.apiUrl}/${id}/complete`, {});
  }

  deleteTopic(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
