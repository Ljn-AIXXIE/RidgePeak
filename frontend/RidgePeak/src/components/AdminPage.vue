<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import {DText} from '../stores/defaultValue.ts'
import {AuthState, Role} from "../stores/auth.ts";
import api from "../api";
import TopBar from "./common/TopBar.vue";
import ProfileCard from "./common/ProfileCard.vue";
import Modal from "./common/Modal.vue";
import UIUtils from "../utils/UIUtils.ts";
import {Categories, refreshCategories, Default as catDefault} from "../stores/category.ts";

const isAdmin = computed(() => AuthState && Role.value === 'ADMIN')

const loading = ref(false)

const showCreateModal = ref(false)
const newCategory = ref({ name: '', description: '' })
const creating = ref(false)

const showDetailModal = ref(false)
const currentDetail = ref<any>(null)

async function createCategoryHandler() {
  if (!isAdmin.value) {
    UIUtils.info('无权')
    return
  }
  if (!newCategory.value.name.trim()) {
    UIUtils.info('请填写专栏名号')
    return
  }
  creating.value = true
  const res = await api.createCategory(newCategory.value.name, newCategory.value.description)
  creating.value = false
  if (res.success) {
    UIUtils.info('专栏「' + newCategory.value.name + '」立卷成功')
    closeCreateModal()
    await refreshCategories()
  } else {
    UIUtils.error('创建失败：' + res.message)
  }
}

async function confirmDelete(id: number, name: string) {
  if (!isAdmin.value) {
    UIUtils.info('无权')
    return
  }
  if (confirm(`是否确定削除专栏「${name}」？此操作不可逆。`)) {
    const res = await api.deleteCategory(id)
    if (res.success) {
      UIUtils.error(`专栏「${name}」已从翰苑移除`)
      await refreshCategories()
    } else {
      UIUtils.error('删除失败：' + res.message)
    }
  }
}

async function viewDetail(id: number) {
  const res = await api.getCategory(id)
  if (res.success) {
    currentDetail.value = res.data
    showDetailModal.value = true
  } else {
    UIUtils.error('无法获取专栏详情：' + res.message)
  }
}

function openCreateModal() {
  newCategory.value = { name: '', description: '' }
  showCreateModal.value = true
}
function closeCreateModal() {
  showCreateModal.value = false
}
function closeDetailModal() {
  showDetailModal.value = false
  currentDetail.value = null
}

onMounted(() => refreshCategories())
</script>

<template>
  <div class="app-container">
    <TopBar :title="DText.APP + '·' + DText.ADMIN_SETTINGS" />

    <div class="main-layout">
      <div class="left-panel">
        <ProfileCard :show-back="true"/>
      </div>

      <div class="right-panel">
        <div v-if="!isAdmin" class="card">
          <div class="title-bar title-text with-between-line">远处声响</div>
          <div class="with-between-line">君非执钥之人，此境惟翰苑主可入也。</div>
        </div>
        <div v-else class="card">
          <div class="title-bar with-between-line">
            <h2>翰苑·专栏辑录</h2>
            <div class="common-btn" @click="openCreateModal">开卷新编</div>
          </div>

          <div class="info-row with-between-line" v-if="Categories.length === 0">翰苑寂寥，尚未有专栏，请执笔开卷～</div>
          <template v-else>
            <div class="category-list" v-if="!loading">
              <div
                  v-for="cat in Categories"
                  :key="cat.id"
                  class="select-card"
              >
                <div class="card-header">
                  <h3 class="category-name">{{ cat.name }}</h3>
                  <div class="action-bar">
                    <button class="submit-btn" @click="viewDetail(cat.id)">览卷</button>
                    <button class="cancel-btn" @click="confirmDelete(cat.id, cat.name)">削籍</button>
                  </div>
                </div>
                <p class="category-desc">{{ cat.description || catDefault.description }}</p>
              </div>
            </div>
            <div class="info-row with-between-line" v-else>卷轴舒展中...</div>
          </template>

        </div>
      </div>
    </div>

    <Modal :out-close="false" :show="showCreateModal">
      <div class="card" style="width: 480px; border-radius: 32px">
        <div class="title-bar">
          <h2>创立新专栏</h2>
          <div class="common-btn" @click="closeCreateModal">✖</div>
        </div>
        <div class="info-col">
          <label class="info-label">专栏名号</label>
          <input type="text" v-model="newCategory.name" placeholder="例：诗词雅集" class="info-input" />
        </div>
        <div class="info-col">
          <label class="info-label">卷轴题解</label>
          <textarea v-model="newCategory.description" rows="3" placeholder="描述此专栏之旨趣..." class="info-textarea"></textarea>
        </div>
        <div class="action-bar">
          <button class="cancel-btn" @click="closeCreateModal">罢笔</button>
          <button class="submit-btn" @click="createCategoryHandler" :disabled="creating">
            {{ creating ? '编撰中...' : '立卷成册' }}
          </button>
        </div>
      </div>
    </Modal>

    <Modal :show="showDetailModal">
      <div class="card" style="width: 480px; border-radius: 32px">
        <div class="title-bar with-between-line">
          <h2>《{{ currentDetail?.name || '专栏' }}》 卷宗</h2>
          <div class="cancel-btn" @click="closeDetailModal">✖</div>
        </div>
        <template v-if="currentDetail">
          <div class="info-row with-between-line">
            <span class="info-label">名号</span>
            <span class="info-value">{{ currentDetail.name }}</span>
          </div>
          <div class="info-row with-between-line">
            <span class="info-label">题解</span>
            <span class="info-value">{{ currentDetail.description || '无' }}</span>
          </div>
          <div class="info-row with-between-line" v-if="currentDetail.id">
            <span class="info-label">翰苑编号</span>
            <span class="info-value">{{ currentDetail.id }}</span>
          </div>
        </template>
        <div class="action-bar with-between-line">
          <div class="common-btn" @click="closeDetailModal">阖卷</div>
        </div>
      </div>
    </Modal>
  </div>
</template>

<style scoped>
@import "../style/page_left_right.css";
@import "../style/profile_card_common.css";

.category-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.select-card {
  background: var(--bg-h);
  border: 1px solid var(--border);
  border-radius: 28px;
  padding: 18px 24px;
  transition: all 0.2s;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.02);
}

.select-card:hover {
  box-shadow: var(--shadow);
  border-color: var(--button-hover-border);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.category-name {
  font-size: 1.4rem;
  font-weight: 600;
  color: var(--text-h);
  margin: 0;
  border-left: 5px solid var(--button-border);
  padding-left: 14px;
  font-family: var(--heading), serif;
}

.category-desc {
  color: var(--text);
  line-height: 1.5;
  margin: 8px 0 6px 0;
  font-style: italic;
}
</style>