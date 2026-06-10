<script setup lang="ts">
import { ref } from "vue";
import { useRouter, RouterView } from "vue-router";

const router = useRouter();

const showNav = ref(
    location.pathname == "/home" ||
    location.pathname == "/publish" ||
    location.pathname == "/profile"
);
const navigationSelection = ref<string[] | null>();

switch (location.pathname) {
  case "/home":
    navigationSelection.value = ["1"];
    break;
  case "/publish":
    navigationSelection.value = ["2"];
    break;
  case "/profile":
    navigationSelection.value = ["3"];
    break;
}

const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
document.body.setAttribute(
    "arco-theme",
    prefersDark.valueOf() ? "dark" : "light"
);

router.beforeEach((to) => {
  showNav.value =
      to.path == "/home" || to.path == "/publish" || to.path == "/profile";

  switch (to.path) {
    case "/home":
      navigationSelection.value = ["1"];
      break;
    case "/publish":
      navigationSelection.value = ["2"];
      break;
    case "/profile":
      navigationSelection.value = ["3"];
      break;
  }
});
</script>

<template>
  <RouterView :style="{width: '100vw', height: '100vh'}"> </RouterView>
  <footer style="width: 100vw; height: min-content; text-align: center;">
    © 2026 Ridge Peak
  </footer>
</template>

<style scoped>
</style>
