import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { Message } from 'primeng/components/common/api';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Response } from '@angular/http';
@Component({
  templateUrl: './edit-trip.component.html'
})
export class EditTripComponent implements OnInit {

  private id: number;

  tripForm: FormGroup;

  errorMessages: Message[] = [];

  constructor(private formBuilder: FormBuilder,
              private apiClient: TravelplannerApiClientService,
              private router: Router,
              private currentRoute: ActivatedRoute,
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
    this.currentRoute.params
      .map(params => {
        this.id = parseInt(params['id'], 10);
        return this.id;
      })
      .flatMap(id => this.apiClient.loadCurrentUserTrip(id))
      .subscribe(trip => {
        this.tripForm.get('startDate').setValue(trip.startDate);
        this.tripForm.get('endDate').setValue(trip.endDate);
        this.tripForm.get('destination').setValue(trip.destination);
        this.tripForm.get('comment').setValue(trip.comment);
      });
  }

  submit(value: any, valid: boolean, data: any) {
    if (!valid) return;
    this.apiClient.editCurrentUserTrip(this.id, value.startDate, value.endDate, value.destination, value.comment)
      .subscribe(trip => {
        this.messagesService.display({
          severity: 'success', summary: 'Trip Edited',
          detail: 'The trip was edited'
        });
        this.router.navigateByUrl('/my-trips');
      }, error => {
        if (error instanceof Response) {
          switch (error.status) {
            case 400:
              this.errorMessages = [{
                severity:'warn', summary:'Invalid Trip Data',
                detail:'Could not edit trip, some data is invalid'
              }];
              break;
            case 409:
              this.errorMessages = [{
                severity:'warn', summary:'Conflicting Trip Data',
                detail:'Cannot edit overlapping trips'
              }];
              break;
            default:
              this.errorMessages = [{
                severity:'error', summary:`Trip Edition Error (${error.status})`,
                detail:'Could not edit trip, please review entered data'
              }];
          }
        } else {
          this.errorMessages = [{
            severity:'error', summary:'Trip Edition Error',
            detail:'Could edit trip, an unexpected error happened'
          }]
        }
      });
  }

}
