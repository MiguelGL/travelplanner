import { Component, OnInit } from '@angular/core';
import { GlobalMessagesService } from './shared/global-messages-service/global-messages.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

  private startTs: Date;

  constructor(private messagesService: GlobalMessagesService) {}

  get messages() {
    return this.messagesService.messages;
  }

  ngOnInit(): void {
    this.startTs = new Date();
  }

}
