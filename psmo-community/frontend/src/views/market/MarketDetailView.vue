<script setup lang="ts">
import { marketService } from '@/services/market'
import { useQuery } from '@tanstack/vue-query'
import { useRoute } from 'vue-router'

const route = useRoute()
const id = Number(route.params.id)

const { data: product, isLoading } = useQuery({
  queryKey: ['product', id],
  queryFn: () => marketService.getProduct(id),
})
</script>

<template>
  <div class="detail-view">
    <div v-if="isLoading">Loading...</div>
    <div v-else-if="product">
      <h1>{{ product.data.title }}</h1>
      <p class="price">{{ product.data.price }}원</p>
      <div class="meta">
        Category: {{ product.data.category }} | Seller: {{ product.data.seller.displayName }}
      </div>
      <div class="description">
        {{ product.data.description }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-view {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}
.price {
  font-size: 1.5rem;
  font-weight: bold;
  color: #2196f3;
}
.description {
  margin-top: 24px;
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>
