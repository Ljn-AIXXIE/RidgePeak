<script setup lang="ts">
import TopBar from './common/TopBar.vue'
import ProfileCard from './common/ProfileCard.vue'
import PostDetailCard from "./post_detail_card/PostDetailCard.vue";
import {useRoute} from "vue-router";
import PostCommentCard from "./post_detail_card/PostCommentCard.vue";

const postId = Number(useRoute().params.postId!)
</script>

<template>
  <div class="app-container">
    <TopBar />
    <div class="main-layout">
      <div class="left-panel">
        <ProfileCard :show-back="true"/>
      </div>
      <div class="right-panel">
        <PostDetailCard :post-id="postId"/>
        <PostCommentCard :post-id="postId"/>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "../style/page_left_right.css";
</style>