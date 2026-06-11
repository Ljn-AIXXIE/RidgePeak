<script setup lang="ts">
import TopBar from "./TopBar.vue";
import { app, userInf } from "../stores/defaultValue.ts";
import ProfileCard from "./ProfileCard.vue";
import SelfInfCard from "./profile/SelfInfCard.vue";
import AccountInfCard from "./profile/AccountInfCard.vue";
</script>

<template>
  <div class="app-container">
    <TopBar :title="app+'·'+userInf"/>

    <div class="main-layout">
      <div class="left-panel">
        <ProfileCard/>
      </div>

      <div class="right-panel">
        <SelfInfCard/>
        <AccountInfCard/>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app-container {
  width: 100%;
  min-height: 100vh;
  height: fit-content;
  background-color: var(--bg);
  display: flex;
  flex-direction: column;
}

.main-layout {
  display: flex;
  flex: 1;
  padding: 24px 32px;
  gap: 32px;
  flex-wrap: wrap;
}

.left-panel {
  flex: 1;
  min-width: 260px;
  max-width: 320px;
  height: 100%;
}
.right-panel {
  flex: 3;
  min-width: 280px;
  height: 100%;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 28px;
}
</style>