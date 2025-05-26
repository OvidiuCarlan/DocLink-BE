import { Component } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CreatePostCardComponent } from "../../components/create-post-card/create-post-card.component";

@Component({
  selector: 'app-create-post-page',
  standalone: true,
  imports: [NavbarComponent, CreatePostCardComponent],
  templateUrl: './create-post-page.component.html',
  styleUrl: './create-post-page.component.scss'
})
export class CreatePostPageComponent {

}
