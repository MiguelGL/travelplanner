import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TravelplannerApiClientService } from './travelplanner-api-client.service';

@NgModule({
  imports: [
    CommonModule
  ],
  providers: [
    TravelplannerApiClientService
  ]
})
export class TravelplannerApiClientModule {

}
