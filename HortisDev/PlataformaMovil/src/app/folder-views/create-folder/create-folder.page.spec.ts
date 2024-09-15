import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateFolderPage } from './create-folder.page';

describe('CreateFolderPage', () => {
  let component: CreateFolderPage;
  let fixture: ComponentFixture<CreateFolderPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateFolderPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
