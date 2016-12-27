import { Component, ChangeDetectorRef } from '@angular/core';
import { Trip } from '../shared/travelplanner-api-client/trip';
import { LazyLoadEvent } from 'primeng/components/common/api';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';
import { Response } from '@angular/http';

@Component({
  templateUrl: './my-trips.component.html'
})
export class MyTripsComponent {

  trips: Trip[] = [];

  totalTripsCnt: number = 0;

  constructor(private apiClient: TravelplannerApiClientService,
              private messagesService: GlobalMessagesService /*,
              private cdr: ChangeDetectorRef */) {}

  loadTripsLazy(event: LazyLoadEvent) {
    // These two sentences create a nasty flickering effect, but needed as a workaround for
    // apparently several issues with PrimeNG lazy datatables.
    //   https://github.com/primefaces/primeng/issues?utf8=%E2%9C%93&q=is%3Aissue%20is%3Aopen%20lazy%20sort
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

  deleteTrip(trip: Trip) {
    const confirmed = window.confirm("Are you sure you want to delete this trip?")
    if (!confirmed) {
      return;
    }

    // These sentences create a nasty flickering effect, but needed as a workaround for
    // apparently several issues with PrimeNG lazy datatables.
    //   https://github.com/primefaces/primeng/issues?utf8=%E2%9C%93&q=is%3Aissue%20is%3Aopen%20lazy%20sort
    const savedTrips = this.trips;
    let savedTotalTripsCnt = this.totalTripsCnt;
    this.totalTripsCnt = 0;
    this.trips = [];

    // console.log('delete', trip);
    this.apiClient.deleteTrip(trip.id)
      .subscribe(() => {
        const idx = savedTrips.indexOf(trip);
        // console.log('idx', idx);
        if (idx >= 0) {
          savedTrips.splice(idx, 1);
          savedTotalTripsCnt = savedTotalTripsCnt - 1;
        }
        this.trips = savedTrips;
        this.totalTripsCnt = savedTotalTripsCnt;

        // console.log('trips', this.trips);
        // this.cdr.detectChanges();
        this.messagesService.display({
          severity: 'success', summary: 'Trip deleted',
          detail: 'The trip has been deleted'
        });
      }, error => {
        if (error instanceof Response) {
          this.messagesService.display({
            severity: 'error', summary: `Error (${error.status}) deleting trip`,
            detail: 'Could not delete trip'
          });
        } else {
          this.messagesService.display({
            severity: 'error', summary: 'Error deleting trip',
            detail: 'Could not delete trip'
          });
        }
      });
  }

}
