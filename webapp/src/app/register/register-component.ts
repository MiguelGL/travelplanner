import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { Message } from 'primeng/components/common/api';
import { Response } from '@angular/http';

@Component({
  templateUrl: './register-component.html'
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;

  errorMessages: Message[] = [];

  constructor(private formBuilder: FormBuilder,
              private apiClient: TravelplannerApiClientService,
              private router: Router) {}

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
      email: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(64),
        Validators.pattern('^.+@.+$')
      ])),
      firstName: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(64)
      ])),
      lastName: new FormControl('', Validators.compose([
        Validators.maxLength(64)
      ])),
      password: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(48),
        Validators.pattern('^[0-9a-zA-Z\_\-]+$')
      ])),
      confirmedPassword: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(48),
        Validators.pattern('^[0-9a-zA-Z\_\-]+$')
      ])),
    }, {
      validator: (group: FormGroup) => {
        const passwordInput = group.get('password');
        const confirmPasswordInput = group.get('confirmedPassword');
        return (passwordInput.value === confirmPasswordInput.value)
          ? confirmPasswordInput.setErrors(confirmPasswordInput.validator(confirmPasswordInput))
          : confirmPasswordInput.setErrors({
             passwordsmismatch: true
        });
      }
    });
  }

  submit(value: any, valid: boolean, data: any) {
    if (!valid) return;
    this.apiClient.register(value.email, value.password, value.firstName, value.lastName)
      .subscribe((user) => {
        this.router.navigateByUrl('/login');
      }, (error) => {
        if (error instanceof Response) {
          switch (error.status) {
            case 400:
              this.errorMessages = [{
                severity:'warn', summary:'Registration Error',
                detail:'Could not register, some data is invalid'
              }];
              break;
            case 409:
              this.errorMessages = [{
                severity:'warn', summary:'Registration Error',
                detail:`Could not register, the email address ${value.email} is already in use`
              }];
              break;
            default:
              this.errorMessages = [{
                severity:'error', summary:`Registration Error (${error.status})`,
                detail:'Could not register, please review entered data'
              }];
          }
        } else {
          this.errorMessages = [{
            severity:'error', summary:'Registration Error',
            detail:'Could register, an unexpected error happened'
          }]
        }
      });
  }

}
