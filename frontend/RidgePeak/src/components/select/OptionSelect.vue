<script setup lang="ts">
const props = withDefaults(defineProps<{
  title: string,
  subtitle: string,
  selected?: boolean,
  onlySelfDesign?: boolean
}>(), {
  selected: false,
  onlySelfDesign: false,
})
</script>

<template>
  <div class="category-card" :class="{ active: props.selected }">
    <template v-if="!onlySelfDesign">
      <div class="category-name">{{ props.title }}</div>
      <div class="category-desc">{{ props.subtitle }}</div>
    </template>
    <slot/>
  </div>
</template>

<style scoped>
.category-card {
  flex: 0 0 auto;
  width: 160px;
  background: var(--code-bg);
  border: 1px solid var(--border);
  border-radius: 20px;
  padding: 12px 14px;
  margin-top: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.category-card:hover {
  background: var(--button-hover-bg);
  border-color: var(--button-hover-border);
  transform: translateY(-2px);
}
.category-card.active {
  background: var(--button-hover-bg);
  border-color: var(--button-hover-border);
}
.category-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-h);
  margin-bottom: 6px;
  letter-spacing: 1px;
}
.category-desc {
  font-size: 12px;
  color: var(--text);
  line-height: 1.3;
  opacity: 0.8;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>