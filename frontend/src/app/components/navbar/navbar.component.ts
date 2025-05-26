import { Component } from '@angular/core';
import { Router } from '@angular/router';


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})


export class NavbarComponent {
  logoPath = 'assets/images/logo.png';
  
  constructor(private router: Router) {}

  // onSearch(event: any) {
  //   const query = event.target.value;
  //   console.log('Search:', query); 
  // }
  navigateToCreatePost() {
    this.router.navigate(['/create-post']);
  }
  navigateToLandingPage(){
    this.router.navigate(['/doc-landing']);
  }
}
