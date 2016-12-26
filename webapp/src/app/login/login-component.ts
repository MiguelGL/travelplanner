import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { Router } from '@angular/router';
import { Message } from 'primeng/components/common/api';
import { Response } from '@angular/http';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';

@Component({
  templateUrl: './login-component.html'
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;

  errorMessages: Message[] = [];

  constructor(private formBuilder: FormBuilder,
              private apiClient: TravelplannerApiClientService,
              private router: Router,
              private messagesService: GlobalMessagesService) {}

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      email: new FormControl('', Validators.compose([
        Validators.required
      ])),
      password: new FormControl('', Validators.compose([
        Validators.required
      ]))
    });
  }

  submit(value: any, valid: boolean, data: any) {
    if (!valid) return;
    this.apiClient.login(value.email, value.password)
      .subscribe((user) => {
        this.messagesService.display({
          severity: 'success', summary: 'Registration OK',
          detail: 'Registration successful'
        })
        this.router.navigateByUrl('/my-trips');
      }, (error) => {
        if (error instanceof Response) {
          switch (error.status) {
            case 401:
              this.errorMessages = [{
                severity:'warn', summary:'Invalid Credentials',
                detail:'Could not log you in, please review entered credentials'
              }];
              break;
            default:
              this.errorMessages = [{
                severity:'error', summary:`Login Error (${error.status})`,
                detail:'Could not log you in, please review entered credentials'
              }];
          }
        } else {
          this.errorMessages = [{
            severity:'error', summary:'Login Error',
            detail:'Could not log you in, an unexpected error happened'
          }];
        }
      });
  }
}
