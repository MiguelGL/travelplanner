import { Injectable } from '@angular/core';
import { Http, URLSearchParams, Headers, Response } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch';
import { User } from './user';
import { Trip } from './trip';
import * as moment from 'moment';

@Injectable()
export class TravelplannerApiClientService {

  private loggedInUser: User;

  constructor(private http: Http) {}

  get isLoggedIn() {
    return !!this.loggedInUser;
  }

  checkLoggedIn(): Observable<boolean> {
    return this.http.get('/travelplanner/api/login')
      .map(response => {
        switch (response.status) {
          case 200:
            const user = response.json() as User;
            this.loggedInUser = user;
            return true;
          case 401:
            this.loggedInUser = null;
            return false;
          default:
            throw response;
        }
      })
      .catch((err) => {
        if (err instanceof Response) {
          if (err.status === 401) {
            return Observable.of(false);
          } else {
            return Observable.throw(err);
          }
        } else {
          return Observable.throw(err);
        }
      });
  }

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
    headers.append('Content-Type', 'application/x-www-form-urlencoded');

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
          this.loggedInUser = null;
          return;
        } else {
          throw response;
        }
      })
  }

  private apiOrderBySpec(sortOrder: number) {
    if (sortOrder > 0) {
      return 'ASC';
    } else if (sortOrder == 0) {
      return 'NONE';
    } else {
      return 'DESC';
    }
  }

  private readonly TRIP_ORDER_BY_FIELDS = {
    user: 'USER',
    startDate: 'START_DATE',
    endDate: 'END_DATE',
    destination: 'DESTINATION'
  };

  loadCurrentUserTrips(offset: number, limit: number,
                       sortField = 'startDate',  sortOrder = 0): Observable<{trips: Trip[], total: number}> {
    const search = new URLSearchParams();
    search.set("offset", `${offset}`);
    search.set("limit", `${limit}`);
    search.set('orderBy', this.TRIP_ORDER_BY_FIELDS[sortField] || 'START_DATE');
    search.set('orderSpec', this.apiOrderBySpec(sortOrder));

    return this.http.get(`/travelplanner/api/sec/users/${this.loggedInUser.id}/trips`, {search})
      .map(response => {
        if (response.status === 200) {
          const now = this.truncateDateToDay(new Date());
          return {
            trips: response.json().map(apiTrip => this.apiTripToTrip(now, apiTrip)),
            total: parseInt(response.headers.get('X-Available-Records-Count'), 10)
          };
        } else {
          throw response;
        }
      });
  }

  loadMonthUserTrips(offset: number, limit: number,
                     year: number,  month: number): Observable<{trips: Trip[], total: number}> {
    const fromDate = new Date(Date.UTC(year, month));
    let toDate;
    if (month => 11) {
      toDate = new Date(Date.UTC(year + 1, 0));
    } else {
      toDate = new Date(Date.UTC(year, month + 1));
    }
    const search = new URLSearchParams();
    search.set("offset", `${offset}`);
    search.set("limit", `${limit}`);
    search.set("orderBy", 'START_DATE');
    search.set("orderSpec", 'ASC');
    search.set('fromDateTs', `${fromDate.getTime()}`);
    search.set('toDateTs', `${toDate.getTime()}`);

    return this.http.get(`/travelplanner/api/sec/users/${this.loggedInUser.id}/trips`, {search})
      .map(response => {
        if (response.status === 200) {
          const now = this.truncateDateToDay(new Date());
          return {
            trips: response.json().map(apiTrip => this.apiTripToTrip(now, apiTrip)),
            total: parseInt(response.headers.get('X-Available-Records-Count'), 10)
          };
        } else {
          throw response;
        }
      });
  }

  private truncateDateToDay(ref: Date): Date {
    return new Date(Date.UTC(ref.getFullYear(), ref.getMonth(), ref.getDate()));
  }

  private truncateTsToDay(ts: number): Date {
    return this.truncateDateToDay(new Date(ts));
  }

  private apiTripToTrip(now: Date, apiTrip: any): Trip {
    const startDate = this.truncateTsToDay(apiTrip.startDate);
    const endDate = this.truncateTsToDay(apiTrip.endDate);
    return {
      id: apiTrip.id,
      updated: new Date(apiTrip.updated),
      startDate: startDate,
      endDate: endDate,
      destination: apiTrip.destinationName,
      comment: apiTrip.comment || '',
      daysToStart: moment.duration(startDate.getTime() - now.getTime()).asDays()
    };
  }

  createCurrentUserTrip(startDate: Date, endDate: Date, destinationName: string, comment = ''): Observable<Trip> {
    const tripData = {
      startDate: startDate.getTime(),
      endDate: endDate.getTime(),
      destinationName,
      comment
    };

    return this.http.post(`/travelplanner/api/sec/users/${this.loggedInUser.id}/trips`, tripData)
      .map(response => {
        if (response.status === 200) {
          return this.apiTripToTrip(this.truncateDateToDay(new Date()), response.json());
        } else {
          throw response;
        }
      });
  }

}
