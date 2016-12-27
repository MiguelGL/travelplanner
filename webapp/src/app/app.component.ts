import { Component, OnInit } from '@angular/core';
import { GlobalMessagesService } from './shared/global-messages-service/global-messages.service';
import { TravelplannerApiClientService } from './shared/travelplanner-api-client/travelplanner-api-client.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

  private startDate: Date;

  constructor(private messagesService: GlobalMessagesService,
              private apiClient: TravelplannerApiClientService,
              private router: Router) {}

  ngOnInit() {
    this.startDate = new Date();
  }

  get year() {
    return this.startDate.getFullYear();
  }

  get month() {
    return this.startDate.getMonth() + 1;
  }

  get tripPlanLink() {
    const year = this.month >= 12 ? this.year + 1 : this.year;
    const month = this.month >= 11 ? 1 : this.month + 1;
    return `/my-trips/${year}/${month}`;
  }

  get messages() {
    return this.messagesService.messages;
  }

  get isLoggedIn() {
    return this.apiClient.isLoggedIn;
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
          detail: 'Could not log you out, redirecting to login anyway'
        });
        this.router.navigateByUrl('/login');
      });
  }

}
