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
import { DataTableModule } from 'primeng/components/datatable/datatable';
import { CreateTripComponent } from './my-trips/create-trip.component';
import { LoggedInRouterGuard } from './login/logged-in-router-guard';
import { NotLoggedInRouterGuard } from './login/not-logged-in-router-guard';
import { CalendarModule } from 'primeng/components/calendar/calendar';
import { TripsPlanComponent } from './trips-plan/trips-plan.component';
import { EditTripComponent } from './my-trips/edit-trip.component';
import { UsersListComponent } from './users/users-list.component';
import { UsersManagementRouterGuard } from './users/users-management-router-guard';
import { UserCreateComponent } from './users/user-create.component';
import { UserFormComponent } from './users/user-form.component';

@NgModule({
  declarations: [
    AppComponent,
    RouteNotFoundComponent,
    LoginComponent,
    RegisterComponent,
    MyTripsComponent,
    CreateTripComponent,
    TripsPlanComponent,
    EditTripComponent,
    UsersListComponent,
    UserCreateComponent,
    UserFormComponent
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
    DataTableModule,
    CalendarModule,

    // project
    AppRoutingModule,
    TravelplannerApiClientModule,
    GlobalMessagesModule
  ],
  providers: [
    LoggedInRouterGuard, NotLoggedInRouterGuard, UsersManagementRouterGuard
  ],
  bootstrap: [
    AppComponent
  ]
})
export class AppModule { }
