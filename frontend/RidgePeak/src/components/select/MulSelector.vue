<script setup lang="ts">
withDefaults(defineProps<{
  list: Array<any>,
  id: number | undefined
  valueAble: boolean,
  showTitle?: boolean,
}>(), {
  showTitle: true,
})

const emit = defineEmits<{
  select: [id: number | undefined]
}>()

function selectItem(id: number | undefined) {
  emit('select', id)
}
</script>

<template>
  <div :class="showTitle ? 'select-card' : 'select-card-no-title'" v-if="valueAble">
    <div class="select-title-bar" v-if="showTitle">
      <h2>山中专栏</h2>
      <p>寻墨问山</p>
    </div>
    <div class="select-scroll">
      <slot
          name="option"
          v-for="item in list"
          :key="item.id"
          :item="item"
          :isSelected="item.id === id"
          :select="() => selectItem(item.id)"
      />
    </div>
  </div>
</template>

<style scoped>
.select-card {
  width: 100%;
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 16px 16px;
  transition: all 0.2s;
}
.select-card:hover {
  border-color: var(--button-hover-border);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}
.select-card-no-title {
  width: 100%;
}
.select-title-bar {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 8px;
  padding-bottom: 4px;
}
.select-title-bar h2 {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-h);
  margin: 0;
  letter-spacing: 1px;
}
.select-title-bar p {
  font-size: 10px;
  color: var(--text);
  opacity: 0.6;
  margin: 0;
}
.select-scroll {
  display: flex;
  flex-wrap: nowrap;
  overflow-x: auto;
  gap: 8px;
  padding-bottom: 4px;
  scrollbar-width: thin;
  scrollbar-color: var(--border) var(--code-bg);
}
.select-scroll::-webkit-scrollbar {
  height: 3px;
}
.select-scroll::-webkit-scrollbar-track {
  background: var(--code-bg);
  border-radius: 3px;
}
.select-scroll::-webkit-scrollbar-thumb {
  background: var(--border);
  border-radius: 3px;
}
</style>