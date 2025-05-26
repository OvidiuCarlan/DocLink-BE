import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { SignUpData } from '../../shared/models/signup-model';
import { Router } from '@angular/router';
import { TokenManagerService } from './token-manager.service';



@Injectable({
  providedIn: 'root'
})
export class AuthService {

  readonly url = '/api/users';

  constructor(private http: HttpClient, private router: Router, private tokenManager: TokenManagerService) {}

  signUp(data: SignUpData): Observable<any>{
    return this.http.post(this.url, data);
  }

  login(loginItem: any): Observable<any> {
    return this.http.post<any>(`${this.url}/tokens`, loginItem, {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    }).pipe(
      tap(response => {
        if (response && response.accessToken) {
          this.tokenManager.storeToken(response.accessToken);
          alert("User Logged In");
        } else {
          alert("Invalid response from the server");
        }
      }),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          alert('Invalid credentials');
        } else {
          alert(error.message || 'An unknown error occurred');
        }
        return throwError(() => error);
      })
    );
  }
}
