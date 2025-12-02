<template>
  <div class="saved-section">
    <div v-for="post in posts" :key="post.id" class="post-card">
      <div class="post-header">
        <span class="category-badge">{{ post.category }}</span>
        <span class="author">{{ post.author }}</span>
      </div>
      <h3 class="post-title">{{ post.title }}</h3>
      <p class="post-preview">{{ post.content }}</p>
      <div class="post-stats">
        <span>👁️ {{ post.views }}</span>
        <span>💬 {{ post.comments }}</span>
      </div>
      <div class="post-actions">
        <button class="btn-detail" type="button" @click="emit('action', { type: 'detail', post })">
          상세보기
        </button>
        <button class="btn-unsave" type="button" @click="emit('action', { type: 'unsave', post })">
          저장 취소
        </button>
      </div>
    </div>

    <div v-if="posts.length === 0" class="empty-state">
      <p>저장한 글이 없습니다</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { SavedActionPayload, SavedPost } from '@/views/posts/constants'

defineProps<{ posts: SavedPost[] }>()

const emit = defineEmits<{ (e: 'action', payload: SavedActionPayload): void }>()
</script>
