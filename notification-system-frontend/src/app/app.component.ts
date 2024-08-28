import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: true,
  imports: [
    RouterModule, // Just import RouterModule, without .forRoot or .forChild
    HttpClientModule,
  ],
})
export class AppComponent {
  title = 'notification-system-client';
}
