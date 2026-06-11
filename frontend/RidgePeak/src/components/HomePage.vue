<script setup lang="ts">
import "vue-waterfall-plugin-next/dist/style.css";
import { ref } from 'vue'
import { Waterfall } from 'vue-waterfall-plugin-next'
import TopBar from "./common/TopBar.vue";
import {goToUserProfile} from "../route/router.ts";
import ProfileCard from "./common/ProfileCard.vue";

const waterfallList = ref([
  { id: 1, content: '横看侧看，你都是我四季里唯一的风景。你的眉眼如远山含黛，每一次对视都让我坠入诗卷。', author: '山间客', likes: 128, userId: '1' },
  { id: 2, content: '我翻越无数山丘，才懂远近高低各不同——你是我唯一的峰顶，也是我甘愿沉溺的谷底。', author: '云深不知处', likes: 96, userId: '3' },
  { id: 3, content: '不识爱之真面目，只缘身在情山中。遇见你之后，庐山烟雨也逊色，人间万象皆温柔。', author: '庐岳散人', likes: 213, userId: '4' },
  { id: 4, content: '暗夜如墨，你是我唯一的银辉。横看是温柔，侧看成永恒。庐山烟雨朦胧，不如你眉间月色。', author: '夜行客', likes: 156, userId: '5' },
  { id: 5, content: '此身已在情山中，远近高低皆是你的一颦一笑。星沉海底，云过山巅，唯你如心间长明灯。', author: '西林旧客', likes: 203, userId: '6' },
  { id: 6, content: '不识庐山真面目，只缘身在此山中。深夜读诗，行行是你；抬头望月，月月是你。', author: '庐岳晚钟', likes: 312, userId: '7' },
  { id: 6, content: '不识庐山真面目，只缘身在此山中。深夜读诗，行行是你；抬头望月，月月是你。', author: '庐岳晚钟', likes: 312, userId: '8' },
  { id: 6, content: '不识庐山真面目，只缘身在此山中。深夜读诗，行行是你；抬头望月，月月是你。', author: '庐岳晚钟', likes: 312, userId: '9' }
])

function likeMessage(id: number) {
  const item = waterfallList.value.find(i => i.id === id)
  if (item) item.likes += 1
}
</script>

<template>
  <div class="app-container">
    <TopBar/>
    <div class="main-layout">
      <div class="left-panel">
        <ProfileCard/>
      </div>
      <div class="right-panel">
        <Waterfall
            style="width: 100%; background-color: transparent"
            :width="400"
            :animationDelay="0"
            :list="waterfallList"
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
              <div class="message-content">{{ item.content }}</div>
              <div class="card-footer">
                <span class="author-name" @click="goToUserProfile(item.userId)">记于 {{ item.author }}</span>
                <button class="like-btn" @click="likeMessage(item.id)">心许 {{ item.likes }}</button>
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