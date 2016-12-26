import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

  private startTs: Date;

  ngOnInit(): void {
    this.startTs = new Date();
  }

}
