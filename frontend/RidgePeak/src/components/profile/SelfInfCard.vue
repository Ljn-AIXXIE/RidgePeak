<script setup lang="ts">
import {computed, onMounted, ref} from 'vue'
import {useRoute} from 'vue-router'
import {
  type AuthProfile,
  AuthState,
  AvatarUrl,
  CreatedAt,
  Default,
  Email,
  Followers,
  LastLoginTime,
  NickName,
  Role,
  Stars,
  UpdateAt,
  UserId,
  UserName,
  Walls
} from '../../stores/auth.ts'
import api from "../../api";
import {goHome} from "../../route/router.ts";
import UIUtils from "../../utils/UIUtils.ts";
import Avatar from "./Avatar.vue";

const route = useRoute()
const isSelf = computed(() => AuthState.value && Number(route.params.userId) == UserId.value)

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
      username: UserName.value,
      nickname: NickName.value,
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
      username: result.username,
      nickname: result.nickname,
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
      UIUtils.info('请选择图片文件')
      return
    }
    if (file.size > 2 * 1024 * 1024) {
      UIUtils.info('图片大小不能超过2MB')
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
  if (editNickname.value !== profile.value?.nickname) {
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
    if (nicknameChanged) profile.value.nickname = editNickname.value
  }

  newAvatarFile.value = null
  newAvatarPreview.value = ''
  isEditing.value = false
  UIUtils.info('修改成功')
}
</script>

<template>
  <div class="card">
    <div class="title-bar title-text with-between-line">
      <h2>{{ isSelf ? '个人资料' : '"' + (profile?.nickname || Default.NickName) + '"的资料' }}</h2>
      <template v-if="isSelf">
        <button v-if="!isEditing" class="common-btn" @click="enterEditMode">修改</button>
        <template v-else>
          <button class="cancel-btn" @click="cancelEdit">取消</button>
          <button class="submit-btn" @click="saveChanges">保存</button>
        </template>
      </template>
    </div>

    <div class="info-row with-between-line">
      <span class="info-label">头像</span>
      <div class="avatar-func">
        <div class="avatar-item">
          <div class="avatar-label" v-if="isEditing">当前</div>
          <Avatar
              :size="80"
              :url="profile?.avatarUrl"
              :text="(profile?.nickname || Default.NickName).charAt(0)"
          />
        </div>
        <div class="avatar-item" v-if="isEditing">
          <div class="avatar-label">新头像</div>
          <div class="profile-avatar upload-area" @click="triggerAvatarUpload">
            <Avatar
                :size="80"
                :url="newAvatarPreview"
                :text="'+'"
            />
          </div>
          <div class="upload-hint">点击上传</div>
        </div>
      </div>
    </div>

    <div class="info-row with-between-line">
      <span class="info-label">身份</span>
      <span>{{ profile?.role }}</span>
    </div>
    <div class="info-row with-between-line">
      <span class="info-label">用户ID</span>
      <span>{{ profile?.userId }}</span>
    </div>
    <div class="info-row with-between-line">
      <span class="info-label">用户名</span>
      <span>{{ profile?.username }}</span>
    </div>
    <div class="info-row with-between-line">
      <span class="info-label">雅号</span>
      <span v-if="!isEditing">{{ profile?.nickname || Default.NickName }}</span>
      <input v-else v-model="editNickname" class="info-input" type="text" placeholder="新雅号">
    </div>
    <template v-if="isSelf">
      <div class="info-row with-between-line">
        <span class="info-label">电子邮箱</span>
        <span>{{ profile?.email || '未绑定' }}</span>
      </div>
      <div class="info-row with-between-line">
        <span class="info-label">注册时间</span>
        <span>{{ new Date(profile?.createdAt as string).toLocaleString() }}</span>
      </div>
      <div class="info-row with-between-line">
        <span class="info-label">资料更新</span>
        <span>{{ new Date(profile?.updatedAt as string).toLocaleString() }}</span>
      </div>
      <div class="info-row with-between-line">
        <span class="info-label">上次登临</span>
        <span>{{ new Date(profile?.lastLoginTime as string).toLocaleString() }}</span>
      </div>
    </template>
  </div>
</template>

<style scoped>
@import "../../style/profile_card_common.css";
.avatar-func {
  align-items: start !important;
}
.avatar-item {
  flex-direction: column !important;
  text-align: center !important;
  justify-content: center !important;
  align-items: center !important;
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
  flex-direction: column !important;
  align-items: center !important;
  justify-content: center !important;
  font-size: 32px;
  font-weight: bold;
  color: var(--text);
}
.upload-area {
  cursor: pointer;
  position: relative;
}
.upload-hint {
  font-size: 11px;
  color: var(--text);
  margin-top: 4px;
  opacity: 0.7;
}
</style>