import { Component, OnInit } from '@angular/core';
import { GlobalMessagesService } from './shared/global-messages-service/global-messages.service';
import { TravelplannerApiClientService } from './shared/travelplanner-api-client/travelplanner-api-client.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

  private startTs: Date;

  constructor(private messagesService: GlobalMessagesService,
              private apiClient: TravelplannerApiClientService,
              private router: Router) {}

  get messages() {
    return this.messagesService.messages;
  }

  ngOnInit(): void {
    this.startTs = new Date();
  }

  logout() {
    this.apiClient.logout()
      .subscribe(() => {
        this.messagesService.display({
          severity: 'success', summary: 'Logout OK',
          detail: 'You have been logged out'
        });
        this.router.navigateByUrl('/login');
      }, (error) => {
        this.messagesService.display({
          severity: 'error', summary: 'Error logging out',
          detail: 'Could not log you out'
        });
      });
  }

}
