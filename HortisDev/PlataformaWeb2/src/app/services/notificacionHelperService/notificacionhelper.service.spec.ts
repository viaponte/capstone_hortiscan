import { TestBed } from '@angular/core/testing';

import { NotificacionhelperService } from './notificacionhelper.service';

describe('NotificacionhelperService', () => {
  let service: NotificacionhelperService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificacionhelperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
