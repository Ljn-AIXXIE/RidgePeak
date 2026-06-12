<script setup lang="ts">
import {onMounted, ref} from "vue";
import {AuthState, Default, UserId} from "../../stores/auth.ts";
import UIUtils from "../../utils/UIUtils.ts";
import api from "../../api";
import type {PostDetail} from "../../stores/post.ts";
import {goHome, goPostEdit, goUserProfile} from "../../route/router.ts";
import Avatar from "../profile/Avatar.vue";
import {DText} from "../../stores/defaultValue.ts";

const props = defineProps<{
  postId: number;
}>()

const isLiked = ref(false)
const isAuthor = ref(false)
const wallDetail = ref<PostDetail>()

async function likePost() {
  const toggledValue: boolean = !isLiked.value;
  const result = await api.likeWall(props.postId, toggledValue)
  UIUtils.info(result.message)
  if (result.success) {
    isLiked.value = toggledValue
    if (wallDetail.value) {
      wallDetail.value.likeCount += toggledValue ? 1 : -1
    }
  }
}

async function deletePost() {
  const result = await api.deleteWall(props.postId)
  UIUtils.info(result.message)
  if (!result.success) return
  goHome()
}

async function update() {
  const resultD = await api.getWallDetail(props.postId)
  const resultL = await api.getLikedWall(props.postId)
  if (!resultD.success) {
    UIUtils.info(resultD.message)
    return
  }
  if (!resultL.success) {
    UIUtils.info(resultD.message)
    return
  }

  isLiked.value = resultL.like as boolean
  isAuthor.value = AuthState && UserId.value === resultD.data?.author.userId
  wallDetail.value = resultD.data
}

onMounted(async () => {
  await update()
})
</script>

<template>
  <div class="card">
    <template v-if="wallDetail !== undefined">
      <div class="title-bar">
        <h2 style="font-size: 32px">{{ wallDetail.title }}</h2>
        <button class="cancel-btn" @click="deletePost" v-if="isAuthor">删除</button>
        <button class="common-btn" @click="goPostEdit(postId)" v-if="isAuthor">编辑</button>
      </div>
      <div class="meta">
        <div class="author-info">
          <Avatar
              :size="32"
              :url="wallDetail.author.avatarUrl"
              :text="(wallDetail.author.nickname || Default.NickName).charAt(0)"
              @click="goUserProfile(wallDetail.author.userId)"
              style="cursor: pointer"
          />
          <span class="author-name" @click="goUserProfile(wallDetail.author.userId)">{{ wallDetail.author.nickname }}</span>
          <span class="meta-sep">|</span>
          <span class="post-time">{{ new Date(wallDetail.createdAt).toLocaleString() }}</span>
        </div>
        <div class="stats">
          <span class="view-count">浏览: {{ wallDetail.viewCount }}</span>
          <span class="category-tag">{{ wallDetail.category.name }}</span>
        </div>
      </div>
      <div class="post-content">{{ wallDetail.content }}</div>
      <div class="action-bar">
        <button class="like-btn" :class="{ liked: isLiked }" @click="likePost">
          {{ DText.STAR }} {{ wallDetail.likeCount }}
        </button>
      </div>
      <div class="update-time">
        最后编辑于 {{ new Date(wallDetail.updatedAt).toLocaleString() }}
      </div>
    </template>

  </div>
</template>

<style scoped>
@import "../../style/profile_card_common.css";
.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  padding-bottom: 16px;
  border-bottom: 1px dashed var(--border);
  margin-bottom: 20px;
}
.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.author-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-h);
  white-space: pre-wrap;
  word-break: break-all;
  cursor: pointer
}
.meta-sep {
  color: var(--border);
}
.post-time, .view-count, .category-tag {
  font-size: 12px;
  color: var(--text);
  opacity: 0.7;
}
.category-tag {
  background: var(--code-bg);
  padding: 2px 10px;
  border-radius: 30px;
  margin-left: 8px;
}
.post-content {
  font-size: 16px;
  line-height: 1.7;
  color: var(--text);
  white-space: pre-wrap;
  word-break: break-all;
  margin: 20px 0;
}
.action-bar {
  margin-top: 16px;
  text-align: center;
}
.like-btn {
  background: var(--button-bg);
  border: 1px solid var(--button-border);
  border-radius: 40px;
  padding: 6px 20px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
}
.like-btn:hover {
  background: var(--button-hover-bg);
  border-color: var(--button-hover-border);
  transform: scale(1.02);
}
.like-btn.liked {
  background: #6a4c4c;
  color: #ffcdcd;
}
.update-time {
  margin-top: 16px;
  font-size: 12px;
  color: var(--text);
  opacity: 0.5;
  text-align: right;
}
</style>