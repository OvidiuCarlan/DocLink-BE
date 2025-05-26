import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocLandingPageComponent } from './doc-landing-page.component';

describe('DocLandingPageComponent', () => {
  let component: DocLandingPageComponent;
  let fixture: ComponentFixture<DocLandingPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocLandingPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DocLandingPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
