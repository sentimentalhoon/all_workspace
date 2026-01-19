<script setup lang="ts">
import { useAuthStore } from "~/stores/auth";

const authStore = useAuthStore();
const { navigateTo } = useRouter();

const handleLogout = async () => {
  await authStore.logout();
  navigateTo("/login");
};
</script>

<template>
  <div class="page-container">
    <!-- Profile Card -->
    <section class="profile-card glass-panel" v-if="authStore.user">
      <div class="profile-top">
        <div class="avatar-wrapper">
          <div
            v-if="authStore.user.images && authStore.user.images.length > 0"
            class="user-avatar"
          >
            <img :src="authStore.user.images[0]?.thumbnailUrl" alt="profile" />
          </div>
          <div v-else class="user-avatar placeholder">
            <span>👤</span>
          </div>
        </div>
        <div class="user-info">
          <h2 class="username">
            {{ authStore.user.displayName }}
            <span class="role-badge">{{ authStore.user.role }}</span>
          </h2>
          <div class="score-display">
            <span class="label">보유 포인트</span>
            <strong class="value gold-text"
              >{{ authStore.user.score?.toLocaleString() }} P</strong
            >
          </div>
        </div>
      </div>
    </section>

    <!-- Menu Section -->
    <nav class="menu-list glass-panel">
      <!-- Edit Profile -->
      <div class="menu-item" @click="navigateTo('/my/edit')">
        <div class="icon-box">✏️</div>
        <span>내 정보 수정</span>
        <div class="arrow">›</div>
      </div>

      <!-- Settings (Mock) -->
      <div class="menu-item">
        <div class="icon-box">⚙️</div>
        <span>설정</span>
        <div class="arrow">›</div>
      </div>

      <!-- Logout -->
      <div class="menu-item logout" @click="handleLogout">
        <div class="icon-box">🚪</div>
        <span>로그아웃</span>
      </div>
    </nav>
  </div>
</template>

<style scoped lang="scss">
@use "sass:color";
@use "~/assets/scss/variables" as *;

.page-container {
  padding-bottom: 40px;
  animation: fadeIn 0.5s ease;
}

/* --- Profile Card --- */
.profile-card {
  padding: 24px;
  margin-bottom: 24px;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.08) 0%,
    rgba(255, 255, 255, 0.02) 100%
  );

  .profile-top {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .avatar-wrapper {
    .user-avatar {
      width: 70px;
      height: 70px;
      border-radius: 20px;
      background: #333;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      border: 2px solid rgba(255, 255, 255, 0.1);
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);

      &.placeholder {
        background: linear-gradient(135deg, #444 0%, #222 100%);
        font-size: 1.8rem;
      }

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }

  .user-info {
    flex: 1;

    .username {
      margin: 0 0 8px 0;
      font-size: 1.3rem;
      font-weight: 700;
      color: white;
      display: flex;
      align-items: center;
      gap: 8px;

      .role-badge {
        font-size: 0.7rem;
        padding: 2px 8px;
        background: rgba(30, 136, 229, 0.2);
        color: #64b5f6;
        border-radius: 12px;
        border: 1px solid rgba(30, 136, 229, 0.3);
      }
    }

    .score-display {
      display: flex;
      flex-direction: column;

      .label {
        font-size: 0.8rem;
        color: $text-secondary;
      }
      .value {
        font-size: 1.4rem;
        font-weight: 700;
      }
    }
  }
}

/* --- Menu List --- */
.menu-list {
  padding: 0;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 18px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  cursor: pointer;
  transition: background 0.2s;
  color: $text-primary;
  font-size: 1rem;
  font-weight: 500;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: rgba(255, 255, 255, 0.05);
  }

  .icon-box {
    margin-right: 16px;
    font-size: 1.2rem;
    width: 24px;
    text-align: center;
  }

  .arrow {
    margin-left: auto;
    color: rgba(255, 255, 255, 0.3);
    font-size: 1.2rem;
  }

  &.logout {
    color: #ff6b6b;
    .icon-box {
      filter: grayscale(0.2);
    }
    &:hover {
      background: rgba(233, 69, 96, 0.1);
    }
  }
}
</style>
