import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RouteNotFoundComponent } from './route-not-found.component';
import { LoginComponent } from './login/login-component';
import { RegisterComponent } from './register/register-component';

const ROUTES: Routes = [
  { path: '', pathMatch: 'full', redirectTo: '/login'},
  { path: 'login', pathMatch: 'full', component: LoginComponent },
  { path: 'register', pathMatch: 'full', component: RegisterComponent },
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
