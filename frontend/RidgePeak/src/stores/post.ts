import type {Category} from "./category.ts";
import type {AuthProfile} from "./auth.ts";

export type FSortType = 'latest' | 'popular' | undefined
export type FCategoryIdType = number | undefined
export type FPageType = number | undefined
export type FKeyWordType = string | undefined

export interface Post {
    postId: number,
    title: string,
    trimmedContent: string,
    categoryName: string,
    authorName: string,
    viewCount: number,
    createdAt: number,
}

export interface PostDetail {
    title: string,
    content: string,
    viewCount: number,
    likeCount: number,
    category: Category,
    author: AuthProfile,
    createdAt: string,
    updatedAt: string,
}