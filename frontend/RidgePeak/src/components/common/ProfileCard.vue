<script setup lang="ts">
import {
  AuthState,
  AvatarUrl,
  NickName,
  Introduction,
  Stars,
  Walls,
  Followers,
  NoAuth,
  Default, Role
} from "../../stores/auth.ts";
import {goAdmin, goHome, goLogin, goToProfile} from "../../route/router.ts";
import {adminSettings, follower, star, userInf, wall} from "../../stores/defaultValue.ts";

const props = withDefaults(defineProps<{
  showMenu?: boolean,
  showBack?: boolean,
}>(), {
  showMenu: true,
  showBack: false,
})
</script>

<template>
  <div class="container">
    <div class="profile-col" v-if="props.showBack">
      <button class="profile-btn" @click="goHome">{{ '返回' }}</button>
    </div>

    <div class="card">
      <div class="avatar-large-wrapper">
        <img v-if="AuthState && AvatarUrl" :src="AvatarUrl" class="avatar-large" alt="avatar">
        <p v-else class="avatar-large placeholder">{{ (NickName || Default.NickName).charAt(0) }}</p>
      </div>

      <div class="display-name">{{ AuthState ? NickName || Default.NickName : NoAuth.NickName }}</div>
      <div class="bio-text" v-if="AuthState">{{ Introduction }}</div>

      <div class="stats-row" v-if="AuthState">
        <div class="stat-item">
          <div>{{ Stars }}</div>
          <label>{{ star }}</label>
        </div>
        <div class="stat-item">
          <div>{{ Walls }}</div>
          <label>{{ wall }}</label>
        </div>
        <div class="stat-item">
          <div>{{ Followers }}</div>
          <label>{{ follower }}</label>
        </div>
      </div>

      <div class="profile-col" v-if="AuthState">
        <button class="profile-btn" @click="goAdmin" v-if="Role === 'ADMIN'">{{ adminSettings }}</button>
        <button class="profile-btn" @click="goToProfile">{{ userInf }}</button>
        <button class="profile-btn" @click="">{{ '挥笔墨迹' }}</button>
      </div>
      <div v-else class="profile-col">
        <button class="profile-btn" @click="goLogin">登录查看资料</button>
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

.container {
  width: 100%;
  min-width: 260px;
  max-width: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.card {
  width: 100%;
  background-color: var(--bg);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 24px 20px;
  text-align: center;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
}

.avatar-large-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.avatar-large {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--border);
}
.avatar-large.placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: var(--code-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: var(--border);
  border: 2px solid var(--border);
}

.display-name {
  word-break: break-all;
  font-size: 20px;
  font-weight: bold;
  color: var(--text-h);
  margin-bottom: 8px;
}

.bio-text {
  font-size: 14px;
  color: var(--text);
  margin-top: 16px;
  padding: 12px;
  background-color: var(--code-bg);
  border-radius: 20px;
  border: 1px solid var(--border);
}

.stats-row {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid var(--border);
  max-height: 100%;
  margin-bottom: 16px;
}

.stat-item {
  text-align: center;
}
.stat-item > div {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-h);
}
.stat-item > label {
  font-size: 12px;
  color: var(--text);
  margin-top: 4px;
}

.profile-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  width: 100%;
  max-height: 100%;

  overflow-y: auto;
  -ms-overflow-y: auto;
  -ms-overflow-style: none;
  scrollbar-width: none;
  overscroll-behavior: contain;
}
.stats-row::-webkit-scrollbar {
  display: none;
}

.profile-col > button {
  padding: 8px 16px;
  border-radius: 30px;
  border: 1px solid var(--button-border);
  background-color: var(--button-bg);
  color: var(--text);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
}
.profile-col > button:hover {
  background-color: var(--button-hover-bg);
  border-color: var(--button-hover-border);
  color: var(--text-h);
}
</style>