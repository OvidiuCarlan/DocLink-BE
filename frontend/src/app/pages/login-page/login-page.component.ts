import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { TokenManagerService } from '../../services/token-manager.service';



@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.scss'
})
export class LoginPageComponent {
  loginForm: FormGroup;
  claims: any;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router, private tokenManager: TokenManagerService){
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  onSubmit(){
    if(this.loginForm.valid){
      const formData = this.loginForm.value;

      this.authService.login(this.loginForm.value).subscribe({
        next: (res) =>{
          console.log('Login successful!', res);
          this.claims = this.tokenManager.getClaims();

          if (!this.claims && !this.claims.roles) {
            console.error('No claims or roles found in token', this.claims);
            return;
          }

          if (this.claims?.roles?.includes('USER')){
            this.router.navigate(['/user-landing']);
          }
          else if (this.claims?.roles?.includes('DOC')){
            this.router.navigate(['/doc-landing']);
          }
          else if (this.claims?.roles?.includes('ADMIN')){
            this.router.navigate(['/admin-landing']);
          }
          else
          {
            console.error('Incorrect user role:', this.claims.roles);
          }
        },
        error: (err) => {
          console.error('Login failed:', err);
        }
      });
    }

  }

  goToSignup(): void {
    this.router.navigate(['/signup']);
  }
}
