import { Component } from '@angular/core';
import { Trip } from '../shared/travelplanner-api-client/trip';
import { LazyLoadEvent } from 'primeng/components/common/api';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';

@Component({
  templateUrl: './my-trips.component.html',
  styleUrls: [ './my-trips.component.scss' ]
})
export class MyTripsComponent {

  trips: Trip[] = [];

  totalTripsCnt: number = 0;

  constructor(private apiClient: TravelplannerApiClientService,
              private messagesService: GlobalMessagesService) {}

  loadTripsLazy(event: LazyLoadEvent) {
    console.log('event', event);
    this.totalTripsCnt = 0;
    this.trips = [];
    this.apiClient.loadCurrentUserTrips(event.first, event.rows, event.sortField, event.sortOrder)
      .subscribe(trips => {
        this.trips = trips.trips;
        this.totalTripsCnt = trips.total;
      }, error => {
        this.messagesService.display({
          severity: 'error', summary: 'Error loading trips',
          detail: 'Could not load trips'
        })
      });
  }
}
