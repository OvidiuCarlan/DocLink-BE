import { Routes } from '@angular/router';
import { SignupPageComponent } from './pages/signup-page/signup-page.component';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { DocLandingPageComponent } from './pages/doc-landing-page/doc-landing-page.component';
import { UserLandingPageComponent } from './pages/user-landing-page/user-landing-page.component';
import { CreatePostPageComponent } from './pages/create-post-page/create-post-page.component';
import { UserAppointmentsComponent } from './pages/user-appointments/user-appointments.component';
import { UserProfilePageComponent } from './pages/user-profile-page/user-profile-page.component';
import { NotificationsPageComponent } from './pages/notifications-page/notifications-page.component';
import { RoleGuard } from './guards/role.guard';


export const routes: Routes = [
    {path: '', redirectTo: 'login', pathMatch: 'full'},
    {path: 'signup', component: SignupPageComponent},
    {path: 'login', component: LoginPageComponent},

    {path: 'doc-landing', component: DocLandingPageComponent, canActivate: [RoleGuard], data: {roles: ['DOC']}},
    {path: 'user-landing', component: UserLandingPageComponent, canActivate: [RoleGuard], data: {roles: ['USER']}},
    
    //Doctors
    {path: 'create-post', component: CreatePostPageComponent, canActivate: [RoleGuard], data: {roles: ['DOC']}},
    
    //Patients  
    {path: 'user-app', component: UserAppointmentsComponent, canActivate: [RoleGuard], data: {roles: ['USER']}},
    
    //Both
    {path: 'profile', component: UserProfilePageComponent, canActivate: [RoleGuard], data: {roles: ['USER', 'DOC']}},
    {path: 'notifications', component: NotificationsPageComponent, canActivate: [RoleGuard], data: {roles: ['USER', 'DOC']}},
];
