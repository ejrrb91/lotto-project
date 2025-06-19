<script setup>
import { RouterView } from 'vue-router'
import Header from '@/components/Header.vue'
</script>

<template>
  <Header />
  <RouterView />
</template>

<style>
body {
  margin: 0;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

main {
  padding: 20px;
}
</style>
