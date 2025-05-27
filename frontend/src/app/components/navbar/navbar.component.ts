import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationDropdownComponent } from '../notification-dropdown/notification-dropdown/notification-dropdown.component'; 


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [NotificationDropdownComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})


export class NavbarComponent {
  logoPath = 'assets/images/logo.png';
  
  constructor(private router: Router) {}

  
  navigateToCreatePost() {
    this.router.navigate(['/create-post']);
  }
  navigateToLandingPage(){
    this.router.navigate(['/doc-landing']);
  }
  navigateToProfilePage(){
    this.router.navigate(['/doc-landing']);
  }
}
