<script setup lang="ts">
const { fetchClient } = useApiClient();

const {
  data: products,
  pending,
  error,
} = await useAsyncData("products", () => fetchClient("/market/products"));

const goToCreate = () => {
  navigateTo("/market/create");
};
</script>

<template>
  <div class="market-page">
    <div class="header">
      <h2>PC Market</h2>
      <button @click="goToCreate" class="create-btn">+ Sell Item</button>
    </div>

    <div v-if="pending" class="loading">Loading items...</div>
    <div v-else-if="error" class="error">Failed to load items</div>

    <div v-else class="product-grid">
      <div v-if="!products?.data || products.data.length === 0" class="empty">
        No items found.
      </div>
      <div
        v-else
        v-for="item in products.data"
        :key="item.id"
        class="product-card"
        @click="navigateTo(`/market/${item.id}`)"
      >
        <div class="image-area">
          <img
            v-if="item.images && item.images.length > 0"
            :src="item.images[0].url"
          />
          <div v-else class="placeholder">No Image</div>
        </div>
        <div class="info">
          <h3>{{ item.title }}</h3>
          <div class="price">{{ item.price.toLocaleString() }} Won</div>
          <div class="meta">
            <span>{{ item.category }}</span>
            <span>Views: {{ item.viewCount }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.market-page {
  padding-bottom: 60px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.create-btn {
  background: #2196f3;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: bold;
  cursor: pointer;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 15px;
}

.product-card {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  cursor: pointer;
}

.image-area {
  height: 120px;
  background: #eee;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #999;
}
.image-area img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info {
  padding: 10px;
}
.info h3 {
  margin: 0 0 5px 0;
  font-size: 0.9rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.price {
  font-weight: bold;
  color: #333;
}
.meta {
  font-size: 0.7rem;
  color: #888;
  margin-top: 5px;
  display: flex;
  justify-content: space-between;
}
</style>
