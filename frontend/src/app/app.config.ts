import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { HttpClientModule, provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes), 
    provideClientHydration(withEventReplay()),
    provideHttpClient(withFetch(),
      withInterceptors([
        (req, next) => {
          const token = localStorage.getItem('token');
          if (token) {
            req = req.clone({
              setHeaders: {
                Authorization: `Bearer ${token}`
              }
            });
          }
          return next(req);
        }
      ]))
  ]
};
