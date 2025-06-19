import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationDropdownComponent } from '../notification-dropdown/notification-dropdown/notification-dropdown.component'; 
import { TokenManagerService } from '../../services/token-manager.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [NotificationDropdownComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})


export class NavbarComponent {
  logoPath = 'assets/images/logo.png';
  isDoctor = false;
  
  constructor(
    private router: Router,
    private tokenManager: TokenManagerService
  ) {}

  ngOnInit(): void {
    this.checkUserRole();
  }

  private checkUserRole(): void {
    const claims = this.tokenManager.getClaims();
    if (claims && claims.roles) {
      this.isDoctor = claims.roles.includes('DOC');
    }
  }
  
  navigateToCreatePost() {
    this.router.navigate(['/create-post']);
  }
  navigateToLandingPage(){
    this.router.navigate(['/doc-landing']);
  }
  navigateToProfilePage(){
    this.router.navigate(['/profile']);
  }
}
