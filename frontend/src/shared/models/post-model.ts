export interface PostData {
    id?: number;
    userId: number;
    title: string;
    content: string;
    category: string;
}

export interface GetPostsResponse {
    posts: PostData[];
}