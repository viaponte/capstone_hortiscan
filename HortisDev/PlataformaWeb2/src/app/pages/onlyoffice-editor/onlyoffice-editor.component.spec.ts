import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlyofficeEditorComponent } from './onlyoffice-editor.component';

describe('OnlyofficeEditorComponent', () => {
  let component: OnlyofficeEditorComponent;
  let fixture: ComponentFixture<OnlyofficeEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OnlyofficeEditorComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OnlyofficeEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
