<script setup lang="ts">
import type { BadUser } from "~/composables/useBlacklist";

defineProps<{
  users: BadUser[];
}>();
</script>

<template>
  <section class="content-section" v-if="users.length > 0">
    <div class="section-header">
      <h3>🚨 최근 불량 사용자</h3>
      <NuxtLink to="/blacklist" class="more-btn">더보기</NuxtLink>
    </div>
    <div class="list-container glass-panel">
      <div
        v-for="user in users"
        :key="user.id"
        class="list-item hover-glow"
        @click="navigateTo(`/blacklist/${user.id}`)"
      >
        <div class="user-avatar-wrapper">
          <div v-if="user.images && user.images.length > 0" class="user-avatar">
            <img :src="user.images[0]?.thumbnailUrl" alt="thumb" />
          </div>
          <div v-else class="user-avatar placeholder">
            <span>📍</span>
          </div>
        </div>

        <div class="info-col">
          <div class="top-row">
            <span class="region-badge">📍 {{ user.region }}</span>
            <span class="date">{{
              new Date(user.createdAt).toLocaleDateString()
            }}</span>
          </div>
          <div class="reason">“{{ user.reason }}”</div>
        </div>

        <div class="arrow-icon">›</div>
      </div>
    </div>
  </section>
</template>

<style scoped lang="scss">
@use "~/assets/scss/variables" as *;

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;

  h3 {
    margin: 0;
    font-size: 1.2rem;
    font-weight: 700;
    color: $text-primary;
  }

  .more-btn {
    font-size: 0.85rem;
    color: $text-secondary;
    text-decoration: none;
    transition: color 0.2s;
    &:hover {
      color: $color-primary;
    }
  }
}

.list-container {
  padding: 12px;
}

.list-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  gap: 16px;
  cursor: pointer;
  border-radius: 12px;
  transition:
    background 0.2s,
    transform 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.03);
  }

  &.hover-glow:hover {
    box-shadow: 0 0 15px rgba(233, 69, 96, 0.1);
    border-color: rgba(233, 69, 96, 0.3);
    transform: translateX(4px);
  }

  &:last-child {
    border-bottom: none;
  }

  .user-avatar-wrapper {
    position: relative;
  }

  .user-avatar {
    width: 50px;
    height: 50px;
    border-radius: 12px;
    background: #222;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    font-size: 1.2rem;
    overflow: hidden;
    border: 2px solid rgba(255, 255, 255, 0.1);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    &.placeholder {
      background: linear-gradient(135deg, #333 0%, #444 100%);
    }
  }

  .info-col {
    flex: 1;
    min-width: 0;

    .top-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 6px;
    }

    .region-badge {
      font-weight: 700;
      font-size: 0.95rem;
      color: white;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      max-width: 150px;
    }

    .date {
      font-size: 0.8rem;
      color: $text-secondary;
      flex-shrink: 0;
      margin-left: 8px;
    }

    .reason {
      font-size: 0.9rem;
      color: #ff6b6b;

      display: -webkit-box;
      -webkit-line-clamp: 1;
      line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
      white-space: normal;

      font-style: italic;
      opacity: 0.9;
    }
  }

  .arrow-icon {
    color: $text-secondary;
    font-size: 1.2rem;
    opacity: 0.5;
  }
}
</style>
