import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAppointmentCardComponent } from './user-appointment-card.component';

describe('UserAppointmentCardComponent', () => {
  let component: UserAppointmentCardComponent;
  let fixture: ComponentFixture<UserAppointmentCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserAppointmentCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserAppointmentCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
