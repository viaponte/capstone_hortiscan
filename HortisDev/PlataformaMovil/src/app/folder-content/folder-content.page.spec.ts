import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FolderContentPage } from './folder-content.page';

describe('FolderContentPage', () => {
  let component: FolderContentPage;
  let fixture: ComponentFixture<FolderContentPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(FolderContentPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
