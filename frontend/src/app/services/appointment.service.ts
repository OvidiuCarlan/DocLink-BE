import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppointmentData, GetAppointmentsResponse } from '../../shared/models/appointment-model';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {

readonly url = 'api/appointments';

  constructor(private http: HttpClient, ) { }
  

  createAppointment(data: AppointmentData): Observable<AppointmentData>{
    return this.http.post<AppointmentData>(this.url, data)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Error creating appointment', error);
          return throwError(() => error);
        })
      );
  }

  // getUserAppointments(userId: string): Observable<GetAppointmentsResponse> {
  //   const getUrl = `${this.url}/${userId}`;
  //   return this.http.get<GetAppointmentsResponse>(getUrl)
  //     .pipe(
  //       catchError((error: HttpErrorResponse) => {
  //         console.error('Error fetching user appointments', error);
  //         return throwError(() => error);
  //       })
  //     );
  // }

  getUserAppointments(userId: string): Observable<GetAppointmentsResponse> {
  const getUrl = `${this.url}/${userId}`;
  return this.http.get<GetAppointmentsResponse>(getUrl).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error('Error fetching user appointments', error);
      return throwError(() => error);
    })
  );
}
}
