<script setup lang="ts">
import type { Product } from '@/services/market'
import { useRouter } from 'vue-router'

const props = defineProps<{
  product: Product
}>()

const router = useRouter()

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('ko-KR').format(price) + '원'
}
</script>

<template>
  <div class="product-card" @click="router.push(`/market/${product.id}`)">
    <div class="image-placeholder">
      <span>No Image</span>
    </div>
    <div class="info">
      <h3 class="title">{{ product.title }}</h3>
      <div class="price">{{ formatPrice(product.price) }}</div>
      <div class="meta">
        <span class="category">{{ product.category }}</span>
        <span class="divider">|</span>
        <span class="seller">{{ product.seller.displayName || product.seller.username }}</span>
      </div>
      <div class="status-badge" :class="product.status.toLowerCase()">{{ product.status }}</div>
    </div>
  </div>
</template>

<style scoped>
.product-card {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s ease;
  background: white;
  display: flex;
  flex-direction: column;
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.image-placeholder {
  height: 150px;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 0.9rem;
}

.info {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #333;
  line-height: 1.4;
  /* Multi-line truncation */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.price {
  font-size: 1.1rem;
  font-weight: 700;
  color: #2196f3; /* Primary color */
  margin-bottom: 8px;
}

.meta {
  font-size: 0.8rem;
  color: #757575;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}

.divider {
  margin: 0 6px;
  color: #ddd;
}

.status-badge {
  align-self: flex-start;
  font-size: 0.75rem;
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: 600;
}
.status-badge.sale {
  background-color: #e3f2fd;
  color: #1976d2;
}
.status-badge.reserved {
  background-color: #fff3e0;
  color: #f57c00;
}
.status-badge.sold {
  background-color: #eeeeee;
  color: #616161;
}
</style>
