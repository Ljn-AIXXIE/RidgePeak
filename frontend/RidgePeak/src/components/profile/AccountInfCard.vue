<script setup lang="ts">
import {computed, ref} from 'vue'
import UIUtils from '../../utils/UIUtils'
import api from "../../api";
import {AuthState, UserId} from "../../stores/auth.ts";
import {useRoute} from "vue-router";

const route = useRoute()
const isSelf = computed(() => AuthState.value && Number(route.params.userId) == UserId.value)

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
  handleCancel()
}

function handleCancel() {
  oldPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
}
</script>

<template>
  <div class="card" v-if="isSelf">

    <div class="title-bar title-text with-between-line">
      <h2>账号设置</h2>
      <button class="cancel-btn" @click="handleCancel">清空</button>
      <button class="submit-btn" @click="handleSave">保存修改</button>
    </div>

    <div class="info-row with-between-line">
      <span class="info-label">旧密码</span>
      <form>
        <input
            type="password"
            v-model="oldPassword"
            class="info-input"
            placeholder="请输入旧密码"
            autocomplete=""
        />
      </form>
    </div>

    <div class="info-row with-between-line">
      <span class="info-label">新密码</span>
      <form>
        <input
            type="password"
            v-model="newPassword"
            class="info-input"
            placeholder="请输入新密码"
            autocomplete=""
        />
      </form>
    </div>

    <div class="info-row with-between-line">
      <span class="info-label">确认新密码</span>
      <form>
        <input
            type="password"
            v-model="confirmPassword"
            class="info-input"
            placeholder="请再次输入新密码"
            autocomplete=""
        />
      </form>
    </div>

  </div>
</template>

<style scoped>
@import "../../style/profile_card_common.css";
</style>