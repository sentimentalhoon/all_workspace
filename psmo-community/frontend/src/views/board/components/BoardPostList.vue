<template>
  <div class="post-list">
    <div class="write-button-container">
      <button class="btn-write" type="button">{{ writeLabel }}</button>
    </div>
    <div
      v-for="post in posts"
      :key="post.id"
      class="post-card"
      :class="{ 'tip-card': variant === 'tip' }"
    >
      <div class="post-header">
        <span v-if="variant === 'free'" :class="['category-badge', post.categoryClass]">
          {{ post.category }}
        </span>
        <span v-else-if="variant === 'game'" class="game-badge">{{ post.game }}</span>
        <span v-else class="tip-badge">💡 팁</span>
        <span class="post-author">{{ post.author }}</span>
        <span class="post-time">{{ post.time }}</span>
      </div>
      <h3 class="post-title">{{ post.title }}</h3>
      <p class="post-preview">{{ post.content }}</p>
      <div class="post-stats">
        <span>👁️ {{ post.views }}</span>
        <span>💬 {{ post.comments }}</span>
        <template v-if="variant === 'tip'">
          <span>👍 추천 {{ post.recommends }}</span>
        </template>
        <template v-else>
          <span>❤️ {{ post.likes }}</span>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { FreePost, GamePost, TipPost } from '../useBoardData'

type Variant = 'free' | 'game' | 'tip'

defineProps({
  posts: {
    type: Array as () => Array<FreePost | GamePost | TipPost>,
    required: true,
  },
  variant: {
    type: String as () => Variant,
    required: true,
  },
  writeLabel: {
    type: String,
    required: true,
  },
})
</script>
```
