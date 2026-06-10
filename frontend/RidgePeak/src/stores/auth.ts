import {ref} from "vue"
import {defineStore} from "pinia"
import api from "../api";

export const AuthState = ref(false)

export const UserName = ref("")
export const NickName = ref("")
export const AvatarUrl = ref("")
export const UserId = ref("")
export const Role = ref("")
export const CreatedAt = ref("")
export const UpdateAt = ref("")
export const LastLoginTime = ref("")

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
    NickName.value = ""
    AvatarUrl.value = ""
    CreatedAt.value = ""
    UpdateAt.value = ""
    LastLoginTime.value = ""
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
    NickName.value = result.data.nickname ?? ''
    AvatarUrl.value = result.data.avatarUrl ?? ''
    CreatedAt.value = result.data.createdAt
    UpdateAt.value = result.data.updatedAt
    LastLoginTime.value = new Date(result.data.lastLoginTime).toLocaleString()

    return {
        success: true,
        message: "获取用户个人信息成功",
    }
}