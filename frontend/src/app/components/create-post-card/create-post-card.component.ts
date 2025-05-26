import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PostService } from '../../services/post.service';
import { PostData } from '../../../shared/models/post-model';
import { TokenManagerService } from '../../services/token-manager.service';


@Component({
  selector: 'app-create-post-card',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './create-post-card.component.html',
  styleUrl: './create-post-card.component.scss'
})
export class CreatePostCardComponent {
  postForm: FormGroup;
  userId: any;

  constructor(private fb: FormBuilder, private postService: PostService,private tokenManager: TokenManagerService) {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      content: ['', [Validators.required]],
      category: ['general', Validators.required]
    });
  }

  onSubmit() {
    this.userId = this.tokenManager.getClaims().userId;

    if (this.postForm.valid && this.tokenManager.getClaims().roles == "DOC") {
      const newPost: PostData = {
        userId: this.userId,
        title: this.postForm.value.title,
        content: this.postForm.value.content,
        category: this.postForm.value.category,
      };

      this.postService.createPost(newPost).subscribe({
      next: (response) => {
        console.log('Post successfully created:', response);
        this.postForm.reset();
      },
      error: (err) => {
        console.error('Error creating post:', err);
      }
    });
    }
  }
}
