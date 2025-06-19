import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { TokenManagerService } from '../../services/token-manager.service';
import { AuthService } from '../../services/auth.service';
import { AppointmentService } from '../../services/appointment.service';
import { GetAppointmentsResponse, AppointmentData } from '../../../shared/models/appointment-model';
import { Router } from '@angular/router';

interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
}

@Component({
  selector: 'app-user-profile-page',
  standalone: true,
  imports: [NavbarComponent, CommonModule],
  templateUrl: './user-profile-page.component.html',
  styleUrl: './user-profile-page.component.scss'
})
export class UserProfilePageComponent implements OnInit {
  user: UserProfile | null = null;
  appointments: AppointmentData[] = [];
  loading = true;
  error: string | null = null;
  isDeleting = false;

  constructor(
    private tokenManager: TokenManagerService,
    private authService: AuthService,
    private appointmentService: AppointmentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
    this.loadUserAppointments();
  }

  onLogout(): void {
    const confirmMessage = `Are you sure you want to log out?`;

    if (confirm(confirmMessage)){
      localStorage.clear();
      sessionStorage.clear();

      this.router.navigate(['/login']);
    }    
  }

  private loadUserProfile(): void {
    const claims = this.tokenManager.getClaims();
    if (claims && claims.userId) {
      this.authService.getUserById(claims.userId).subscribe({
        next: (user) => {
          this.user = user;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error loading user profile:', err);
          this.error = 'Failed to load user profile';
          this.loading = false;
        }
      });
    } else {
      this.error = 'User not authenticated';
      this.loading = false;
    }
  }

  private loadUserAppointments(): void {
    const claims = this.tokenManager.getClaims();
    if (claims && claims.userId) {
      this.appointmentService.getUserAppointments(claims.userId.toString()).subscribe({
        next: (response: GetAppointmentsResponse) => {
          this.appointments = response.appointments;
        },
        error: (err) => {
          console.error('Error loading appointments:', err);
        }
      });
    }
  }

  onDeleteAccount(): void {
    if (!this.user) return;

    const confirmMessage = `Are you sure you want to delete your account?\n\nThis will permanently delete:\n- Your profile information\n- All your posts\n- All your appointments\n- All your notifications\n\nThis action cannot be undone and is required by GDPR regulations.`;
    
    if (confirm(confirmMessage)) {
      this.isDeleting = true;
      
      this.authService.deleteUserAccount(this.user.id).subscribe({
        next: () => {
          alert('Your account has been successfully deleted. You will now be logged out.');
          localStorage.clear();
          sessionStorage.clear();
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error('Error deleting account:', err);
          alert('Failed to delete account. Please try again or contact support.');
          this.isDeleting = false;
        }
      });
    }
  }

  getRoleDisplayName(role: string): string {
    switch (role) {
      case 'DOC':
        return 'Doctor';
      case 'USER':
        return 'Patient';
      case 'ADMIN':
        return 'Administrator';
      default:
        return role;
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}