<script setup lang="ts">
const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
document.body.setAttribute(
    "arco-theme",
    prefersDark.valueOf() ? "dark" : "light"
)
</script>

<template>
  <RouterView/>
  <footer style="width: 100vw; height: min-content; text-align: center; z-index: 10000">
    © 2026 Ridge Peak
  </footer>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
</style>
