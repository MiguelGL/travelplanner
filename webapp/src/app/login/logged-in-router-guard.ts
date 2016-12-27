import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { TravelplannerApiClientService } from '../shared/travelplanner-api-client/travelplanner-api-client.service';
import { GlobalMessagesService } from '../shared/global-messages-service/global-messages.service';

@Injectable()
export class LoggedInRouterGuard implements CanActivate {

  constructor(private router: Router,
              private apiClient: TravelplannerApiClientService,
              private messagesService: GlobalMessagesService) {}

  canActivate(route: ActivatedRouteSnapshot,
              state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    if (this.apiClient.isLoggedIn) {
      return true;
    } else {
      this.messagesService.display({
        severity: 'info', summary: 'Not Logged In',
        detail: 'You are not logged in, redirecting to login'
      })
      this.router.navigateByUrl('/login');
      return false;
    }
  }

}
