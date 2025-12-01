<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <div class="mobile-container">
    <header>
      <h1>🏕️ Campstation</h1>
      <p class="subtitle">캠핑장 예약 서비스</p>
    </header>

    <RouterView />
  </div>
</template>

<style scoped>
.mobile-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem 1rem;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

header h1 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
}

.subtitle {
  margin: 0.5rem 0 0 0;
  font-size: 0.9rem;
  opacity: 0.9;
}
</style>
