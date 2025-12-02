<template>
  <section class="app-section">
    <div class="promo-copy">
      <h3>{{ title }}</h3>
      <p>{{ description }}</p>
      <div class="download-buttons">
        <a class="download android" :href="androidLink" target="_blank" rel="noreferrer">
          Android
        </a>
        <a class="download ios" :href="iosLink" target="_blank" rel="noreferrer"> iOS </a>
      </div>
    </div>
    <div class="promo-visual" aria-hidden="true">{{ visual }}</div>
  </section>
</template>

<script setup lang="ts">
defineProps({
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    required: true,
  },
  androidLink: {
    type: String,
    required: true,
  },
  iosLink: {
    type: String,
    required: true,
  },
  visual: {
    type: String,
    default: '📱',
  },
})
</script>
