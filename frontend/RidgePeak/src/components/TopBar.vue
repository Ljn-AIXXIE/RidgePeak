<script setup lang="ts">
import {AuthState, AvatarUrl, Default, NickName} from "../stores/auth.ts";
import {goHome, goLogin, goRegister, goToProfile} from "../route/router.ts";
import api from "../api";
import {ref} from "vue";
import {app} from "../stores/defaultValue.ts";

const props = withDefaults(defineProps<{
  title?: string
}>(), {
  title: app
})

async function logout() {
  await api.logout()
  goLogin()
}

const isHover = ref(false)
</script>

<template>
  <div class="top-bar">
    <div class="title" @click="goHome">{{props.title}}</div>
    <div class="auth-area">
      <template v-if="AuthState">
        <div class="user-info" @mouseenter="isHover = true" @mouseleave="isHover = false">
          <transition name="slide">
            <span v-show="isHover" class="nickname">{{ NickName || Default.NickName }}</span>
          </transition>
          <div class="avatar-wrapper">
            <img v-if="AvatarUrl" :src="AvatarUrl" class="avatar-small" alt="avatar">
            <div v-else class="avatar-small placeholder">{{ (NickName || Default.NickName).charAt(0) }}</div>
          </div>

          <div class="menu-card">
            <div class="menu-item" @click="goToProfile">个人主页</div>
            <div class="menu-item" @click="goToProfile">账号设置</div>
            <div class="menu-divider"></div>
            <div class="menu-item exit" @click.stop="logout">出境</div>
          </div>
        </div>
      </template>
      <template v-else>
        <div class="btn-group">
          <RouterLink to="/login" class="action-btn">入境</RouterLink>
          <RouterLink to="/register" class="action-btn">新启</RouterLink>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.top-bar {
  width: 100vw;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  border-bottom: 1px solid var(--border);
  background-color: var(--bg);
}

.title {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  font-family: 宋体, serif;
  color: var(--text-h);
  letter-spacing: 4px;
  cursor: pointer;
  transition: opacity 0.2s;
}
.title:hover {
  opacity: 0.8;
}

.auth-area {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 1px;
  border-radius: 40px;
  transition: background-color 0.2s;
}
.user-info:hover {
  background-color: var(--code-bg);
}
.user-info:hover .menu-card {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.avatar-wrapper {
  display: flex;
  align-items: center;
}

.avatar-small {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid var(--border);
}
.avatar-small.placeholder {
  background-color: var(--code-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: var(--text);
}
.nickname {
  font-size: 12px;
  font-weight: 500;
  font-family: 宋体, serif;
  color: var(--text-h);
  padding-left: 16px;
}

.menu-card {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 160px;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 8px 0;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  opacity: 0;
  visibility: hidden;
  transform: translateY(-8px);
  transition: opacity 0.2s ease, visibility 0.2s, transform 0.2s ease;
  z-index: 100;
}

.menu-item {
  padding: 8px 16px;
  font-size: 14px;
  color: var(--text);
  transition: background-color 0.2s;
  cursor: pointer;
}
.menu-item:hover {
  background-color: var(--code-bg);
  color: var(--text-h);
}
.menu-item.exit {
  color: #d47a7a;
}
.menu-item.exit:hover {
  background-color: rgba(212, 122, 122, 0.15);
}
.menu-divider {
  height: 1px;
  background: var(--border);
  margin: 4px 0;
}

.btn-group {
  display: flex;
  gap: 12px;
}

.action-btn {
  padding: 8px 20px;
  border-radius: 40px;
  border: 1px solid var(--button-border);
  background-color: var(--button-bg);
  color: var(--text);
  font-size: 14px;
  cursor: pointer;
  font-family: 宋体, serif;
  transition: all 0.2s;
  text-decoration: none;
}
.action-btn:hover {
  background-color: var(--button-hover-bg);
  border-color: var(--button-hover-border);
  color: var(--text-h);
  transform: translateY(-1px);
}
.action-btn:active {
  background-color: var(--button-active-bg);
  border-color: var(--button-active-border);
  transform: translateY(0);
}

.slide-enter-active, .slide-leave-active {
  transition: all 0.2s ease;
}
.slide-enter-from {
  opacity: 0;
  transform: translateX(10px);
}
.slide-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>