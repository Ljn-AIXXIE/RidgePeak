<script setup lang="ts">
import TopBar from "./common/TopBar.vue";
import { app, userInf } from "../stores/defaultValue.ts";
import ProfileCard from "./common/ProfileCard.vue";
import SelfInfCard from "./profile/SelfInfCard.vue";
import AccountInfCard from "./profile/AccountInfCard.vue";
</script>

<template>
  <div class="app-container">
    <TopBar :title="app+'·'+userInf"/>

    <div class="main-layout">
      <div class="left-panel">
        <ProfileCard :show-back="true"/>
      </div>

      <div class="right-panel">
        <SelfInfCard/>
        <AccountInfCard/>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "../style/page_left_right.css";
</style>