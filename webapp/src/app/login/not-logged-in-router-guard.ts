import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { Injectable } from '@angular/core';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';

@Injectable()
export class NotLoggedInRouterGuard implements CanActivate {

  constructor(private apiClient: TravelplannerApiClientService,
              private messagesService: GlobalMessagesService,
              private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot,
              state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.apiClient.checkLoggedIn().map(loggedIn => {
      if (loggedIn) {
        this.messagesService.display({
          severity: 'info', summary: 'Already Logged In',
          detail: 'You are already logged in, redirecting'
        });
        this.router.navigateByUrl('/my-trips');
        return false;
      } else {
        return true;
      }
    });
  }

}
