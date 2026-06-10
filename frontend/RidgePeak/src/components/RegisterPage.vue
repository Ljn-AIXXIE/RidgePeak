<script setup lang="ts">
import {ref} from "vue";
import {useRouter} from "vue-router";
import api from "../api";
import {RefreshUserInfo, useAuthStore} from "../stores/auth.ts";
import UIUtils from "../utils/UIUtils.ts";

const form = ref({
  username: "",
  password: "",
  email: "",
  confirm_password: "",
});

const authStore = useAuthStore();

const handleSubmit = async () => {
  if (form.value.password !== form.value.confirm_password) {
    UIUtils.info('再三提醒，再三提醒')
    return;
  }

  const result = await api.register(form.value.username, form.value.password, form.value.email);

  if (!result.success) {
    UIUtils.info(result.message)
    return
  }

  const resultMe = await RefreshUserInfo()
  if (!resultMe.success) {
    UIUtils.info(resultMe.message)
    return
  }

  UIUtils.info(result.message)
  authStore.setAuthState(true)

  router.push("/home");
};
const router = useRouter();
</script>

<template>
  <div class="container">
    <div class="login-container">
      <div class="poetry-header">
        <h2>题西林壁·新启</h2>
        <p>横看成岭侧成峰 · 远近高低都是你</p>
      </div>

      <div class="card">

        <form action="javascript:void(0);" method="post">
          <div class="input-group">
            <label>雅号 · 山间名 / 邮箱</label>
            <input v-model="form.username" type="text" placeholder="是东坡故友，还是西林散人、夜山客...">
            <input v-model="form.email" type="email" placeholder="故址有名曰...">
          </div>
          <div class="input-group">
            <label>墨印 · 密钥</label>
            <input v-model="form.password" type="password" placeholder="暗语">
            <input v-model="form.confirm_password" type="password" placeholder="再三提醒">
          </div>

          <button type="submit" class="btn" @click="handleSubmit">登临西林 · 新启</button>
        </form>

        <div class="hint">
          已识庐山真面？ <a href="/login" class="link">点此题壁 · 入境墨缘</a>
        </div>
        <div class="mountain-silhouette">～ 远山长 云山乱 晓山青 ～</div>
      </div>
      <div class="footer-motto">
        不识庐山真面目 · 只缘身在此山中
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  max-width: 460px;
}

.poetry-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-bottom: 36px;
}

.poetry-header h2 {
  font-weight: 500;
  letter-spacing: 4px;
  color: var(--text-h);
  display: inline-block;
  background: linear-gradient(135deg, #e8dfd8 0%, #cbc0bc 100%);
  background-clip: text;
  -webkit-background-clip: text;
  text-shadow: 0 0 2px var(--shadow);
  font-family: 宋体, serif;
}

.poetry-header p {
  display: inline-block;
  font-size: small;
  font-weight: 240;
  color: var(--text);
  background: var(--code-bg);
  border-radius: 16px;
  border: 1px solid var(--border);
  padding: 4px 8px;
  margin-top: 16px;
  font-style: italic;
  backdrop-filter: blur(2px);
}

.card {
  min-width: 420px;
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 24px 16px 16px;
  box-shadow: 0 20px 35px -12px rgba(0, 0, 0, 0.5), 0 1px 2px rgba(255, 255, 255, 0.03);
  transition: all 0.2s;
  backdrop-filter: blur(2px);
}

.card:hover {
  border-color: var(--button-hover-border);
  box-shadow: 0 24px 40px -14px rgba(0, 0, 0, 0.6);
}

.input-group {
  margin-bottom: 18px;
}

label {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 450;
  color: var(--text);
}

input {
  width: 100%;
  padding: 16px 16px;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 28px;
  font-family: inherit;
  font-size: 16px;
  color: var(--text);
  transition: all 0.2s ease;
  outline: none;
}
input:focus {
  border-color: var(--button-hover-border);
  box-shadow: 0 0 0 3px rgba(124, 98, 120, 0.25);
  background-color: var(--bg);
}
input::placeholder {
  color: #5e5468;
  font-size: 14px;
  font-style: italic;
}
input + input {
  margin-top: 8px;
}

.btn {
  background: var(--button-bg);
  border: 1px solid var(--button-border);
  width: 100%;
  padding: 16px 0;
  border-radius: 40px;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-h);
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 24px;
}

.btn:hover {
  background: var(--button-hover-bg);
  border-color: var(--button-hover-border);
  transform: translateY(-2px);
}

.btn:active {
  background: var(--button-active-bg);
  border-color: var(--button-active-border);
  transform: translateY(1px);
}

.hint {
  text-align: center;
  font-size: 12px;
  border-top: 1px solid var(--border);
  padding-top: 16px;
  margin-top: 8px;
}

.link {
  color: var(--text-h);
  text-decoration: none;
  font-weight: 500;
  border-bottom: 1px dotted var(--button-hover-border);
  transition: 0.2s;
}

.link:hover {
  color: #f2ece4;
  border-bottom-color: var(--text-h);
}

.footer-motto {
  text-align: center;
  margin-top: 1.8rem;
  font-size: 0.7rem;
  opacity: 0.6;
  color: var(--text);
}

.mountain-silhouette {
  text-align: center;
  margin-top: 1rem;
  font-size: 0.75rem;
  letter-spacing: 4px;
  opacity: 0.4;
}
</style>