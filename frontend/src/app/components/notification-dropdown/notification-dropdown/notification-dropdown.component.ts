import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Notification, NotificationService, NotificationsResponse } from '../../../notification.service';
import { TokenManagerService } from '../../../services/token-manager.service'; 
import { interval, Subscription } from 'rxjs';
import { RouterModule } from '@angular/router';
import { ClickOutsideDirective } from '../../../directives/click-outside.directive';

@Component({
  selector: 'app-notification-dropdown',
  standalone: true,
  imports: [CommonModule, RouterModule, ClickOutsideDirective],
  templateUrl: './notification-dropdown.component.html',
  styleUrl: './notification-dropdown.component.scss'
})
export class NotificationDropdownComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  unreadCount: number = 0;
  isDropdownOpen = false;
  private pollSubscription?: Subscription;

  constructor(
    private notificationService: NotificationService,
    private tokenManager: TokenManagerService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
    this.startPolling();
  }

  ngOnDestroy(): void {
    if (this.pollSubscription) {
      this.pollSubscription.unsubscribe();
    }
  }

  private startPolling(): void {
    // Poll for new notifications every 10 seconds
    this.pollSubscription = interval(10000).subscribe(() => {
      this.loadNotifications();
    });
  }

  loadNotifications(): void {
    const userId = this.tokenManager.getClaims()?.userId;
    if (userId) {
      this.notificationService.getUserNotifications(userId.toString()).subscribe({
        next: (response: NotificationsResponse) => {
          this.notifications = response.notifications.slice(0, 10); // Show only latest 10
          this.unreadCount = response.unreadCount;
        },
        error: (error) => {
          console.error('Error loading notifications:', error);
        }
      });
    }
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
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

  formatTimeAgo(dateString: string): string {
    const now = new Date();
    const notificationDate = new Date(dateString);
    const diffInMinutes = Math.floor((now.getTime() - notificationDate.getTime()) / (1000 * 60));

    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}h ago`;
    return `${Math.floor(diffInMinutes / 1440)}d ago`;
  }

  closeDropdown(): void {
    this.isDropdownOpen = false;
  }
}