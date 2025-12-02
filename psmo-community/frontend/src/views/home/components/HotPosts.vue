<template>
  <section class="hot-section">
    <SectionHeader title="🔥 인기 게시글" link="/board" />
    <div class="post-list">
      <button
        v-for="post in posts"
        :key="post.id"
        class="post-item"
        type="button"
        @click="emit('select', post)"
      >
        <div class="post-title">{{ post.title }}</div>
        <div class="post-stats">
          <span>💬 {{ post.comments }}</span>
          <span>❤️ {{ post.likes }}</span>
        </div>
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
import SectionHeader from '@/views/home/components/SectionHeader.vue'
import type { HotPost } from '@/views/home/constants'

defineProps<{ posts: HotPost[] }>()

const emit = defineEmits<{ (e: 'select', post: HotPost): void }>()
</script>
