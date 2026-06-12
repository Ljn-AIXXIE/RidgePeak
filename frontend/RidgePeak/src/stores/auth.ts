import {ref} from "vue"
import {defineStore} from "pinia"
import api from "../api";

export interface AuthProfile {
    role: string
    userId: number
    username: string
    nickname: string
    avatarUrl: string

    stars: number
    walls: number
    followers: number

    email?: string
    createdAt?: string
    updatedAt?: string
    lastLoginTime?: string
}

export const AuthState = ref(false)

export const Role = ref("")
export const UserId = ref(-1)

export const UserName = ref("")
export const Email = ref("")

export const NickName = ref("")
export const Introduction = ref("")
export const AvatarUrl = ref("")

export const CreatedAt = ref("")
export const UpdateAt = ref("")
export const LastLoginTime = ref("")

export const Stars = ref(0)
export const Walls = ref(0)
export const Followers = ref(0)

export const NoAuth = {
    NickName: '未入境',
    Introduction: '入境打开新世界'
}
export const Default = {
    NickName: '无名氏',
    Introduction: '小生匆忙，没有留下任何痕迹'
}

export const useAuthStore = defineStore("authStore", {
    state: () => {
        return { AuthState: false }
    },
    actions: {
        setAuthState(state: boolean) {
            this.$state.AuthState = state
            AuthState.value = state
        },
    },
})

export function clearUserInfo() {
    Role.value = ""
    UserId.value = -1

    UserName.value = ""
    Email.value = ""

    NickName.value = ""
    Introduction.value = ""
    AvatarUrl.value = ""

    CreatedAt.value = ""
    UpdateAt.value = ""
    LastLoginTime.value = ""

    Stars.value = 0
    Walls.value = 0
    Followers.value = 0
}

export async function refreshUserInfo() {
    const result = await api.getMe()
    if (!result.success) return {
        success: false,
        message: result.message as string,
    }

    Role.value = result.role
    UserId.value = result.userId

    UserName.value = result.username
    Email.value = result.email

    NickName.value = result.nickname
    Introduction.value = result.introduction || Default.Introduction
    AvatarUrl.value = result.avatarUrl || ''

    CreatedAt.value = result.createdAt
    UpdateAt.value = result.updatedAt
    LastLoginTime.value = result.lastLoginTime

    return {
        success: true,
        message: "获取用户个人信息成功",
    }
}