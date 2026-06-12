<script setup lang="ts">
import { ref, computed } from "vue"
import type { Comment } from "../../stores/comment.ts"
import {AuthState, Default, UserId} from "../../stores/auth"
import UIUtils from "../../utils/UIUtils.ts";
import Avatar from "../profile/Avatar.vue";
import {goUserProfile} from "../../route/router.ts";

const props = defineProps<{
  comment: Comment
}>()

const emit = defineEmits<{
  reply: [parentId: number, content: string]
  delete: [commentId: number]
}>()

const showReplyInput = ref(false)
const replyContent = ref("")
const isDeleting = ref(false)

const canDelete = computed(() => {
  return AuthState.value && props.comment.author.userId === UserId.value
})

function submitReply() {
  if (!replyContent.value.trim()) {
    UIUtils.info("请填写回复内容")
    return
  }
  emit("reply", props.comment.commentId, replyContent.value.trim())
  replyContent.value = ""
  showReplyInput.value = false
}

function deleteComment() {
  if (!confirm("确定删除这条评论吗？")) return
  isDeleting.value = true
  emit("delete", props.comment.commentId)
}
</script>

<template>
  <div class="comment-item">
    <Avatar
        :size="40"
        :url="comment.author.avatarUrl"
        :text="(comment.author.nickname || Default.NickName).charAt(0)"
        @click="goUserProfile(comment.author.userId)"
        style="cursor: pointer"
    />
    <div class="comment-body">
      <div class="comment-meta">
        <span class="comment-author" @click="goUserProfile(comment.author.userId)">{{ comment.author.nickname || Default.NickName }}</span>
        <span class="comment-time">{{ new Date(comment.createdAt).toLocaleString() }}</span>
        <div class="comment-actions">
          <button class="action-btn reply-btn" @click="showReplyInput = !showReplyInput">回复</button>
          <button
              v-if="canDelete"
              class="action-btn delete-btn"
              @click="deleteComment"
              :disabled="isDeleting"
          >删除</button>
        </div>
      </div>
      <div class="comment-content">{{ comment.content }}</div>

      <div v-if="showReplyInput" class="reply-form">
        <textarea
            style="width: 100%"
            class="info-textarea"
            v-model="replyContent"
            rows="2"
            placeholder="写下你的回复..."
        ></textarea>
        <div class="reply-buttons">
          <button @click="submitReply">发表回复</button>
          <button @click="showReplyInput = false">取消</button>
        </div>
      </div>

      <CommentItem
          v-if="comment.children && comment.children.length"
          v-for="child in comment.children"
          :key="child.commentId"
          :comment="child"
          @reply="(parentId, content) => $emit('reply', parentId, content)"
          @delete="(commentId) => $emit('delete', commentId)"/>
    </div>
  </div>
</template>

<style scoped>
@import "../../style/profile_card_common.css";

.comment-item {
  display: flex;
  gap: 12px;
  padding-top: 8px;
}
.comment-item + .comment-item {
  border-top: 1px solid var(--border);
}
.comment-item .comment-item {
  border-top: 1px dashed var(--border);
}

.comment-body {
  flex: 1;
}
.comment-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}
.comment-author {
  font-weight: 600;
  color: var(--text-h);
  cursor: pointer
}
.comment-time {
  font-size: 11px;
  color: var(--text);
  opacity: 0.6;
}
.comment-actions {
  margin-left: auto;
}

.action-btn {
  background: none;
  border: none;
  color: var(--text);
  font-size: 12px;
  cursor: pointer;
  margin-left: 8px;
  padding: 2px 6px;
}
.action-btn:hover {
  color: var(--text-h);
}
.delete-btn:hover {
  color: #d47a7a;
}

.comment-content {
  font-size: 14px;
  color: var(--text);
  line-height: 1.4;
  margin-bottom: 8px;
  white-space: pre-wrap;
}

.reply-buttons {
  margin: 8px 0;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
.reply-buttons button {
  background: var(--button-bg);
  border: 1px solid var(--button-border);
  border-radius: 30px;
  padding: 4px 12px;
  font-size: 12px;
  cursor: pointer;
}
.reply-buttons button:hover {
  background: var(--button-hover-bg);
  border: 1px solid var(--button-hover-border);
  border-radius: 30px;
  padding: 4px 12px;
  font-size: 12px;
  cursor: pointer;
}
</style>