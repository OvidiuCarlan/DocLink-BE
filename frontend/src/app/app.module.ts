import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { CreatePostCardComponent } from './components/create-post-card/create-post-card.component';
import { PostCardComponent } from './components/post-card/post-card.component';

@NgModule({
    imports: [
      BrowserModule,
      ReactiveFormsModule,
      AppComponent,            
      CreatePostCardComponent,      
      PostCardComponent,
      FormsModule,
      CreatePostCardComponent,
      PostCardComponent
    ],
  })
  export class AppModule { }