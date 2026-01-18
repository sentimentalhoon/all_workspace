<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import {
  useMarket,
  type Product,
  type ProductStatus,
} from "~/composables/useMarket";
import { useAuthStore } from "~/stores/auth";

const route = useRoute();
const router = useRouter();
const { fetchProductById, deleteProduct, updateProductStatus } = useMarket();
const authStore = useAuthStore();

const product = ref<Product | null>(null);
const loading = ref(true);
const activeImageIndex = ref(0);

const isOwner = computed(() => {
  return product.value?.seller.id === authStore.user?.id;
});

const isAdmin = computed(() => {
  return authStore.user?.role === "ADMIN";
});

const handleDelete = async () => {
  if (!confirm("정말 삭제하시겠습니까?")) return;
  if (!product.value) return;

  try {
    await deleteProduct(product.value.id);
    alert("삭제되었습니다.");
    router.push("/market");
  } catch (e: any) {
    alert("삭제 실패: " + (e.response?.data?.message || e.message));
  }
};

const handleStatusChange = async (status: ProductStatus) => {
  if (!confirm(`${status} 상태로 변경하시겠습니까?`)) return;
  if (!product.value) return;

  try {
    const res = await updateProductStatus(product.value.id, status);
    product.value.status = res.newStatus;
    alert("상태가 변경되었습니다.");
  } catch (e: any) {
    alert("변경 실패: " + (e.response?.data?.message || e.message));
  }
};

