import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Notification {
  id: number;
  message: string;
  createdAt: string;
  isRead: boolean;
  appointmentId: string;
}

export interface NotificationsResponse {
  notifications: Notification[];
  unreadCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly url = '/api/notifications';

  constructor(private http: HttpClient) { }

  getUserNotifications(userId: string): Observable<NotificationsResponse> {
    return this.http.get<NotificationsResponse>(`${this.url}/user/${userId}`);
  }

  markNotificationAsRead(notificationId: number): Observable<void> {
    return this.http.put<void>(`${this.url}/${notificationId}/read`, {});
  }
}