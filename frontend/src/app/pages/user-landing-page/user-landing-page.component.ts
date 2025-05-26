import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { PostCardComponent } from '../../components/post-card/post-card.component';
import { PostService } from '../../services/post.service';
import { TokenManagerService } from '../../services/token-manager.service'; 
import { CommonModule } from '@angular/common';
import { GetPostsResponse, PostData } from '../../../shared/models/post-model';


@Component({
  selector: 'app-user-landing-page',
  imports: [NavbarComponent, PostCardComponent, CommonModule],
  templateUrl: './user-landing-page.component.html',
  styleUrl: './user-landing-page.component.scss'
})
export class UserLandingPageComponent implements OnInit{
  posts: PostData[] = [];

  constructor(private postService: PostService, private tokenManager : TokenManagerService) {}

    ngOnInit(): void {
      const userId = this.tokenManager.getClaims().userId;
      if (userId) {
        this.postService.getPosts(userId).subscribe((response: GetPostsResponse) => {
          this.posts = response.posts;
        });
    }
  }
}
