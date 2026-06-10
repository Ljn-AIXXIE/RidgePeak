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
  Default
} from "../stores/auth.ts";
import {goLogin, goToProfile} from "../route/router.ts";
</script>

<template>
  <div class="profile-card">
    <div class="avatar-large-wrapper">
      <img v-if="AuthState && AvatarUrl" :src="AvatarUrl" class="avatar-large" alt="avatar">
      <p v-else class="avatar-large placeholder">{{ (NickName || Default.NickName).charAt(0) }}</p>
    </div>

    <div class="display-name">{{ AuthState ? NickName || Default.NickName : NoAuth.NickName }}</div>
    <div class="bio-text" v-if="AuthState">{{ Introduction }}</div>

    <div class="stats-row" v-if="AuthState">
      <div class="stat-item">
        <div>{{ Stars }}</div>
        <label>心许</label>
      </div>
      <div class="stat-item">
        <div>{{ Walls }}</div>
        <label>墨迹</label>
      </div>
      <div class="stat-item">
        <div>{{ Followers }}</div>
        <label>山缘</label>
      </div>
    </div>

    <div v-if="AuthState" class="profile-col">
      <button class="profile-btn" @click="goToProfile">个人资料</button>
      <button class="profile-btn" @click="goToProfile">账号设置</button>
    </div>
    <div v-else class="profile-col">
      <button class="profile-btn" @click="goLogin">登录查看资料</button>
    </div>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.profile-card {
  background-color: var(--bg);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 24px 20px;
  text-align: center;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  max-height: 100%;
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
  overflow: auto;
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
  margin-top: 20px;
  overflow: auto;
  max-height: 100%;
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