import type {Category} from "./category.ts";
import type {AuthProfile} from "./auth.ts";

export type FSortType = 'latest' | 'popular' | undefined
export type FCategoryIdType = number | undefined
export type FPageType = number | undefined
export type FKeyWordType = string | undefined

export interface Wall {
    postId: number,
    title: string,
    trimmedContent: string,
    categoryName: string,
    authorName: string,
    viewCount: number,
    createCount: number,
}

export interface WallDetail {
    title: string,
    content: string,
    viewCount: number,
    category: Category,
    author: AuthProfile,
    createAt: string,
    updateAt: string,
}