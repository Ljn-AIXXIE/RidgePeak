<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  AuthState, UserId, UserName, NickName, AvatarUrl, Email, Role, CreatedAt, UpdateAt, LastLoginTime, type AuthProfile,
  Stars, Walls, Followers, Default
} from '../../stores/auth.ts'
import api from "../../api";
import { goHome } from "../../route/router.ts";
import UIUtils from "../../utils/UIUtils.ts";

const route = useRoute()

const isSelf = computed(() => AuthState.value && route.params.userId == UserId.value)
const profile = ref<AuthProfile>()
const originalProfile = ref<AuthProfile>()

const isEditing = ref(false)

const editNickname = ref('')
const newAvatarFile = ref<File | null>(null)
const newAvatarPreview = ref('')

async function getCurrentUserData() {
  if (isSelf.value) {
    profile.value = {
      role: Role.value,
      userId: UserId.value,
      userName: UserName.value,
      nickName: NickName.value,
      avatarUrl: AvatarUrl.value,
      stars: Stars.value,
      walls: Walls.value,
      followers: Followers.value,
      email: Email.value,
      createdAt: CreatedAt.value,
      updatedAt: UpdateAt.value,
      lastLoginTime: LastLoginTime.value
    }
  } else {
    const result = await api.getUser(route.params.userId as string)
    if (!result.success) {
      goHome()
      return
    }
    profile.value = {
      role: result.role,
      userId: result.userId,
      userName: result.userName,
      nickName: result.nickName,
      avatarUrl: result.avatarUrl,
      stars: result.stars,
      walls: result.walls,
      followers: result.followers,
    }
  }
}

onMounted(() => getCurrentUserData())

function enterEditMode() {
  if (!isSelf.value) return
  originalProfile.value = JSON.parse(JSON.stringify(profile.value))
  editNickname.value = ''
  newAvatarFile.value = null
  newAvatarPreview.value = ''
  isEditing.value = true
}

function cancelEdit() {
  if (originalProfile.value) profile.value = JSON.parse(JSON.stringify(originalProfile.value))
  newAvatarFile.value = null
  newAvatarPreview.value = ''
  isEditing.value = false
}

function triggerAvatarUpload() {
  if (!isSelf.value || !isEditing.value) return
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (e) => {
    const file = (e.target as HTMLInputElement).files?.[0]
    if (!file) return
    if (!file.type.startsWith('image/')) {
      alert('请选择图片文件')
      return
    }
    if (file.size > 2 * 1024 * 1024) {
      alert('图片大小不能超过2MB')
      return
    }
    newAvatarFile.value = file

    const reader = new FileReader()
    reader.onload = (ev) => {
      newAvatarPreview.value = ev.target?.result as string
    }
    reader.readAsDataURL(file)
  }
  input.click()
}

async function saveChanges() {
  if (!isSelf.value) return

  let newAvatarUrl = profile.value?.avatarUrl
  if (newAvatarFile.value) {
    const result = await api.uploadAvatar(newAvatarFile.value)
    if (result.success && result.data) {
      newAvatarUrl = result.data
      AvatarUrl.value = newAvatarUrl as string
    } else {
      UIUtils.error(result.message)
      return
    }
  }
  let nicknameChanged = false
  if (editNickname.value !== profile.value?.nickName) {
    const result = await api.putMe(editNickname.value)
    if (result.success) {
      nicknameChanged = true
      NickName.value = editNickname.value
    } else {
      UIUtils.error(result.message)
      return
    }
  }

  if (profile.value) {
    if (newAvatarUrl) profile.value.avatarUrl = newAvatarUrl
    if (nicknameChanged) profile.value.nickName = editNickname.value
  }

  newAvatarFile.value = null
  newAvatarPreview.value = ''
  isEditing.value = false
  UIUtils.info('修改成功')
}
</script>

<template>
  <div class="card">
    <div class="title-bar title-text">
      <h2>个人资料</h2>
      <template v-if="isSelf">
        <button v-if="!isEditing" class="common-btn" @click="enterEditMode">修改</button>
        <template v-else>
          <button class="cancel-btn" @click="cancelEdit">取消</button>
          <button class="submit-btn" @click="saveChanges">保存</button>
        </template>
      </template>
    </div>

    <div class="info-row">
      <span class="info-label">头像</span>
      <div>
        <div class="avatar-item">
          <div class="avatar-label" v-if="isEditing">当前</div>
          <img v-if="profile?.avatarUrl" :src="profile.avatarUrl" class="profile-avatar" alt="avatar">
          <div v-else class="profile-avatar placeholder">{{ (profile?.nickName || Default.NickName).charAt(0) }}</div>
        </div>
        <div class="avatar-item" v-if="isEditing">
          <div class="avatar-label">新头像</div>
          <div class="profile-avatar upload-area" @click="triggerAvatarUpload">
            <img v-if="newAvatarPreview" :src="newAvatarPreview" class="profile-avatar" alt="new avatar">
            <div v-else class="placeholder-icon">+</div>
          </div>
          <div class="upload-hint">点击上传</div>
        </div>
      </div>
    </div>

    <div class="info-row">
      <span class="info-label">身份</span>
      <span>{{ profile?.role }}</span>
    </div>
    <div class="info-row">
      <span class="info-label">用户ID</span>
      <span>{{ profile?.userId }}</span>
    </div>
    <div class="info-row">
      <span class="info-label">用户名</span>
      <span>{{ profile?.userName }}</span>
    </div>
    <div class="info-row">
      <span class="info-label">雅号</span>
      <span v-if="!isEditing">{{ profile?.nickName || Default.NickName }}</span>
      <input v-else v-model="editNickname" class="info-input" type="text" placeholder="新雅号">
    </div>
    <template v-if="isSelf">
      <div class="info-row">
        <span class="info-label">电子邮箱</span>
        <span>{{ profile?.email || '未绑定' }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">注册时间</span>
        <span>{{ profile?.createdAt }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">资料更新</span>
        <span>{{ profile?.updatedAt }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">上次登临</span>
        <span>{{ profile?.lastLoginTime || '未知' }}</span>
      </div>
    </template>
  </div>
</template>

<style scoped>
@import "../../style/profile_card_common.css";

.avatar-item {
  text-align: center;
}
.avatar-label {
  font-size: 12px;
  color: var(--text);
  margin-bottom: 6px;
}
.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid var(--border);
}
.profile-avatar.placeholder {
  background: var(--code-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: bold;
  color: var(--text);
}
.upload-area {
  cursor: pointer;
  position: relative;
}
.placeholder-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--code-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: var(--text);
  border: 1px solid var(--border);
}
.upload-hint {
  font-size: 11px;
  color: var(--text);
  margin-top: 4px;
  opacity: 0.7;
}
</style>