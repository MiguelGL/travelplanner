import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Message } from 'primeng/components/common/api';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';
import { Router } from '@angular/router';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { Response } from '@angular/http';

@Component({
  templateUrl: './create-trip.component.html'
})
export class CreateTripComponent implements OnInit {

  tripForm: FormGroup;

  errorMessages: Message[] = [];

  constructor(private formBuilder: FormBuilder,
              private apiClient: TravelplannerApiClientService,
              private router: Router,
              private messagesService: GlobalMessagesService) {}

  ngOnInit() {
    this.tripForm = this.formBuilder.group({
      startDate: new FormControl(new Date(), Validators.compose([
        Validators.required
      ])),
      endDate: new FormControl(new Date(), Validators.compose([
        Validators.required
      ])),
      destination: new FormControl('', Validators.compose([
        Validators.required,
        Validators.maxLength(64),
      ])),
      comment: new FormControl('', Validators.compose([
        Validators.maxLength(512)
      ]))
    }, {
      validator: (group: FormGroup) => {
        const startDateInput = group.get('startDate');
        const endDatePasswordInput = group.get('endDate');
        return (endDatePasswordInput.value.getTime() >= startDateInput.value.getTime())
          ? endDatePasswordInput.setErrors(endDatePasswordInput.validator(endDatePasswordInput))
          : endDatePasswordInput.setErrors({
          datesmismatch: true
        });
      }
    });
  }

  submit(value: any, valid: boolean, data: any) {
    if (!valid) return;
    this.apiClient.createCurrentUserTrip(value.startDate, value.endDate, value.destination, value.comment)
      .subscribe(trip => {
        this.messagesService.display({
          severity: 'success', summary: 'Trip Created',
          detail: 'The trip was created'
        });
        this.router.navigateByUrl('/my-trips');
      }, error => {
        if (error instanceof Response) {
          switch (error.status) {
            case 400:
              this.errorMessages = [{
                severity:'warn', summary:'Invalid Trip Data',
                detail:'Could not create trip, some data is invalid'
              }];
              break;
            case 409:
              this.errorMessages = [{
                severity:'warn', summary:'Conflicting Trip Data',
                detail:'Cannot create overlapping trips'
              }];
              break;
            default:
              this.errorMessages = [{
                severity:'error', summary:`Trip Creation Error (${error.status})`,
                detail:'Could not create trip, please review entered data'
              }];
          }
        } else {
          this.errorMessages = [{
            severity:'error', summary:'Trip Creation Error',
            detail:'Could create trip, an unexpected error happened'
          }]
        }
      });
  }

}
