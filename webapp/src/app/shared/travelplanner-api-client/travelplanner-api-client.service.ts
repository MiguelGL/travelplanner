import { Injectable } from '@angular/core';
import { Http, URLSearchParams, Headers } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map'
import { User } from './user';
import { Trip } from './trip';
import * as moment from 'moment';

@Injectable()
export class TravelplannerApiClientService {

  private loggedInUser: User;

  constructor(private http: Http) {}

  register(email: string, password: string,
           firstName: string, lastName: string = ''): Observable<User> {
    const params = new URLSearchParams();
    params.set('email', email);
    params.set('password', password);
    params.set('firstName', firstName);
    params.set('lastName', lastName);

    const headers = new Headers();
    headers.append('Content-Type', 'application/x-www-form-urlencoded');

    return this.http.post('/travelplanner/api/login/register', params.toString(), {headers})
      .map(response => {
        if (response.status === 200) {
          return response.json() as User;
        } else {
          throw response;
        }
      })
  }

  login(email: string,  password: string): Observable<User> {
    const params = new URLSearchParams();
    params.set('email', email);
    params.set('password', password);

    const headers = new Headers();
    headers.append('Content-Type', 'application/x-www-form-urlencoded')

    return this.http.post('/travelplanner/api/login', params.toString(), {headers})
      .map(response => {
        if (response.status === 200) {
          const user = response.json() as User;
          this.loggedInUser = user;
          return user;
        } else {
          throw response;
        }
      });
  }

  logout(): Observable<void> {
    return this.http.delete('/travelplanner/api/login')
      .map(response => {
        if (response.status === 204) {
          return;
        } else {
          throw response;
        }
      })
  }

  loadCurrentUserTrips(offset: number, limit: number): Observable<Trip[]> {
    const search = new URLSearchParams()
    search.set("offset", `${offset}`);
    search.set("limit", `${limit}`);

    return this.http.get(`/travelplanner/api/sec/users/${this.loggedInUser.id}/trips`, {search})
      .map(response => {
        if (response.status === 200) {
          const now = new Date();
          return response.json().map(apiTrip => {
            const startDate = new Date(apiTrip.startDate);
            const endDate = new Date(apiTrip.endDate);
            return {
              id: apiTrip.id,
              updated: moment(new Date(apiTrip.updated)),
              startDate: moment(startDate),
              endDate: moment(endDate),
              destination: apiTrip.destinationName,
              comment: apiTrip.comment || '',
              daysToStart: moment.duration(now.getMilliseconds() - startDate.getMilliseconds())
            };
          });
        } else {
          throw response;
        }
      });
  }

}
