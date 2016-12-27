import { Component, OnInit } from '@angular/core';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { ActivatedRoute } from '@angular/router';
import { LazyLoadEvent } from 'primeng/components/common/api';
import { Trip } from '../shared/travelplanner-api-client/trip';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';

@Component({
  templateUrl: './trips-plan.component.html'
})
export class TripsPlanComponent implements OnInit {

  trips: Trip[] = [];

  totalTripsCnt: number = 0;

  private year: number;
  private month: number;

  constructor(private apiClient: TravelplannerApiClientService,
              private currentRoute: ActivatedRoute,
              private messagesService: GlobalMessagesService) {}

  ngOnInit() {
    this.currentRoute.params.subscribe(params => {
        this.year = parseInt(params['year']);
        this.month = parseInt(params['month']);
    });
  }

  loadMonthTripsLazy(event: LazyLoadEvent) {
    this.apiClient.loadMonthUserTrips(event.first, event.rows, this.year, this.month - 1)
      .subscribe(trips => {
        this.trips = trips.trips;
        this.totalTripsCnt = trips.total;
      }, error => {
        this.messagesService.display({
          severity: 'error', summary: 'Error loading month trips',
          detail: 'Could not load trips for month'
        })
      });
  }

}
