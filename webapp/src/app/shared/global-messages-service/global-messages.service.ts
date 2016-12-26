import { Injectable } from '@angular/core';
import { Message } from 'primeng/components/common/api';
import { Subject } from 'rxjs';

@Injectable()
export class GlobalMessagesService {

  readonly messages = new Subject<Message[]>();

  display(message: Message) {
    this.messages.next([message]);
  }

}
