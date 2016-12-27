import { Component, OnInit } from '@angular/core';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { User } from '../shared/travelplanner-api-client/user';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';

@Component({
  templateUrl: './users-list.component.html',
  styleUrls: [ './users-list.component.scss' ]
})
export class UsersListComponent implements OnInit {

  private users: User[] = [];

  selectedUser: User;

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

  deleteUser(user: User) {
    const confirmed = window.confirm(`Are you sure you want to delete user ${user.email}?`)
    if (!confirmed) {
      return;
    }

    this.apiClient.deleteUser(user.id)
      .subscribe(() => {
        const idx = this.users.indexOf(user);
        if (idx >= 0) {
          this.users.splice(idx, 1);
          this.messagesService.display({
            severity: 'success', summary: 'User Deleted',
            detail: 'User has been deleted'
          });
        }
      }, error => {
        this.messagesService.display({
          severity: 'error', summary: 'Error Deleting User',
          detail: 'Error deleting user'
        })
      });
  }

  editUser(user: User) {
    console.log('edit user');
  }

}
