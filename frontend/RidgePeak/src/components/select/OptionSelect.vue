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
  <div class="option-card" :class="{ active: props.selected }">
    <template v-if="!onlySelfDesign">
      <div class="option-name">{{ props.title }}</div>
      <div class="option-desc">{{ props.subtitle }}</div>
    </template>
    <slot/>
  </div>
</template>

<style scoped>
.option-card {
  flex: 0 0 auto;
  min-width: 60px;
  max-width: 80%;
  background: var(--code-bg);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 8px 12px;
  margin-top: 2px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.option-card:hover {
  background: var(--button-hover-bg);
  border-color: var(--button-hover-border);
  transform: translateY(-2px);
}
.option-card.active {
  background: var(--button-hover-bg);
  border-color: var(--button-hover-border);
}
.option-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-h);
  margin-bottom: 4px;
  letter-spacing: 1px;
}
.option-desc {
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