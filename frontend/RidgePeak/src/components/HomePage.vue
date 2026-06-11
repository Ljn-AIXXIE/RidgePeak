<script setup lang="ts">
import "vue-waterfall-plugin-next/dist/style.css";
import {computed, onMounted, ref} from 'vue'
import { Waterfall } from 'vue-waterfall-plugin-next'
import TopBar from "./common/TopBar.vue";
import ProfileCard from "./common/ProfileCard.vue";
import {Categories, CategoryId, CategoryState, Default as catDefault, refreshCategories} from "../stores/category.ts";
import OptionSelect from "./select/OptionSelect.vue";
import api from "../api";
import UIUtils from "../utils/UIUtils.ts";
import type {FCategoryIdType, FKeyWordType, FPageType, Wall} from "../stores/wall.ts";
import type {FSortType} from "../stores/wall.ts";

const dataLoaded = ref(false);
const dataList = ref<Array<Wall>>([])

const keyword = ref<FKeyWordType>()
const sortType = ref<FSortType>()
const page = ref<FPageType>()

const totalPages = ref(0)

async function selectCategory(newCategoryId: FCategoryIdType) {
  if (CategoryId.value === newCategoryId) CategoryId.value = undefined
  else CategoryId.value = newCategoryId
  await refreshWalls()
}

async function goPrevPage() {
  if (page.value && page.value > 1) {
    page.value--
    await refreshWalls()
  }
}
async function goNextPage() {
  if (page.value && page.value < totalPages.value) {
    page.value++
    await refreshWalls()
  }
}
async function goToPage(_page: number) {
  if (_page >= 1 && _page <= totalPages.value) {
    page.value = _page
    await refreshWalls()
  }
}
async function changeSort(type: FSortType) {
  if (sortType.value === type) sortType.value = undefined
  else sortType.value = type
  page.value = 1
  await refreshWalls()
}

async function refreshWalls() {
  const result = await api.getWalls(CategoryId.value, keyword.value, sortType.value, page.value)
  dataLoaded.value = result.success
  if (!result.success) {
    UIUtils.info(result.message)
    return
  }
  totalPages.value = result.totalPages as number
  dataList.value = result.posts as Wall[]
}

const visiblePages = computed(() => {
  if (!page.value) return [1, 2, 3, 4, 5]
  const total = totalPages.value
  let start = Math.max(1, page.value - 2)
  let end = Math.min(total, start + 5)
  if (end - start < 5) start = Math.max(1, end - 5)
  const pages = []
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

onMounted(async () => {
  await refreshCategories()
  if (CategoryState.value) {
    await refreshWalls()
  }
})
</script>

<template>
  <div class="app-container">
    <TopBar/>
    <div class="main-layout">
      <div class="left-panel">
        <ProfileCard/>
      </div>
      <div class="right-panel">
        <!--分类栏-->
        <div class="card" v-if="dataLoaded">
          <div class="title-bar">
            <h2>山中专栏</h2>
            <p>寻墨问山</p>
          </div>
          <div class="category-scroll">
            <OptionSelect
                v-for="cat of Categories"
                :key="cat.id"
                :title="cat.name"
                :subtitle="cat.description || catDefault.description"
                :selected="cat.id === CategoryId"
                @click="selectCategory(cat.id)"
            />
          </div>
        </div>

        <!--工具栏: 分页 | 排序方式-->
        <div class="toolbar" v-if="dataList.length > 0">
          <div class="pagination-left" v-if="page">
            <button class="page-btn" @click="goPrevPage" :disabled="page <= 1">上一页</button>
            <button
                v-for="page in visiblePages"
                :key="page"
                class="page-num"
                :class="{ active: page === page }"
                :disabled="page === 0 || totalPages === 0"
                @click="goToPage(page)"
            >{{ page }}</button>
            <button class="page-btn" @click="goNextPage" :disabled="page >= totalPages">下一页</button>
          </div>
          <div class="sort-right">
            <button
                class="sort-btn"
                :class="{ active: sortType === 'latest' }"
                @click="changeSort('latest')"
            >最新</button>
            <button
                class="sort-btn"
                :class="{ active: sortType === 'popular' }"
                @click="changeSort('popular')"
            >最多浏览</button>
          </div>
        </div>
        <div class="title-text" v-else-if="dataLoaded">此处空空如也~</div>

        <Waterfall
            style="width: 100%; background-color: transparent"
            :width="400"
            :animationDelay="0"
            :list="dataList"
            :gutter="16"
            :has-around-gutter="false"
            :breakpoints="{
              1200: { rowPerView: 3 },
              800: { rowPerView: 2 },
              500: { rowPerView: 1 }
            }"
        >
          <template #default="{ item }">
            <div class="message-card">
              <div class="message-content">{{ item.trimmedContent }}</div>
              <div class="card-footer">
                <span class="author-name" @click="">记于 {{ item.authorName }}</span>
                <button class="like-btn" @click="">心许 {{ item.likes }}</button>
              </div>
            </div>
          </template>
        </Waterfall>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "../style/page_left_right.css";
@import "../style/profile_card_common.css";
.category-scroll {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  gap: 12px;
  padding-bottom: 6px;
  scrollbar-width: thin;
  scrollbar-color: var(--border) var(--code-bg);
}
.category-scroll::-webkit-scrollbar {
  height: 4px;
}
.category-scroll::-webkit-scrollbar-track {
  background: var(--code-bg);
  border-radius: 4px;
}
.category-scroll::-webkit-scrollbar-thumb {
  background: var(--border);
  border-radius: 4px;
}


.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
  flex-wrap: wrap;
}
.pagination-left {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}
.sort-right {
  display: flex;
  gap: 12px;
  justify-content: end;
}

.page-btn, .page-num, .sort-btn {
  background: var(--button-bg);
  border: 1px solid var(--button-border);
  border-radius: 30px;
  padding: 6px 14px;
  font-size: 14px;
  color: var(--text);
  cursor: pointer;
  transition: all 0.2s;
}
.page-btn:hover:not(:disabled),
.page-num:hover,
.sort-btn:hover {
  background: var(--button-hover-bg);
  border-color: var(--button-hover-border);
  color: var(--text-h);
}
.page-num.active,
.sort-btn.active {
  background: var(--button-hover-bg);
  border-color: var(--text-h);
  color: var(--text-h);
  font-weight: bold;
}
.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}


.message-card {
  background-color: var(--bg);
  border: 1px solid var(--border);
  border-radius: 24px;
  padding: 16px;
  margin-bottom: 12px;
  transition: all 0.2s;
}
.message-card:hover {
  border-color: var(--button-hover-border);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

.message-content {
  font-size: 15px;
  line-height: 1.5;
  color: var(--text);
  margin-bottom: 12px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px dashed var(--border);
  padding-top: 10px;
  font-size: 13px;
  color: var(--text);
}

.author-name {
  cursor: pointer;
  opacity: 0.8;
  transition: opacity 0.2s;
}
.author-name:hover {
  opacity: 1;
  color: var(--text-h);
}

.like-btn {
  background: var(--button-bg);
  border: 1px solid var(--button-border);
  border-radius: 30px;
  padding: 4px 12px;
  font-size: 12px;
  color: var(--text);
  cursor: pointer;
  transition: all 0.2s;
}
.like-btn:hover {
  background: var(--button-hover-bg);
  border-color: var(--button-hover-border);
  color: var(--text-h);
  transform: translateY(-1px);
}
.like-btn:active {
  background: var(--button-active-bg);
  border-color: var(--button-active-border);
  transform: translateY(0);
}
</style>