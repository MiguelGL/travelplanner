import { NgModule } from '@angular/core';
import { GrowlModule } from 'primeng/components/growl/growl';
import { CommonModule } from '@angular/common';
import { GlobalMessagesService } from './global-messages.service';

@NgModule({
  imports: [
    CommonModule,
    GrowlModule
  ],
  providers: [
    GlobalMessagesService
  ]
})
export class GlobalMessagesModule {}
