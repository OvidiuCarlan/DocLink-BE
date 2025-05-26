import { Injectable } from '@angular/core';
import { json } from 'stream/consumers';
import { jwtDecode } from 'jwt-decode';


@Injectable({
  providedIn: 'root'
})
export class TokenManagerService {

  constructor() { }

  storeToken(token: string): void {
    try {
      const decoded: any = jwtDecode(token);
      localStorage.setItem('claims', JSON.stringify(decoded));
      localStorage.setItem('token', token)
    } catch (error) {
      console.error('Error decoding token:', error);
    }
  }

  getClaims(): any {
    if (typeof window !== 'undefined') {
      const claims = localStorage.getItem('claims');
      return claims ? JSON.parse(claims) : null;
    }
    return null;
  }
}
