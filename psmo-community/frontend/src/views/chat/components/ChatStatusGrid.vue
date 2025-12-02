<template>
  <section class="status-grid">
    <article v-for="card in cards" :key="card.label" class="status-card">
      <div>
        <p class="label">{{ card.label }}</p>
        <p class="value" :class="card.valueClass">{{ card.value }}</p>
      </div>
      <p v-if="card.description" class="desc">{{ card.description }}</p>
    </article>
  </section>
</template>

<script setup lang="ts">
export type StatusCard = {
  label: string
  value: string
  valueClass?: string
  description?: string
}

defineProps<{ cards: StatusCard[] }>()
</script>
