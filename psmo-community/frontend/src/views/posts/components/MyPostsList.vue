<template>
  <div class="posts-section">
    <div v-for="post in posts" :key="post.id" class="post-card">
      <div class="post-header">
        <span class="category-badge">{{ post.category }}</span>
        <span class="post-date">{{ post.date }}</span>
      </div>
      <h3 class="post-title">{{ post.title }}</h3>
      <p class="post-preview">{{ post.content }}</p>
      <div class="post-stats">
        <span>👁️ {{ post.views }}</span>
        <span>💬 {{ post.comments }}</span>
        <span>❤️ {{ post.likes }}</span>
      </div>
      <div class="post-actions">
        <button class="btn-detail" type="button" @click="emit('action', { type: 'detail', post })">
          상세보기
        </button>
        <button class="btn-edit" type="button" @click="emit('action', { type: 'edit', post })">
          수정
        </button>
        <button class="btn-delete" type="button" @click="emit('action', { type: 'delete', post })">
          삭제
        </button>
      </div>
    </div>

    <div v-if="posts.length === 0" class="empty-state">
      <p>작성한 글이 없습니다</p>
      <button class="btn-create" type="button" @click="emit('action', { type: 'create' })">
        글 작성하기
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { MyPost, PostActionPayload } from '@/views/posts/constants'

defineProps<{ posts: MyPost[] }>()

const emit = defineEmits<{ (e: 'action', payload: PostActionPayload): void }>()
</script>
