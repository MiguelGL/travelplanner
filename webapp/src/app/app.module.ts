import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { RouteNotFoundComponent } from './route-not-found.component';
import { TravelplannerApiClientModule } from './shared/travelplanner-api-client/travelplanner-api-client.module';
import { LoginComponent } from './login/login-component';
import { RegisterComponent } from './register/register-component';
import { MomentModule } from 'angular2-moment';

@NgModule({
  declarations: [
    AppComponent,
    RouteNotFoundComponent,
    LoginComponent,
    RegisterComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,

    MomentModule,

    AppRoutingModule,
    TravelplannerApiClientModule
  ],
  providers: [],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule { }
