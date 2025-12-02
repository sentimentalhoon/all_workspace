<template>
  <section class="admin-section">
    <h3 class="section-title">{{ title }}</h3>
    <div class="section-body">
      <slot />
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{ title: string }>()
</script>
