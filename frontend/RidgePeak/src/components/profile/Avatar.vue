<script setup lang="ts">
import {computed} from "vue";
import config from "../../api/config.ts";

const props = defineProps<{
  size: number;
  url?: string;
  text?: string;
}>()
const parsedUrl = computed(() => {
  if (props.url === undefined || props.url.length === 0) return '';
  if (props.url.startsWith('/')) return config.serverHost + props.url;
  return props.url;
})

</script>

<template>
  <img v-if="parsedUrl.length !== 0" :src="parsedUrl" class="avatar" alt="avatar" :width="size" :height="size" />
  <p v-else class="avatar placeholder" :style="{width: size+'px', height: size+'px'}">{{ text }}</p>
</template>

<style scoped>
.avatar {
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid var(--border);
}
.avatar.placeholder {
  aspect-ratio: 1 / 1;
  background-color: var(--code-bg);
  display: flex;
  align-items: center !important;
  justify-content: center !important;
  font-size: 40px;
  color: var(--border);
}
</style>