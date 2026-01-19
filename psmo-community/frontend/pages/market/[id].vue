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
  <div v-if="loading" class="loading-state">
    <div class="spinner"></div>
    <p>정보를 불러오는 중...</p>
  </div>
  <div v-else-if="!product" class="error-state">
    <p>🙅‍♂️ 존재하지 않거나 삭제된 상품입니다.</p>
    <button @click="router.push('/market')">목록으로 돌아가기</button>
  </div>

  <div v-else class="page-container fade-in">
    <!-- Image Gallery -->
    <div class="gallery-section glass-panel">
      <div class="main-image">
        <img
          v-if="product.images[activeImageIndex]?.imageUrl"
          :src="product.images[activeImageIndex]?.imageUrl"
          alt="Main Product Image"
          class="zoom-img"
        />
        <div v-else class="no-image-placeholder">
          <span class="icon">📷</span>
          <span>이미지가 없습니다</span>
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
          <img :src="img.thumbnailUrl" loading="lazy" />
        </div>
      </div>
    </div>

    <!-- Product Info -->
    <div class="info-section glass-panel">
      <div class="header">
        <div class="top-row">
          <span class="category-badge">{{
            product.category === "PC_BUSINESS" ? "매장 매매" : product.category
          }}</span>
          <span class="status-badge" :class="product.status">{{
            product.status
          }}</span>
        </div>

        <h1 class="title">{{ product.title }}</h1>
        <div class="meta">
          <span>🧢 {{ product.seller.displayName }}</span>
          <span>📅 {{ new Date(product.createdAt).toLocaleDateString() }}</span>
          <span>👀 {{ product.viewCount }}</span>
        </div>
      </div>

      <div class="price-box">
        <span class="label">{{
          product.category === "PC_BUSINESS" ? "권리금" : "판매 가격"
        }}</span>
        <span class="value"
          >{{ product.price.toLocaleString() }} <small>원</small></span
        >
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
          <span class="val">
            {{ product.realEstate?.floor }}층 /
            {{ product.realEstate?.areaMeters }}㎡
            <span class="sub-text"
              >({{ product.realEstate?.areaPyeong }}평)</span
            >
          </span>
        </div>
        <div class="grid-item">
          <span class="label">권리금</span>
          <span class="val highlight"
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
          <span class="val highlight-gold">{{
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

      <div class="action-spacer"></div>
    </div>

    <!-- Bottom Floating Action Bar -->
    <div class="action-bar-wrapper">
      <div
        v-if="product.status === 'PENDING' && isAdmin"
        class="admin-actions glass-panel-sm"
      >
        <p class="admin-notice">📢 관리자 승인 대기 중</p>
        <div class="btn-group">
          <button @click="handleStatusChange('SALE')" class="approve-btn">
            승인 (공개)
          </button>
          <button @click="handleStatusChange('DELETED')" class="reject-btn">
            반려 (삭제)
          </button>
        </div>
      </div>

      <div class="action-bar glass-panel">
        <!-- Owner or Admin Actions -->
        <template v-if="isOwner || isAdmin">
          <button @click="handleDelete" class="delete-btn">삭제</button>

          <button
            @click="router.push(`/market/create?id=${product.id}`)"
            class="edit-btn"
          >
            수정
          </button>

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

        <template v-else>
          <!-- Contact Buttons -->
          <button class="chat-btn">💬 채팅하기</button>
          <a
            :href="`tel:${product.realEstate?.contactNumber}`"
            v-if="
              product.category === 'PC_BUSINESS' &&
              product.realEstate?.contactNumber
            "
            class="call-btn"
            >📞 전화하기</a
          >
          <a href="#" v-else class="call-btn disabled">📞 연락처 없음</a>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
/* --- Variables --- */
$color-bg-dark: #121212;
$color-primary: #1e88e5;
$color-accent: #c5a059;
$color-danger: #e94560;
$glass-bg: rgba(255, 255, 255, 0.05);
$glass-border: rgba(255, 255, 255, 0.1);
$text-primary: #ffffff;
$text-secondary: #b0b0b0;

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding-bottom: 60px;
}

/* Glass Panel */
.glass-panel {
  background: $glass-bg;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid $glass-border;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.glass-panel-sm {
  background: rgba(255, 243, 205, 0.9);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 193, 7, 0.3);
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
}

/* Gallery */
.gallery-section {
  padding: 16px;

  .main-image {
    width: 100%;
    height: 350px;
    background: #000;
    border-radius: 16px;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid rgba(255, 255, 255, 0.1);

    .zoom-img {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
  }

  .thumbnails {
    display: flex;
    gap: 12px;
    margin-top: 16px;
    overflow-x: auto;
    padding-bottom: 4px;

    .thumb {
      flex: 0 0 70px;
      height: 70px;
      border-radius: 10px;
      overflow: hidden;
      cursor: pointer;
      border: 2px solid transparent;
      opacity: 0.6;
      transition: all 0.2s;

      &.active {
        border-color: $color-accent;
        opacity: 1;
      }
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
}

.no-image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #666;
  gap: 8px;
  .icon {
    font-size: 2rem;
  }
}

/* Info Section */
.info-section {
  .header {
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding-bottom: 20px;
    margin-bottom: 20px;

    .top-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;

      .category-badge {
        background: rgba(255, 255, 255, 0.1);
        padding: 4px 10px;
        border-radius: 6px;
        font-size: 0.85rem;
        color: $text-secondary;
      }

      .status-badge {
        font-size: 0.85rem;
        font-weight: bold;
        padding: 4px 8px;
        border-radius: 4px;

        &.PENDING {
          color: #ffc107;
          background: rgba(255, 193, 7, 0.1);
        }
        &.SOLD {
          color: #aaa;
          background: rgba(255, 255, 255, 0.1);
        }
        &.SALE {
          color: #28a745;
          background: rgba(40, 167, 69, 0.1);
        }
      }
    }

    .title {
      margin: 0 0 12px;
      font-size: 1.6rem;
      color: $text-primary;
    }

    .meta {
      color: #666;
      font-size: 0.9rem;
      display: flex;
      flex-wrap: wrap;
      gap: 15px;
    }
  }

  .price-box {
    margin-bottom: 30px;

    .label {
      display: block;
      font-size: 0.9rem;
      color: $color-accent;
      margin-bottom: 4px;
    }
    .value {
      font-size: 2rem;
      font-weight: 800;
      background: linear-gradient(90deg, #fff, #ddd);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      small {
        font-size: 1.2rem;
        margin-left: 4px;
      }
    }
  }
}

/* Detail Grid */
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  background: rgba(0, 0, 0, 0.2); /* Darker inner background */
  padding: 24px;
  border-radius: 16px;
  margin-bottom: 30px;
  border: 1px solid rgba(255, 255, 255, 0.05);

  .grid-item {
    display: flex;
    flex-direction: column;

    &.full-width {
      grid-column: span 2;
    }

    .label {
      font-size: 0.85rem;
      color: $text-secondary;
      margin-bottom: 4px;
    }

    .val {
      font-size: 1rem;
      font-weight: 600;
      color: $text-primary;

      &.highlight {
        color: $color-danger;
      }
      &.highlight-gold {
        color: $color-accent;
        font-size: 1.1rem;
      }
      .sub-text {
        font-size: 0.85rem;
        color: #666;
        font-weight: normal;
      }
    }

    .pre-wrap {
      white-space: pre-wrap;
      line-height: 1.5;
      color: #ddd;
    }
  }
}

.description {
  h3 {
    color: $text-primary;
    margin-bottom: 15px;
    border-left: 3px solid $color-accent;
    padding-left: 10px;
  }
  p {
    line-height: 1.8;
    color: #ddd;
    white-space: pre-line;
    font-size: 1.05rem;
  }
}

/* Action Bar */
.action-bar-wrapper {
  position: fixed;
  bottom: 10px; /* 조금 더 아래로 내림 */
  left: 50%;
  transform: translateX(-50%);
  width: 95%;
  max-width: 760px;
  z-index: 100;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.admin-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;

  .admin-notice {
    margin: 0;
    font-weight: bold;
    color: #856404;
    font-size: 0.9rem;
  }
  .btn-group {
    display: flex;
    gap: 10px;
    width: 100%;
  }

  button {
    flex: 1;
    padding: 8px;
    border-radius: 8px;
    border: none;
    font-weight: bold;
    cursor: pointer;
  }
  .approve-btn {
    background: #28a745;
    color: white;
  }
  .reject-btn {
    background: #dc3545;
    color: white;
  }
}

.action-bar {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  margin: 0; /* Override glass-panel margin */
  background: rgba(22, 33, 62, 0.9); /* Darker glass for better contrast */
  align-items: center;
  gap: 12px;

  button,
  a {
    flex: 1;
    height: 50px;
    border: none;
    border-radius: 12px;
    font-weight: bold;
    font-size: 1rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    text-decoration: none;
    transition: transform 0.2s;

    &:active {
      transform: scale(0.97);
    }
  }

  .chat-btn {
    background: rgba(255, 255, 255, 0.1);
    color: white;
    border: 1px solid rgba(255, 255, 255, 0.2);
  }
  .call-btn {
    background: $color-accent;
    color: #000;
    &.disabled {
      background: #555;
      color: #888;
      cursor: not-allowed;
    }
  }
  .delete-btn {
    background: rgba(233, 69, 96, 0.2);
    color: $color-danger;
    border: 1px solid $color-danger;
  }
  .edit-btn {
    background: rgba(30, 136, 229, 0.2);
    color: $color-primary;
    border: 1px solid $color-primary;
  }
  .sold-btn {
    background: $text-secondary;
    color: white;
  }
}

/* Loading/Error */
.loading-state,
.error-state {
  text-align: center;
  padding: 100px 0;
  color: $text-secondary;

  button {
    margin-top: 20px;
    padding: 10px 20px;
    background: $color-primary;
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
  }
}
.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(255, 255, 255, 0.1);
  border-top-color: $color-accent;
  border-radius: 50%;
  margin: 0 auto 20px;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@media (min-width: 800px) {
  .page-container {
    padding-top: 40px;
  }
}
</style>
