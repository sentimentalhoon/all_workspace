<script setup lang="ts">
const authStore = useAuthStore();

// 시간대에 따른 인사말 생성
const greetingMessage = computed(() => {
  const hour = new Date().getHours();
  if (hour < 12) return "활기찬 오전 되세요!";
  if (hour < 18) return "오후도 힘내세요!";
  return "오늘 하루도 고생 많으셨습니다.";
});
</script>

<template>
  <section class="hero-section glass-panel">
    <div class="hero-content">
      <div class="greeting-box">
        <span class="sub-greeting">
          <ClientOnly fallback="반갑습니다!">{{ greetingMessage }}</ClientOnly>
        </span>
        <h1 class="username">
          <span class="highlight">
            <ClientOnly fallback="Guest">{{
              authStore.user?.displayName || "Guest"
            }}</ClientOnly>
          </span>
          사장님
        </h1>
      </div>
      <div class="stats-box">
        <div class="stat-item">
          <span class="label">보유 포인트</span>
          <strong class="value gold-text">
            <ClientOnly fallback="0 P"
              >{{ authStore.user?.score?.toLocaleString() || 0 }} P</ClientOnly
            >
          </strong>
        </div>
      </div>
    </div>
    <div class="hero-bg-decoration"></div>
  </section>
</template>

<style scoped lang="scss">
@use "~/assets/scss/variables" as *;

.hero-section {
  position: relative;
  padding: 30px;
  overflow: hidden;
  background: linear-gradient(
    135deg,
    rgba(22, 33, 62, 0.8) 0%,
    rgba(15, 52, 96, 0.8) 100%
  );

  .hero-content {
    position: relative;
    z-index: 2;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .sub-greeting {
    display: block;
    font-size: 0.9rem;
    color: $text-secondary;
    margin-bottom: 4px;
  }

  h1.username {
    margin: 0;
    font-size: 1.5rem;
    font-weight: 300;

    .highlight {
      font-weight: 700;
      color: $text-primary;
    }
  }

  .stats-box {
    text-align: right;

    .stat-item {
      display: flex;
      flex-direction: column;
    }

    .label {
      font-size: 0.8rem;
      color: $text-secondary;
    }
    .value {
      font-size: 1.5rem;
      font-weight: 700;
    }
  }

  .hero-bg-decoration {
    position: absolute;
    top: -50%;
    right: -10%;
    width: 300px;
    height: 300px;
    background: radial-gradient(
      circle,
      rgba(197, 160, 89, 0.15) 0%,
      transparent 70%
    );
    border-radius: 50%;
    z-index: 1;
    pointer-events: none;
  }
}

@media (max-width: 600px) {
  .hero-section {
    padding: 20px;

    .hero-content {
      flex-direction: row; /* Keep row for compact height */
      align-items: center;
      gap: 12px;

      .greeting-box {
        .sub-greeting {
          font-size: 0.8rem;
          margin-bottom: 2px;
        }
        h1.username {
          font-size: 1.2rem;
        }
      }

      .stats-box {
        width: auto;
        text-align: right;
        border-top: none;
        padding-top: 0;

        .label {
          font-size: 0.7rem;
        }
        .value {
          font-size: 1.2rem;
        }
      }
    }
  }
}
</style>
