import type {AuthProfile} from "./auth.ts";

export interface Comment {
    commentId: number;
    content: string;
    author: AuthProfile;
    createdAt: string;
    children: Comment[];
}