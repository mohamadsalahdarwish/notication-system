import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { NotificationsComponent } from './notifications/notifications.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'notifications', component: NotificationsComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },  // Redirect to /login if no path is provided
  { path: '**', redirectTo: '/login', pathMatch: 'full' }  // Wildcard route for a 404 page or redirect
];
