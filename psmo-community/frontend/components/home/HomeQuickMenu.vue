<script setup lang="ts">
// No script needed for static navigation
</script>

<template>
  <nav class="quick-menu">
    <NuxtLink
      to="/blacklist"
      class="menu-card glass-panel"
      title="불량 사용자 검색"
    >
      <div class="icon-box red-glow">🚫</div>
      <span class="menu-title">블랙리스트</span>
      <span class="menu-desc">불량 이용자 조회</span>
    </NuxtLink>
    <NuxtLink to="/market" class="menu-card glass-panel" title="매장 거래">
      <div class="icon-box blue-glow">🤝</div>
      <span class="menu-title">매장 거래</span>
      <span class="menu-desc">PC방 양도/양수</span>
    </NuxtLink>
    <NuxtLink
      to="/market?category=PC_FULL"
      class="menu-card glass-panel"
      title="중고 장터"
    >
      <div class="icon-box gold-glow">📦</div>
      <span class="menu-title">중고 장터</span>
      <span class="menu-desc">PC 부품/집기</span>
    </NuxtLink>
    <NuxtLink to="/community" class="menu-card glass-panel" title="커뮤니티">
      <div class="icon-box green-glow">💬</div>
      <span class="menu-title">소통방</span>
      <span class="menu-desc">운영 노하우</span>
    </NuxtLink>
  </nav>
</template>

<style scoped lang="scss">
@use "~/assets/scss/variables" as *;

.quick-menu {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;

  .menu-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    text-decoration: none;
    transition:
      transform 0.2s,
      background 0.2s;

    &:hover {
      transform: translateY(-5px);
      background: rgba(255, 255, 255, 0.1);
    }

    .icon-box {
      width: 50px;
      height: 50px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.5rem;
      margin-bottom: 12px;
    }

    .menu-title {
      font-weight: 700;
      font-size: 1rem;
      color: $text-primary;
      margin-bottom: 4px;
    }

    .menu-desc {
      font-size: 0.75rem;
      color: $text-secondary;
    }
  }
}

@media (max-width: 600px) {
  .quick-menu {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
