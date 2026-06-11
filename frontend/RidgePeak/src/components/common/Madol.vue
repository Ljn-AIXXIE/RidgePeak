<script setup lang="ts">
const props = withDefaults(defineProps<{
  show?: boolean
  outClose?: boolean
}>(), {
  show: false,
  outClose: true
})
const emit = defineEmits<{
  (e: 'update:show', value: boolean): void
}>()
const close = () => {
  props.outClose && emit('update:show', false)
}
</script>

<template>
  <Transition name="modal">
    <div v-if="props.show" class="modal-overlay" @click.self="close">
      <slot />
    </div>
  </Transition>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;

  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(3px);
  opacity: 1;
  transform: scale3d(1, 1, 1) translateY(0);
}
.modal-enter-active,
.modal-leave-active {
  transition: all 0.2s cubic-bezier(0.2, 0.9, 0.4, 1.1);
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale3d(0.96, 0.96, 1) translateY(20px);
  backdrop-filter: blur(0);
  background: rgba(0, 0, 0, 0);
}
</style>