<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  AuthState, UserId, UserName, NickName, AvatarUrl, Email, Role, CreatedAt, UpdateAt, LastLoginTime, type AuthProfile,
  Stars, Walls, Followers, Default
} from '../stores/auth'
import api from "../api";
import { goHome } from "../route/router.ts";
import TopBar from "./TopBar.vue";
import { app, userInf } from "../stores/defaultValue.ts";
import ProfileCard from "./ProfileCard.vue";
import UIUtils from "../utils/UIUtils.ts";

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
      role: result.data.role,
      userId: route.params.userId as string,
      userName: result.data.userName,
      nickName: result.data.nickName,
      avatarUrl: result.data.avatarUrl,
      stars: result.data.stars,
      walls: result.data.walls,
      followers: result.data.followers,
    }
  }
}

onMounted(() => {
  getCurrentUserData()
})

function enterEditMode() {
  if (!isSelf.value) return
  originalProfile.value = JSON.parse(JSON.stringify(profile.value))
  editNickname.value = profile.value?.nickName || ''
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
  <div class="app-container">
    <TopBar :title="app+'·'+userInf"/>

    <div class="main-layout">

      <div class="left-panel">
        <ProfileCard/>
      </div>

      <div class="right-panel">
        <div class="profile-card">
          <div class="action-bar" v-if="isSelf">
            <button v-if="!isEditing" class="edit-mode-btn" @click="enterEditMode">修改</button>
            <div v-else class="edit-buttons">
              <button class="save-btn" @click="saveChanges">保存</button>
              <button class="cancel-btn" @click="cancelEdit">取消</button>
            </div>
          </div>

          <div class="info-row">
            <span class="info-label">头像</span>
            <div class="avatar-wrapper">
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
            <span class="info-value">{{ profile?.role }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">用户ID</span>
            <span class="info-value">{{ profile?.userId }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">用户名</span>
            <span class="info-value">{{ profile?.userName }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">雅号</span>
            <span class="info-value" v-if="!isEditing">{{ profile?.nickName || Default.NickName }}</span>
            <input v-else v-model="editNickname" class="nickname-input" type="text" placeholder="输入雅号">
          </div>
          <template v-if="isSelf">
            <div class="info-row">
              <span class="info-label">电子邮箱</span>
              <span class="info-value">{{ profile?.email || '未绑定' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">注册时间</span>
              <span class="info-value">{{ profile?.createdAt }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">资料更新</span>
              <span class="info-value">{{ profile?.updatedAt }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">上次登临</span>
              <span class="info-value">{{ profile?.lastLoginTime || '未知' }}</span>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app-container {
  width: 100%;
  min-height: 100vh;
  background-color: var(--bg);
  display: flex;
  flex-direction: column;
}

.main-layout {
  display: flex;
  flex: 1;
  padding: 24px 32px;
  gap: 32px;
  flex-wrap: wrap;
}

.left-panel {
  flex: 1;
  min-width: 260px;
  max-width: 320px;
  height: 100%;
}
.right-panel {
  flex: 3;
  min-width: 280px;
  height: 100%;
}

.profile-card {
  width: 100%;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 32px 28px;
  box-shadow: 0 20px 35px -10px rgba(0, 0, 0, 0.4);
  position: relative;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}
.edit-mode-btn, .save-btn, .cancel-btn {
  background: var(--button-bg);
  border: 1px solid var(--button-border);
  border-radius: 40px;
  padding: 6px 18px;
  font-size: 14px;
  color: var(--text);
  cursor: pointer;
  transition: all 0.2s;
  font-family: 宋体, serif;
  margin-left: 8px;
}
.edit-mode-btn:hover, .save-btn:hover, .cancel-btn:hover {
  background: var(--button-hover-bg);
  border-color: var(--button-hover-border);
  color: var(--text-h);
}
.save-btn {
  background: #4c6a4e;
  border-color: #5c7c5e;
  color: white;
}
.save-btn:hover {
  background: #3d563f;
}
.cancel-btn {
  background: #6a4c4c;
  border-color: #7c5e5e;
  color: white;
}
.cancel-btn:hover {
  background: #563d3d;
}

.avatar-row {
  flex-direction: column;
  align-items: flex-start;
}
.avatar-wrapper {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}
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
.nickname-input {
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 24px;
  padding: 6px 12px;
  font-size: 15px;
  color: var(--text-h);
  font-family: inherit;
  outline: none;
  width: 60%;
  text-align: right;
}
.nickname-input:focus {
  border-color: var(--button-hover-border);
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 8px;
  transition: background-color 0.2s ease-in-out;
}
.info-row:hover {
  background: var(--code-bg);
}
.info-row + .info-row {
  border-top: 1px dashed var(--border);
}
.info-label {
  text-align: center;
  font-size: 14px;
  font-weight: 500;
  color: var(--text);
  opacity: 0.8;
  letter-spacing: 1px;
}
.info-value {
  font-size: 15px;
  color: var(--text-h);
  font-weight: 450;
  word-break: break-all;
  text-align: right;
}
</style>