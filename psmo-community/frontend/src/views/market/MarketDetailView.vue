<script setup lang="ts">
import { marketService } from '@/services/market'
import { useQuery } from '@tanstack/vue-query'
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const id = Number(route.params.id)

const { data: response, isLoading } = useQuery({
  queryKey: ['product', id],
  queryFn: () => marketService.getProduct(id),
})

const product = computed(() => response.value?.data)
const activeImageIndex = ref(0)

const realEstate = computed(() => product.value?.realEstate)
const images = computed(() => product.value?.images || [])
</script>

<template>
  <div class="detail-view">
    <div v-if="isLoading">Loading...</div>
    <div v-else-if="product">
      <!-- Media Gallery -->
      <div v-if="images.length > 0" class="gallery">
        <div class="main-image">
          <video
            v-if="images[activeImageIndex]?.type === 'VIDEO'"
            controls
            :src="images[activeImageIndex]?.url"
            class="media-content"
          ></video>
          <img v-else :src="images[activeImageIndex]?.url" class="media-content" />
        </div>
        <div class="thumbnails">
          <div
            v-for="(img, idx) in images"
            :key="img.id"
            class="thumbnail"
            :class="{ active: idx === activeImageIndex }"
            @click="activeImageIndex = idx"
          >
            <span v-if="img.type === 'VIDEO'" class="video-badge">▶</span>
            <img :src="img.url" />
          </div>
        </div>
      </div>

      <h1>{{ product.title }}</h1>
      <p class="price" v-if="product.category === 'PC_BUSINESS'">
        권리금: {{ product.price.toLocaleString() }}원
      </p>
      <p class="price" v-else>{{ product.price.toLocaleString() }}원</p>

      <div class="meta">
        Category: {{ product.category }} | Seller: {{ product.seller.displayName }} | Views:
        {{ product.viewCount }}
      </div>

      <!-- Real Estate Details -->
      <div v-if="realEstate" class="real-estate-info">
        <h3>매장 정보</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">지역</span>
            <span class="value"
              >{{ realEstate?.locationCity }} {{ realEstate?.locationDistrict }}</span
            >
          </div>
          <div class="info-item">
            <span class="label">PC 대수</span>
            <span class="value">{{ realEstate?.pcCount }}대</span>
          </div>
          <div class="info-item">
            <span class="label">보증금</span>
            <span class="value">{{ realEstate?.deposit.toLocaleString() }}원</span>
          </div>
          <div class="info-item">
            <span class="label">임대료</span>
            <span class="value">{{ realEstate?.monthlyRent.toLocaleString() }}원</span>
          </div>
          <div class="info-item">
            <span class="label">관리비</span>
            <span class="value">{{ realEstate?.managementFee.toLocaleString() }}원</span>
          </div>
          <div class="info-item">
            <span class="label">월매출</span>
            <span class="value">{{ realEstate?.averageMonthlyRevenue.toLocaleString() }}원</span>
          </div>
          <div class="info-item" v-if="realEstate?.floor">
            <span class="label">층수</span>
            <span class="value">{{ realEstate?.floor }}층</span>
          </div>
          <div class="info-item" v-if="realEstate?.areaMeters">
            <span class="label">면적</span>
            <span class="value">{{ realEstate?.areaMeters }}m²</span>
          </div>
        </div>
      </div>

      <div class="description">
        {{ product.description }}
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
.gallery {
  margin-bottom: 24px;
}
.main-image {
  width: 100%;
  height: 400px;
  background: #000;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  border-radius: 12px;
}
.media-content {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}
.thumbnails {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  overflow-x: auto;
  padding-bottom: 4px;
}
.thumbnail {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  position: relative;
}
.thumbnail.active {
  border-color: #2196f3;
}
.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.video-badge {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 20px;
  text-shadow: 0 0 4px rgba(0, 0, 0, 0.5);
  z-index: 10;
}

.price {
  font-size: 1.8rem;
  font-weight: bold;
  color: #2196f3;
  margin: 10px 0;
}
.real-estate-info {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  margin: 20px 0;
  border: 1px solid #e9ecef;
}
.real-estate-info h3 {
  margin: 0 0 16px 0;
  font-size: 1.2rem;
  color: #495057;
}
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}
.info-item {
  display: flex;
  flex-direction: column;
}
.label {
  font-size: 0.85rem;
  color: #868e96;
  margin-bottom: 4px;
}
.value {
  font-size: 1.1rem;
  font-weight: 600;
}

.description {
  margin-top: 24px;
  white-space: pre-wrap;
  line-height: 1.6;
  font-size: 1.05rem;
  color: #333;
}
</style>
