<script setup lang="ts">
import ProductCard from '@/components/market/ProductCard.vue'
import { marketService } from '@/services/market'
import { useMarketStore } from '@/stores/market'
import { useQuery } from '@tanstack/vue-query'
import { storeToRefs } from 'pinia'

const marketStore = useMarketStore()
const { categoryFilter } = storeToRefs(marketStore)

const {
  data: products,
  isLoading,
  error,
} = useQuery({
  queryKey: ['products', categoryFilter],
  queryFn: () => marketService.getProducts(1, 20, categoryFilter.value),
})
</script>

<template>
  <div class="market-view">
    <header class="page-header">
      <h1>💻 PC Market</h1>
      <p>PC방 매매, 중고 부품, 주변기기 거래</p>

      <div class="actions">
        <!-- TODO: Add Category Tabs here -->
        <button class="btn-primary" @click="$router.push('/market/create')">➕ Sell Item</button>
      </div>
    </header>

    <div v-if="isLoading" class="loading">Loading products...</div>
    <div v-else-if="error" class="error">Failed to load products.</div>

    <div v-else class="product-grid">
      <div v-if="products?.data?.length === 0" class="empty-state">
        No items found. Be the first to sell!
      </div>
      <ProductCard v-else v-for="product in products?.data" :key="product.id" :product="product" />
    </div>
  </div>
</template>

<style scoped>
.market-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 16px;
}

.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-header h1 {
  font-size: 2rem;
  margin-bottom: 8px;
  color: #1a1a1a;
}

.page-header p {
  color: #666;
  margin-bottom: 24px;
}

.actions {
  display: flex;
  justify-content: flex-end;
}

.btn-primary {
  background-color: #2196f3;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-primary:hover {
  background-color: #1976d2;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 24px;
}

.loading,
.error,
.empty-state {
  text-align: center;
  padding: 40px;
  color: #888;
}
</style>
