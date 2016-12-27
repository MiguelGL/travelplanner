import { Component, OnInit } from '@angular/core';
import { User } from '../shared/travelplanner-api-client/user';
import { Message } from 'primeng/components/common/api';
import { Response } from '@angular/http';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';
import { Router, ActivatedRoute } from '@angular/router';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { Observable } from 'rxjs';

@Component({
  templateUrl: './user-edit.component.html'
})
export class UserEditComponent implements OnInit {

  user: Observable<User>;

  errorMessages: Message[] = [];

  constructor(private apiClient: TravelplannerApiClientService,
              private router: Router,
              private currentRoute: ActivatedRoute,
              private messagesService: GlobalMessagesService) {}

  ngOnInit() {
    this.user = this.currentRoute.params
      .map(params => parseInt(params['id']))
      .flatMap(id => this.apiClient.loadUser(id));
  }

  editUser(user: User) {
    this.apiClient.editUser(user.id, user.email, user.role, user.password, user.firstName, user.lastName)
      .subscribe((user) => {
        this.messagesService.display({
          severity: 'success', summary: 'User Edit OK',
          detail: 'User Edition successful'
        });
        this.router.navigateByUrl('/users');
      }, (error) => {
        if (error instanceof Response) {
          switch (error.status) {
            case 400:
              this.errorMessages = [{
                severity:'warn', summary:'User Edition Error',
                detail:'Could not edit user, some data is invalid'
              }];
              break;
            case 409:
              this.errorMessages = [{
                severity:'warn', summary:'User Edition Error',
                detail:`Could not edit user, the email address ${user.email} is already in use`
              }];
              break;
            default:
              this.errorMessages = [{
                severity:'error', summary:`User Edition Error (${error.status})`,
                detail:'Could not edit user, please review entered data'
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
