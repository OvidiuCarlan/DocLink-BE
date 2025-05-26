import { Injectable } from '@angular/core';
import { GetPostsResponse, PostData } from '../../shared/models/post-model';
import { Observable, catchError, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class PostService {

readonly url = 'http://localhost:9000/posts';

  constructor(private http: HttpClient, ) { }

  createPost(data: PostData): Observable<PostData> {
    return this.http.post<PostData>(this.url, data)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Error creating post:', error);
          return throwError(() => error);
        })
      );
  }

  // getPosts(userId: string): Observable<GetPostsResponse> {
  //   return this.http.get<GetPostsResponse>(`${this.url}/${userId}`);
  // } 
  
  getPosts(userId: string): Observable<GetPostsResponse> {
  return this.http.get<GetPostsResponse>(`${this.url}/${userId}`).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error('Error fetching posts:', error);
      return throwError(() => error);
    })
  );
}
}
