<script setup lang="ts">
import { onMounted, ref } from "vue"
import UIUtils from "../../utils/UIUtils.ts"
import type { Comment } from "../../stores/comment.ts"
import api from "../../api"
import CommentItem from "./CommentItem.vue"

const props = defineProps<{
  postId: number
}>()

const comments = ref<Comment[]>([])
const newComment = ref("")
const loading = ref(false)

async function getComments() {
  const result = await api.getWallComment(props.postId)
  if (!result.success) {
    UIUtils.error(result.message)
    return
  }
  comments.value = result.data || []
}

async function postRootComment() {
  if (!newComment.value.trim()) {
    UIUtils.info("请填写评论内容")
    return
  }
  loading.value = true
  const result = await api.createWallComment(props.postId, newComment.value)
  loading.value = false
  if (!result.success) {
    UIUtils.error(result.message)
    return
  }
  UIUtils.info("评论发表成功")
  newComment.value = ""
  await getComments()
}

async function handleReply(parentId: number, content: string) {
  const result = await api.createWallComment(props.postId, content, parentId)
  if (!result.success) {
    UIUtils.error(result.message)
    return false
  }
  UIUtils.info("回复成功")
  await getComments()
  return true
}

async function handleDelete(commentId: number) {
  const result = await api.deleteWallComment(commentId)
  if (!result.success) {
    UIUtils.error(result.message)
    return false
  }
  UIUtils.info("删除成功")
  await getComments()
  return true
}

onMounted(() => {
  getComments()
})
</script>

<template>
  <div class="card">
    <div class="title-bar">
      <h2>墨痕回响 · {{ comments?.length || 0 }} 条</h2>
      <button class="submit-btn" @click="postRootComment" :disabled="loading">
        {{ loading ? "发表中..." : "留墨" }}
      </button>
    </div>
    <textarea
        style="width: 100%"
        v-model="newComment"
        class="info-textarea"
        rows="3"
        placeholder="写下你的感触……"
    />
    <div class="comment-list">
      <CommentItem
          v-for="comment in comments"
          :key="comment.commentId"
          :comment="comment"
          @reply="handleReply"
          @delete="handleDelete"
      />
    </div>
  </div>
</template>

<style scoped>
@import "../../style/profile_card_common.css";

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}
</style>