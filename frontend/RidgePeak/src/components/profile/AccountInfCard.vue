<script setup lang="ts">
import { ref } from 'vue'
import UIUtils from '../../utils/UIUtils'
import api from "../../api";

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

async function handleSave() {
  if (!oldPassword.value) {
    UIUtils.info('请输入当前密码')
    return
  }
  if (!newPassword.value) {
    UIUtils.info('请输入新密码')
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    UIUtils.info('两次输入的新密码不一致')
    return
  }

  const result = await api.changePwd(oldPassword.value, newPassword.value)
  if (!result.success) {
    UIUtils.info(result.message)
    return
  }

  UIUtils.info(result.message)
  oldPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
}

function handleCancel() {
  oldPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
}
</script>

<template>
  <div class="profile-card">
    <div class="card-title">账号设置</div>

    <div class="info-row">
      <span class="info-label">旧密码</span>
      <input
          type="password"
          v-model="oldPassword"
          class="settings-input"
          placeholder="请输入旧密码"
      />
    </div>

    <div class="info-row">
      <span class="info-label">新密码</span>
      <input
          type="password"
          v-model="newPassword"
          class="settings-input"
          placeholder="请输入新密码"
      />
    </div>

    <div class="info-row">
      <span class="info-label">确认新密码</span>
      <input
          type="password"
          v-model="confirmPassword"
          class="settings-input"
          placeholder="请再次输入新密码"
      />
    </div>

    <div class="action-bar">
      <button class="submit-btn" @click="handleSave">保存修改</button>
      <button class="cancel-btn" @click="handleCancel">清空</button>
    </div>
  </div>
</template>

<style scoped>
@import "../../style/profile_card_common.css";
</style>