<script setup lang="ts">
import { ref } from 'vue'
import TopBar from './common/TopBar.vue'
import ProfileCard from './common/ProfileCard.vue'
import UIUtils from '../utils/UIUtils'
import MulSelector from "./select/MulSelector.vue";
import {Categories, CategoryId, CategoryState, Default} from "../stores/category.ts";
import type {FCategoryIdType} from "../stores/post.ts";
import api from "../api";
import {goPostDetail} from "../route/router.ts";
import OptionSelect from "./select/OptionSelect.vue";

const thisCategoryId = ref(CategoryId.value)
const title = ref('')
const content = ref('')
const submitting = ref(false)

async function selectCategory(newCategoryId: FCategoryIdType) {
  if (thisCategoryId.value === newCategoryId) thisCategoryId.value = undefined
  else thisCategoryId.value = newCategoryId
}

async function submitWall() {
  if (!thisCategoryId.value) {
    UIUtils.info('请选择板块')
    return
  }
  if (!title.value.trim()) {
    UIUtils.info('请填写标题')
    return
  }
  if (!content.value.trim()) {
    UIUtils.info('请填写表白内容')
    return
  }
  if (title.value.length > 100) {
    UIUtils.info('标题过长')
    return
  }
  submitting.value = true

  const result = await api.createWall(thisCategoryId.value, title.value, content.value)
  UIUtils.info(result.message)

  if (!result.success) return
  thisCategoryId.value = undefined
  title.value = ''
  content.value = ''
  submitting.value = false

  goPostDetail(result.postId as number)
}
</script>

<template>
  <div class="app-container">
    <TopBar />
    <div class="main-layout">
      <div class="left-panel">
        <ProfileCard :show-back="true"/>
      </div>
      <div class="right-panel">

        <div class="card">
          <div class="title-bar">
            <h2>题壁 · 留墨</h2>
            <button class="submit-btn" @click="submitWall" :disabled="submitting || !thisCategoryId || !title || !content">
              {{ submitting ? '发布中...' : '题壁寄情' }}
            </button>
          </div>
          <div class="info-col">
            <label class="info-label">板块</label>
            <MulSelector
                :list="Categories"
                :id="thisCategoryId"
                :value-able="CategoryState"
                :show-title="false"
                @select="selectCategory"
            >
              <template #option="{ item, isSelected, select }">
                <OptionSelect
                    :title="item.name"
                    :subtitle="item.description || Default.description"
                    :selected="isSelected"
                    @click="select"
                />
              </template>
            </MulSelector>
          </div>
          <div class="info-col">
            <label class="info-label">标题</label>
            <input
                type="text"
                class="info-input"
                v-model="title"
                placeholder="山月不知心底事"
            />
          </div>
          <div class="info-col">
            <label class="info-label">内容</label>
            <textarea
                class="info-textarea"
                v-model="content"
                rows="8"
                placeholder="横看成岭侧成峰，远近高低皆是你..."
            />
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
@import "../style/page_left_right.css";
@import "../style/profile_card_common.css";
</style>