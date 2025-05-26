import { bootstrapApplication } from '@angular/platform-browser';
import { importProvidersFrom } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';

bootstrapApplication(AppComponent, {
  ...appConfig, 
  providers: [
    ...appConfig.providers,
    importProvidersFrom(ReactiveFormsModule)
  ]
})
  .catch((err) => console.error(err));
