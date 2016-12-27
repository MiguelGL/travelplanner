import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';

@Injectable()
export class LoggedInRouterGuard implements CanActivate {

  constructor(private apiClient: TravelplannerApiClientService) {}

  canActivate(route: ActivatedRouteSnapshot,
              state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.apiClient.canManageUsers;
  }

}
