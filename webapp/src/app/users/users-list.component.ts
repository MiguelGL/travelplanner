import { Component, OnInit } from '@angular/core';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { User } from '../shared/travelplanner-api-client/user';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';

@Component({
  templateUrl: './users-list.component.html'
})
export class UsersListComponent implements OnInit {

  private users: User[] = [];

  constructor(private apiClient: TravelplannerApiClientService,
              private messagesService: GlobalMessagesService) {}

  ngOnInit() {
    this.apiClient.loadAllUsers()
      .subscribe(users => {
        this.users = users;
      }, error => {
        this.messagesService.display({
          severity: 'error', summary: 'Error Loading Users',
          detail: 'Error loading all users list'
        })
      });
  }

}
