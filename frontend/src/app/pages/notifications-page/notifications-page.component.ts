import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { NotificationService, NotificationsResponse, Notification } from '../../notification.service';
import { TokenManagerService } from '../../services/token-manager.service';

@Component({
  selector: 'app-notifications-page',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './notifications-page.component.html',
  styleUrl: './notifications-page.component.scss'
})
export class NotificationsPageComponent implements OnInit {
  notifications: Notification[] = [];
  unreadCount: number = 0;
  loading = true;
  filter: 'all' | 'unread' = 'all';

  constructor(
    private notificationService: NotificationService,
    private tokenManager: TokenManagerService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    const userId = this.tokenManager.getClaims()?.userId;
    if (userId) {
      this.notificationService.getUserNotifications(userId.toString()).subscribe({
        next: (response: NotificationsResponse) => {
          this.notifications = response.notifications;
          this.unreadCount = response.unreadCount;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading notifications:', error);
          this.loading = false;
        }
      });
    }
  }

  get filteredNotifications(): Notification[] {
    if (this.filter === 'unread') {
      return this.notifications.filter(n => !n.isRead);
    }
    return this.notifications;
  }

  markAsRead(notification: Notification): void {
    if (!notification.isRead) {
      this.notificationService.markNotificationAsRead(notification.id).subscribe({
        next: () => {
          notification.isRead = true;
          this.unreadCount = Math.max(0, this.unreadCount - 1);
        },
        error: (error) => {
          console.error('Error marking notification as read:', error);
        }
      });
    }
  }

  markAllAsRead(): void {
    const unreadNotifications = this.notifications.filter(n => !n.isRead);
    
    unreadNotifications.forEach(notification => {
      this.notificationService.markNotificationAsRead(notification.id).subscribe({
        next: () => {
          notification.isRead = true;
        }
      });
    });
    
    this.unreadCount = 0;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffInHours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60));

    if (diffInHours < 24) {
      return date.toLocaleTimeString('en-US', { 
        hour: 'numeric', 
        minute: '2-digit',
        hour12: true 
      });
    } else if (diffInHours < 168) { // Less than a week
      return date.toLocaleDateString('en-US', { 
        weekday: 'short',
        hour: 'numeric',
        minute: '2-digit',
        hour12: true
      });
    } else {
      return date.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
      });
    }
  }
}