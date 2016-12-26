import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { RouteNotFoundComponent } from './route-not-found.component';
import { TravelplannerApiClientModule } from './shared/travelplanner-api-client/travelplanner-api-client.module';
import { LoginComponent } from './login/login-component';
import { RegisterComponent } from './register/register-component';
import { MomentModule } from 'angular2-moment';
import { InputTextModule } from 'primeng/components/inputtext/inputtext';
import { SharedModule } from 'primeng/components/common/shared';
import { ButtonModule } from 'primeng/components/button/button';
import { MyTripsComponent } from './my-trips/my-trips.component';
import { MessagesModule } from 'primeng/components/messages/messages';
import { GrowlModule } from 'primeng/components/growl/growl';
import { GlobalMessagesModule } from './shared/global-messages-service/global-messages.module';

@NgModule({
  declarations: [
    AppComponent,
    RouteNotFoundComponent,
    LoginComponent,
    RegisterComponent,
    MyTripsComponent
  ],
  imports: [
    // angular
    BrowserModule,
    FormsModule,
    HttpModule,
    ReactiveFormsModule,

    // moment.js
    MomentModule,

    // primeng
    SharedModule,
    InputTextModule,
    ButtonModule,
    MessagesModule,
    GrowlModule,

    // project
    AppRoutingModule,
    TravelplannerApiClientModule,
    GlobalMessagesModule
  ],
  providers: [],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule { }
