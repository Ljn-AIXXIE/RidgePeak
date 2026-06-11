import {ref} from "vue"
import {defineStore} from "pinia"
import api from "../api";

export interface AuthProfile {
    role: string
    userId: string
    userName: string
    nickName: string
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
export const UserId = ref("")

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

export const useAuthStore = defineStore("store", {
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

export function ClearUserInfo() {
    Role.value = ""
    UserId.value = ""

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

export async function RefreshUserInfo() {
    const result = await api.getMe()
    if (!result.success || result.code === 400 || !result.data) return {
        success: false,
        message: result.message,
    }

    Role.value = result.data.role
    UserId.value = result.data.userId

    UserName.value = result.data.username
    Email.value = result.data.email

    NickName.value = result.data.nickname
    Introduction.value = result.data.introduction || Default.Introduction
    AvatarUrl.value = result.data.avatarUrl || ''

    CreatedAt.value = new Date(result.data.createdAt).toLocaleString()
    UpdateAt.value = new Date(result.data.updatedAt).toLocaleString()
    LastLoginTime.value = new Date(result.data.lastLoginTime).toLocaleString()

    return {
        success: true,
        message: "获取用户个人信息成功",
    }
}