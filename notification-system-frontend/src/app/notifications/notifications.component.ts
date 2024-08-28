import { Component, OnInit } from '@angular/core';
import { WebSocketService } from '../websocket.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class NotificationsComponent implements OnInit {
  notifications: string[] = [];

  constructor(private webSocketService: WebSocketService) {}

  ngOnInit(): void {
    this.webSocketService.activate();  // This will now use the JWT token
    this.webSocketService.getMessages().subscribe((message) => {
      this.notifications.push(message);
    });
  }
}
