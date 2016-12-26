import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RouteNotFoundComponent } from './route-not-found.component';
import { LoginComponent } from './login/login-component';
import { RegisterComponent } from './register/register-component';
import { MyTripsComponent } from './my-trips/my-trips.component';
import { CreateTripComponent } from './my-trips/create-trip.component';
import { LoggedInRouterGuard } from './login/logged-in-router-guard';
import { NotLoggedInRouterGuard } from './login/not-logged-in-router-guard';

const ROUTES: Routes = [
  { path: '', pathMatch: 'full', redirectTo: '/login'},
  { path: 'login', pathMatch: 'full', component: LoginComponent, canActivate: [ NotLoggedInRouterGuard ] },
  { path: 'register', pathMatch: 'full', component: RegisterComponent },
  { path: 'my-trips', canActivate: [ LoggedInRouterGuard ], children: [
    { path: '', pathMatch: 'full', component: MyTripsComponent },
    { path: 'create', pathMatch: 'full', component: CreateTripComponent }
  ]},
  // { path: 'user', component: EnterUsernameComponent, children: [
  //   { path: '' },
  //   { path: ':username', component: RepositoriesComponent }
  // ]},
  { path: '**', component: RouteNotFoundComponent}
];

@NgModule({
  imports: [
    RouterModule.forRoot(ROUTES)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {

}
