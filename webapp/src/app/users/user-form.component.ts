import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, Validators, FormControl, FormGroup } from '@angular/forms';
import { User } from '../shared/travelplanner-api-client/user';

@Component({
  selector: 'user-form',
  templateUrl: './user-form.component.html'
})
export class UserFormComponent implements OnInit {

  @Input() user: User;

  @Output() saveRequest = new EventEmitter<User>();

  userForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit() {
    this.userForm = this.formBuilder.group({
      email: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(64),
        Validators.pattern('^.+@.+$')
      ])),
      role: new FormControl('', Validators.compose([
        Validators.required
      ])),
      firstName: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(64)
      ])),
      lastName: new FormControl('', Validators.compose([
        Validators.maxLength(64)
      ])),
      password: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(48),
        Validators.pattern('^[0-9a-zA-Z\_\-]+$')
      ])),
      confirmedPassword: new FormControl('', Validators.compose([
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(48),
        Validators.pattern('^[0-9a-zA-Z\_\-]+$')
      ])),
    }, {
      validator: (group: FormGroup) => {
        const passwordInput = group.get('password');
        const confirmPasswordInput = group.get('confirmedPassword');
        return (passwordInput.value === confirmPasswordInput.value)
          ? confirmPasswordInput.setErrors(confirmPasswordInput.validator(confirmPasswordInput))
          : confirmPasswordInput.setErrors({
          passwordsmismatch: true
        });
      }
    });

    if (this.user) this.userForm.patchValue(this.user, { onlySelf: true});
  }

  submit(value: any, valid: boolean, data: any) {
    if (!valid) return;
    this.saveRequest.emit(value as User);
  }
}
