<template>
  <section class="guide-card">
    <h3>{{ title }}</h3>
    <ol>
      <li v-for="step in steps" :key="step" v-html="step" />
    </ol>
  </section>
</template>

<script setup lang="ts">
defineProps({
  title: {
    type: String,
    required: true,
  },
  steps: {
    type: Array as () => string[],
    required: true,
  },
})
</script>
