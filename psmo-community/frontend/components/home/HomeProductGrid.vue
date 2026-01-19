<script setup lang="ts">
import type { Product } from "~/composables/useMarket";

defineProps<{
  products: Product[];
}>();
</script>

<template>
  <section class="content-section" v-if="products.length > 0">
    <div class="section-header">
      <h3>🛒 최근 올라온 매물</h3>
      <NuxtLink to="/market" class="more-btn">더보기</NuxtLink>
    </div>
    <div class="product-grid">
      <div
        v-for="product in products"
        :key="product.id"
        class="product-card glass-panel hover-lift"
        @click="navigateTo(`/market/${product.id}`)"
      >
        <div class="product-thumb">
          <!-- Placeholder Icon used if no image -->
          <div
            v-if="!product.images || product.images.length === 0"
            class="no-image-placeholder"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
              <circle cx="8.5" cy="8.5" r="1.5"></circle>
              <polyline points="21 15 16 10 5 21"></polyline>
            </svg>
          </div>
          <img
            v-else
            :src="product.images?.[0]?.thumbnailUrl"
            alt="상품 이미지"
            loading="lazy"
          />
          <span class="status-badge" :class="product.status.toLowerCase()">{{
            product.status
          }}</span>
        </div>
        <div class="product-details">
          <h4 class="title">{{ product.title }}</h4>
          <div class="price-row">
            <span class="price">{{ product.price.toLocaleString() }}원</span>
            <span class="location">{{
              product.realEstate?.locationCity || "지역"
            }}</span>
          </div>
        </div>
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

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.product-card {
  overflow: hidden;
  cursor: pointer;

  .product-thumb {
    position: relative;
    width: 100%;
    aspect-ratio: 4 / 3;
    background: #2a2a2a;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .no-image-placeholder {
      color: #555;
    }

    .status-badge {
      position: absolute;
      top: 8px;
      left: 8px;
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 0.7rem;
      font-weight: bold;

      &.pending {
        background: rgba(255, 193, 7, 0.9);
        color: black;
      }
      &.sale {
        background: rgba(30, 136, 229, 0.9);
        color: white;
      }
      &.reserved {
        background: rgba(255, 87, 34, 0.9);
        color: white;
      }
      &.sold {
        background: rgba(158, 158, 158, 0.9);
        color: white;
      }
    }
  }

  .product-details {
    padding: 12px;

    .title {
      font-size: 0.95rem;
      font-weight: 600;
      margin: 0 0 6px 0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      color: $text-primary;
    }

    .price-row {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .price {
        font-weight: 700;
        color: $color-accent;
        font-size: 1rem;
      }
      .location {
        font-size: 0.75rem;
        color: $text-secondary;
      }
    }
  }
}
</style>
