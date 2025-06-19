import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { TokenManagerService } from '../services/token-manager.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private tokenManager: TokenManagerService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const claims = this.tokenManager.getClaims();
    const requiredRoles = route.data['roles'] as Array<string>;
    
    if (!claims || !requiredRoles) return true;
    
    const hasRole = requiredRoles.some(role => claims.roles?.includes(role));
    
    if (!hasRole) {
      if (claims.roles?.includes('DOC')) {
        this.router.navigate(['/doc-landing']);
      } else {
        this.router.navigate(['/user-landing']);
      }
      return false;
    }
    
    return true;
  }
}