<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import {adminSettings, app} from '../stores/defaultValue.ts'
import {AuthState, Role} from "../stores/auth.ts";
import api from "../api";
import TopBar from "./common/TopBar.vue";
import ProfileCard from "./common/ProfileCard.vue";
import Madol from "./common/Madol.vue";

const isAdmin = computed(() => AuthState && Role.value === 'ADMIN')

const categories = ref<any[]>([])
const loading = ref(false)

const showCreateModal = ref(false)
const newCategory = ref({ name: '', desc: '' })
const creating = ref(false)

const showDetailModal = ref(false)
const currentDetail = ref<any>(null)

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

async function loadCategories() {
  loading.value = true
  const res = await api.getCategories()
  if (res.success) {
    categories.value = res.data || []
  } else {
    alert('❌ 加载专栏失败：' + res.message)
  }
  loading.value = false
}

// 创建专栏
async function createCategoryHandler() {
  if (!newCategory.value.name.trim()) {
    alert('请填写专栏名号')
    return
  }
  creating.value = true
  const res = await api.createCategory(newCategory.value.name, newCategory.value.desc)
  creating.value = false
  if (res.success) {
    alert('✅ 专栏「' + newCategory.value.name + '」立卷成功')
    closeCreateModal()
    await loadCategories()
  } else {
    alert('❌ 创建失败：' + res.message)
  }
}

// 删除确认
async function confirmDelete(id: string, name: string) {
  if (confirm(`是否确定削除专栏「${name}」？此操作不可逆。`)) {
    const res = await api.deleteCategory(id)
    if (res.success) {
      alert(`🗑️ 专栏「${name}」已从翰苑移除`)
      await loadCategories()
    } else {
      alert('❌ 删除失败：' + res.message)
    }
  }
}

// 查看详情
async function viewDetail(id: string) {
  const res = await api.getCategory(id)
  if (res.success) {
    currentDetail.value = res.data
    showDetailModal.value = true
  } else {
    alert('无法获取专栏详情：' + res.message)
  }
}

// 模态窗控制
function openCreateModal() {
  newCategory.value = { name: '', desc: '' }
  showCreateModal.value = true
}
function closeCreateModal() {
  showCreateModal.value = false
}
function closeDetailModal() {
  showDetailModal.value = false
  currentDetail.value = null
}

onMounted(() => {
  if (isAdmin.value) loadCategories()
})
</script>

<template>
  <div class="app-container">
    <TopBar :title="app + '·' + adminSettings" />

    <div class="main-layout">
      <div class="left-panel">
        <ProfileCard :show-back="true"/>
      </div>

      <div class="right-panel">
        <div v-if="!isAdmin" class="profile-card">
          <div class="title-bar title-text">远处声响</div>
          <div class="info-row info-label">君非执钥之人，此境惟翰苑主可入也。</div>
        </div>
        <div v-else class="profile-card">
          <div class="title-bar">
            <p class="title-text">翰苑·专栏辑录</p>
            <div class="common-btn" @click="openCreateModal">开卷新编</div>
          </div>

          <div class="info-row info-label" v-if="categories.length === 0">翰苑寂寥，尚未有专栏，请执笔开卷～</div>
          <template v-else>
            <div class="category-list" v-if="!loading">
              <div
                  v-for="cat in categories"
                  :key="cat.id"
                  class="category-card"
              >
                <div class="card-header">
                  <h3 class="category-name">{{ cat.name }}</h3>
                  <div class="actions">
                    <button class="submit-btn" @click="viewDetail(cat.id)">览卷</button>
                    <button class="cancel-btn" @click="confirmDelete(cat.id, cat.name)">削籍</button>
                  </div>
                </div>
                <p class="category-desc">{{ cat.description || '无题解' }}</p>
              </div>
            </div>
            <div class="info-row info-label" v-else>卷轴舒展中...</div>
          </template>

        </div>
      </div>
    </div>

    <Madol :out-close="false" :show="showDetailModal">
      <div class="profile-card" style="width: 480px; border-radius: 32px">
        <div class="title-bar">
          <p>创立新专栏</p>
          <div class="common-btn" @click="closeCreateModal">✖</div>
        </div>
        <div class="info-col">
          <label class="info-label">专栏名号</label>
          <input type="text" v-model="newCategory.name" placeholder="例：诗词雅集" class="info-input" />
        </div>
        <div class="info-col">
          <label class="info-label">卷轴题解</label>
          <textarea v-model="newCategory.desc" rows="3" placeholder="描述此专栏之旨趣..." class="info-textarea"></textarea>
        </div>
        <div class="action-bar">
          <button class="cancel-btn" @click="closeCreateModal">罢笔</button>
          <button class="submit-btn" @click="createCategoryHandler" :disabled="creating">
            {{ creating ? '编撰中...' : '立卷成册' }}
          </button>
        </div>
      </div>
    </Madol>

    <Madol :show="showDetailModal">
      <div class="profile-card" style="width: 480px; border-radius: 32px">
        <div class="title-bar">
          <p>《{{ currentDetail?.name || '专栏' }}》 卷宗</p>
          <div class="cancel-btn" @click="closeDetailModal">✖</div>
        </div>
        <template v-if="currentDetail">
          <div class="info-row">
            <span class="info-label">名号</span>
            <span class="info-value">{{ currentDetail.name }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">题解</span>
            <span class="info-value">{{ currentDetail.description || '无' }}</span>
          </div>
          <div class="info-row" v-if="currentDetail.createdAt">
            <span class="info-label">刊印日期</span>
            <span class="info-value">{{ formatDate(currentDetail.createdAt) }}</span>
          </div>
          <div class="info-row" v-if="currentDetail.id">
            <span class="info-label">翰苑编号</span>
            <span class="info-value">{{ currentDetail.id }}</span>
          </div>
        </template>
        <div class="action-bar">
          <div class="common-btn" @click="closeDetailModal">阖卷</div>
        </div>
      </div>
    </Madol>
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

.category-card {
  background: var(--bg-h);
  border: 1px solid var(--border);
  border-radius: 28px;
  padding: 18px 24px;
  transition: all 0.2s;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.02);
}

.category-card:hover {
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

.actions {
  display: flex;
  gap: 12px;
}

.category-desc {
  color: var(--text);
  line-height: 1.5;
  margin: 8px 0 6px 0;
  font-style: italic;
}

@media (max-width: 760px) {
  .right-panel {
    padding: 16px;
  }
  .card-header {
    flex-direction: column;
    align-items: start;
    gap: 8px;
  }
}
</style>