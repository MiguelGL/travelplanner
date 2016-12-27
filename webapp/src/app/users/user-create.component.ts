import { Component } from '@angular/core';
import { User } from '../shared/travelplanner-api-client/user';
import { Message } from 'primeng/components/common/api';
import { Response } from '@angular/http';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';
import { Router } from '@angular/router';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';

@Component({
  templateUrl: './user-create.component.html'
})
export class UserCreateComponent {

  user: User = {
    id: 0,
    updated: new Date(0),
    email: '',
    role: 'REGULAR_USER',
    firstName: '',
    lastName: '',
    password: ''
  };

  errorMessages: Message[] = [];

  constructor(private apiClient: TravelplannerApiClientService,
              private router: Router,
              private messagesService: GlobalMessagesService) {}

  createUser(user: User) {
    this.apiClient.createUser(user.email, user.role, user.password, user.firstName, user.lastName)
      .subscribe((user) => {
        this.messagesService.display({
          severity: 'success', summary: 'User Creation OK',
          detail: 'User Creation successful'
        });
        this.router.navigateByUrl('/users');
      }, (error) => {
        if (error instanceof Response) {
          switch (error.status) {
            case 400:
              this.errorMessages = [{
                severity:'warn', summary:'User Creation Error',
                detail:'Could not create user, some data is invalid'
              }];
              break;
            case 409:
              this.errorMessages = [{
                severity:'warn', summary:'User Creation Error',
                detail:`Could not create user, the email address ${user.email} is already in use`
              }];
              break;
            default:
              this.errorMessages = [{
                severity:'error', summary:`User Creation Error (${error.status})`,
                detail:'Could not create user, please review entered data'
              }];
          }
        } else {
          this.errorMessages = [{
            severity:'error', summary:'User Creation Error',
            detail:'Could create user, an unexpected error happened'
          }]
        }
      });
  }

}