onMounted(async () => {
  const id = route.params.id as string;
  try {
    const res = await fetchProductById(id);
    product.value = res.data;
  } catch (e) {
    alert("상품을 불러오지 못했습니다.");
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div v-if="loading" class="loading">Loading...</div>
  <div v-else-if="!product" class="error">존재하지 않는 상품입니다.</div>

  <div v-else class="page-container">
    <!-- Image Gallery -->
    <div class="gallery-section">
      <div class="main-image">
        <img
          v-if="product.images[activeImageIndex]?.url"
          :src="product.images[activeImageIndex]?.url"
          alt="Main Product Image"
        />
        <div v-else class="no-image-placeholder">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="1.5"
            stroke="currentColor"
            class="icon"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M2.25 15.75l5.159-5.159a2.25 2.25 0 013.182 0l5.159 5.159m-1.5-1.5l1.409-1.409a2.25 2.25 0 013.182 0l2.909 2.909m-18 3.75h16.5a1.5 1.5 0 001.5-1.5V6a1.5 1.5 0 00-1.5-1.5H3.75A1.5 1.5 0 002.25 6v12a1.5 1.5 0 001.5 1.5zm10.5-11.25h.008v.008h-.008V8.25zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z"
            />
          </svg>
        </div>
      </div>
      <div v-if="product.images.length > 1" class="thumbnails">
        <div
          v-for="(img, idx) in product.images"
          :key="img.id"
          class="thumb"
          :class="{ active: idx === activeImageIndex }"
          @click="activeImageIndex = idx"
        >
          <img :src="img.url" />
        </div>
      </div>
    </div>

    <!-- Product Info -->
    <div class="info-section">
      <div class="header">
        <span class="category-badge">{{
          product.category === "PC_BUSINESS" ? "매장 매매" : product.category
        }}</span>
        <h1 class="title">{{ product.title }}</h1>
        <div class="meta">
          <span>작성자: {{ product.seller.displayName }}</span>
          <span>{{ new Date(product.createdAt).toLocaleDateString() }}</span>
          <span>조회 {{ product.viewCount }}</span>
        </div>
      </div>

      <div class="price-box">
        <span class="label">{{
          product.category === "PC_BUSINESS" ? "권리금" : "판매 가격"
        }}</span>
        <span class="value">{{ product.price.toLocaleString() }}원</span>
      </div>

      <!-- Real Estate Details -->
      <div
        v-if="product.category === 'PC_BUSINESS' && product.realEstate"
        class="detail-grid"
      >
        <div class="grid-item">
          <span class="label">보증금</span>
          <span class="val"
            >{{ product.realEstate.deposit.toLocaleString() }}원</span
          >
        </div>
        <div class="grid-item">
          <span class="label">월세</span>
          <span class="val"
            >{{ product.realEstate.monthlyRent.toLocaleString() }}원</span
          >
        </div>
        <div class="grid-item">
          <span class="label">관리비</span>
          <span class="val"
            >{{ product.realEstate.managementFee.toLocaleString() }}원</span
          >
        </div>
        <div class="grid-item">
          <span class="label">PC 대수</span>
          <span class="val">{{ product.realEstate.pcCount }}대</span>
        </div>
        <div class="grid-item">
          <span class="label">평균 월매출</span>
          <span class="val"
            >{{
              product.realEstate?.averageMonthlyRevenue.toLocaleString()
            }}원</span
          >
        </div>
        <div class="grid-item">
          <span class="label">층수 / 면적</span>
          <span class="val"
            >{{ product.realEstate?.floor }}층 /
            {{ product.realEstate?.areaMeters }}㎡ ({{
              product.realEstate?.areaPyeong
            }}평)</span
          >
        </div>
        <div class="grid-item">
          <span class="label">권리금</span>
          <span class="val"
            >{{ product.realEstate?.rightsMoney.toLocaleString() }}원</span
          >
        </div>
        <div class="grid-item">
          <span class="label">입주 가능일</span>
          <span class="val">{{ product.realEstate?.moveInDate || "-" }}</span>
        </div>
        <div class="grid-item">
          <span class="label">허가 여부</span>
          <span class="val">{{ product.realEstate?.permitStatus || "-" }}</span>
        </div>
        <div class="grid-item">
          <span class="label">행정처분 이력</span>
          <span class="val">{{
            product.realEstate?.adminActionHistory || "없음"
          }}</span>
        </div>
        <div class="grid-item full-width">
          <span class="label">시설 정보</span>
          <span class="val pre-wrap">{{
            product.realEstate?.facilities || "내용 없음"
          }}</span>
        </div>
        <div class="grid-item full-width">
          <span class="label">연락처</span>
          <span class="val highlight">{{
            product.realEstate?.contactNumber
          }}</span>
        </div>
        <div class="grid-item full-width">
          <span class="label">위치</span>
          <span class="val"
            >{{ product.realEstate?.locationCity }}
            {{ product.realEstate?.locationDistrict }}</span
          >
        </div>
      </div>

      <div class="description">
        <h3>상세 내용</h3>
        <p>{{ product.description }}</p>
      </div>

      <div class="action-bar-wrapper">
        <div
          v-if="product.status === 'PENDING' && isAdmin"
          class="admin-actions"
        >
          <p class="admin-notice">📢 관리자 승인 대기 중인 상품입니다.</p>
          <button @click="handleStatusChange('SALE')" class="approve-btn">
            승인 (공개)
          </button>
          <button @click="handleStatusChange('DELETED')" class="reject-btn">
            반려 (삭제)
          </button>
        </div>

        <div class="action-bar">
          <!-- Owner Actions -->
          <template v-if="isOwner">
            <button @click="handleDelete" class="delete-btn">삭제</button>
            <button
              v-if="product.status === 'SALE'"
              @click="handleStatusChange('SOLD')"
              class="sold-btn"
            >
              판매 완료 처리
            </button>
            <button
              v-else-if="product.status === 'SOLD'"
              class="sold-btn disabled"
              disabled
            >
              판매 완료됨
            </button>
          </template>

          <!-- Contact Buttons -->
          <a
            :href="`tel:${product.realEstate?.contactNumber}`"
            v-if="product.realEstate?.contactNumber"
            class="call-btn"
            >📞 전화하기</a
          >
          <button class="chat-btn">💬 채팅하기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: white;
  min-height: 100vh;
}

.gallery-section {
  margin-bottom: 30px;
}

.main-image {
  width: 100%;
  height: 400px;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-image img {
  max-width: 100%;
  max-height: 100%;
}

.no-image-placeholder {
  width: 100%;
  height: 100%;
  background: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.no-image-placeholder .icon {
  width: 64px;
  height: 64px;
}

.thumbnails {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  overflow-x: auto;
}

.thumb {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
}

.thumb.active {
  border-color: #e94560;
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.header {
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
  margin-bottom: 20px;
}

.category-badge {
  background: #f0f0f0;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  color: #555;
}

.title {
  margin: 10px 0;
  font-size: 1.5rem;
  color: #333;
}

.meta {
  color: #888;
  font-size: 0.9rem;
  display: flex;
  gap: 15px;
}

.price-box {
  margin-bottom: 30px;
}

.price-box .value {
  font-size: 1.8rem;
  font-weight: bold;
  color: #e94560;
  display: block;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  background: #f9f9f9;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 30px;
}

.grid-item {
  display: flex;
  flex-direction: column;
}

.grid-item.full-width {
  grid-column: span 2;
}

.grid-item .label {
  font-size: 0.8rem;
  color: #888;
}

.grid-item .val {
  font-weight: bold;
  color: #333;
}

.description h3 {
  margin-bottom: 15px;
}

.description p {
  line-height: 1.6;
  color: #444;
  white-space: pre-line;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px;
  background: white;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
  justify-content: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.action-bar button {
  flex: 1;
  max-width: 300px;
  padding: 15px;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
}

.chat-btn {
  background: #eee;
  color: #333;
}

.call-btn {
  background: #16213e;
  color: #c5a059;
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-bar-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.admin-actions {
  background: #fff3cd;
  padding: 10px;
  text-align: center;
  border-top: 1px solid #ffeeba;
  display: flex;
  gap: 10px;
  justify-content: center;
  align-items: center;
}

.admin-notice {
  margin: 0;
  font-weight: bold;
  color: #856404;
}

.approve-btn {
  background: #28a745;
  color: white;
  padding: 5px 15px;
  border-radius: 4px;
}

.reject-btn {
  background: #dc3545;
  color: white;
  padding: 5px 15px;
  border-radius: 4px;
}

.delete-btn {
  background: #dc3545;
  color: white;
}

.sold-btn {
  background: #6c757d;
  color: white;
}

.sold-btn.disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.pre-wrap {
  white-space: pre-wrap;
}

.val.highlight {
  color: #e94560;
  font-size: 1.1rem;
}

@media (min-width: 800px) {
  .page-container {
    margin-top: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}
</style>
