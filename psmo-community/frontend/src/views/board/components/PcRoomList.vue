<template>
  <div class="pcroom-list">
    <div class="write-button-container">
      <button class="btn-write" type="button">{{ addButtonLabel }}</button>
    </div>
    <div v-for="pc in rooms" :key="pc.id" class="pcroom-card">
      <div class="pcroom-header">
        <h3>{{ pc.name }}</h3>
        <span :class="['rating-badge', pc.ratingClass]">⭐ {{ pc.rating }}</span>
      </div>
      <div class="pcroom-info">
        <p class="location">📍 {{ pc.region }} {{ pc.address }}</p>
        <p class="specs">💻 {{ pc.specs }}</p>
        <p class="price">💰 {{ pc.price }}</p>
      </div>
      <div class="pcroom-tags">
        <span v-for="tag in pc.tags" :key="tag" class="tag">{{ tag }}</span>
      </div>
      <div class="pcroom-stats">
        <span>👁️ {{ pc.views }}</span>
        <span>💬 {{ pc.reviews }}개 리뷰</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PcRoom } from '../useBoardData'

defineProps({
  rooms: {
    type: Array as () => PcRoom[],
    required: true,
  },
  addButtonLabel: {
    type: String,
    default: '📍 피씨방 추가',
  },
})
</script>
