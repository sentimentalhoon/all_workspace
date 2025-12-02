<template>
  <div class="comments-section">
    <div v-for="comment in comments" :key="comment.id" class="comment-card">
      <div class="comment-header">
        <span class="post-title-ref">{{ comment.postTitle }}</span>
        <span class="comment-date">{{ comment.date }}</span>
      </div>
      <p class="comment-content">{{ comment.content }}</p>
      <div class="comment-stats">
        <span>❤️ {{ comment.likes }}</span>
      </div>
      <div class="comment-actions">
        <button
          class="btn-detail"
          type="button"
          @click="emit('action', { type: 'detail', comment })"
        >
          원글 보기
        </button>
        <button
          class="btn-delete"
          type="button"
          @click="emit('action', { type: 'delete', comment })"
        >
          삭제
        </button>
      </div>
    </div>

    <div v-if="comments.length === 0" class="empty-state">
      <p>작성한 댓글이 없습니다</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { CommentActionPayload, MyComment } from '@/views/posts/constants'

defineProps<{ comments: MyComment[] }>()

const emit = defineEmits<{ (e: 'action', payload: CommentActionPayload): void }>()
</script>
