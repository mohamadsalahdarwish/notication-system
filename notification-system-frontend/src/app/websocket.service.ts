import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client'; // Correct import for SockJS
import { Observable, Subject } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private stompClient: Client;
  private messageSubject = new Subject<string>();

  constructor(private authService: AuthService) {
    this.stompClient = new Client();
    this.stompClient.webSocketFactory = () => new SockJS('/ws');

    this.stompClient.onConnect = (frame) => {
      console.log('Connected: ' + frame);
      this.stompClient.subscribe('/user/queue/notifications', (message) => {
        this.messageSubject.next(message.body);
      });
    };

    this.stompClient.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };

    this.stompClient.connectHeaders = {
      Authorization: `Bearer ${this.authService.getToken()}`,
    };
  }

  activate() {
    this.stompClient.activate();
  }

  deactivate() {
    this.stompClient.deactivate();
  }

  getMessages(): Observable<string> {
    return this.messageSubject.asObservable();
  }
}
