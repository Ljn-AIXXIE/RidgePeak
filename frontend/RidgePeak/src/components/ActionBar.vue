<script setup lang="ts">
import {AuthState, AvatarUrl, NickName} from "../stores/auth.ts";
import router from "../route/router.ts";

</script>

<template>
  <div class="action-bar">
    <div class="title-bar">
      <div class="line"/>
      <h1 class="title">题西林壁</h1>
      <div class="line"/>
    </div>
    <div class="author" v-if="AuthState">
      <img :src="AvatarUrl" alt="avatar" v-if="AvatarUrl"/>
      <p v-if="NickName">{{NickName}}</p>
    </div>
    <div class="btn-bar" v-if="!AuthState">
      <button class="circle-btn" v-on:click="router.push('/register')">注册</button>
      <button class="circle-btn" v-on:click="router.push('/login')">登录</button>
    </div>
  </div>
</template>

<style scoped>
.action-bar {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  width: 100vw;
  height: min-content;
}

.title-bar, .author, .btn-bar {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 16px;
  width: 100%;
}

.circle-btn {
  margin: 16px;
  border-radius: 50%;
  text-align: center;
  font-family: 宋体, serif;
  font-size: 16px;

  width: 72px;
  height: 72px;
  opacity: 0;

  animation: fadeIn 2s ease-in-out forwards;
}

.title {
  margin: 0;
  padding: 36px 16px 36px 16px;
  text-align: center;
  font-weight: bold;
  font-family: 宋体, serif;

  width: max-content;

  animation: fadeIn 2s;
}

.line {
  height: 1px;
  flex: 1;
  margin: 0 16px;
  background-color: var(--text);

  animation: fadeIn 2s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(100px) scale(0);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>