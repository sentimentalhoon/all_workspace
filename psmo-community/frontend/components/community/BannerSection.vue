<template>
  <div class="banner-section">
    <div class="section-header">
      <h3>Premium Affiliates</h3>
      <button v-if="isAdmin" @click="openAdminModal" class="admin-btn">
        관리
      </button>
    </div>

    <div class="banner-list">
      <a
        v-for="banner in banners"
        :key="banner.id"
        :href="banner.linkUrl || '#'"
        target="_blank"
        class="banner-item"
      >
        <img :src="banner.imageUrl" :alt="banner.title" />
      </a>
      <div v-if="banners.length === 0" class="no-banner">
        제휴 배너가 없습니다.
      </div>
    </div>

    <!-- Admin Modal (Simple implementation) -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content glass-panel">
        <h3>배너 관리</h3>
        <div class="upload-form">
          <input v-model="newBanner.title" placeholder="업체명" />
          <input v-model="newBanner.linkUrl" placeholder="링크 URL" />
          <input type="file" @change="handleFileChange" accept="image/*" />
          <button @click="uploadBanner" :disabled="uploading">
            {{ uploading ? "등록 중..." : "등록" }}
          </button>
        </div>

        <ul class="admin-list">
          <li v-for="banner in allBanners" :key="banner.id">
            <span>{{ banner.title }} (Order: {{ banner.orderIndex }})</span>
            <button @click="removeBanner(banner.id)" class="delete-btn">
              삭제
            </button>
          </li>
        </ul>
        <button @click="closeModal" class="close-btn">닫기</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useBanner, type Banner } from "~/composables/useBanner";
import { useAuthStore } from "~/stores/auth"; // Assuming store exists

const { user } = useAuthStore();
const isAdmin = computed(() => user?.role === "ADMIN");

const { fetchBanners, createBanner, deleteBanner } = useBanner();
const banners = ref<Banner[]>([]);
const allBanners = ref<Banner[]>([]); // For admin view including hidden? API handles it differently?
// Actually API /banners returns visible. /banners/admin returns all.

const showModal = ref(false);
const uploading = ref(false);
const newBanner = ref({
  title: "",
  linkUrl: "",
  image: null as File | null,
});

const loadBanners = async () => {
  banners.value = await fetchBanners(false);
  if (isAdmin.value) {
    allBanners.value = await fetchBanners(true);
  }
};

const openAdminModal = async () => {
  if (!isAdmin.value) return;
  allBanners.value = await fetchBanners(true);
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    newBanner.value.image = target.files[0];
  }
};

const uploadBanner = async () => {
  if (!newBanner.value.image || !newBanner.value.title) {
    alert("이미지와 업체명은 필수입니다.");
    return;
  }
  uploading.value = true;
  try {
    await createBanner(
      {
        title: newBanner.value.title,
        linkUrl: newBanner.value.linkUrl,
        isVisible: true,
        orderIndex: allBanners.value.length + 1,
      },
      newBanner.value.image,
    );
    await loadBanners();
    newBanner.value = { title: "", linkUrl: "", image: null }; // Reset
    alert("등록되었습니다.");
  } catch (e) {
    alert("등록 실패");
  } finally {
    uploading.value = false;
  }
};

const removeBanner = async (id: number) => {
  if (!confirm("삭제하시겠습니까?")) return;
  try {
    await deleteBanner(id);
    await loadBanners();
  } catch (e) {
    alert("삭제 실패");
  }
};

onMounted(() => {
  loadBanners();
});
</script>

<style scoped lang="scss">
@use "~/assets/scss/variables" as *;
@use "~/assets/scss/main" as *;

.banner-section {
  margin-bottom: 24px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    h3 {
      font-size: 1.1rem;
      font-weight: 700;
      color: $text-primary;
    }

    .admin-btn {
      font-size: 0.8rem;
      padding: 4px 8px;
      background: rgba($primary, 0.2);
      color: $primary;
      border-radius: 4px;
    }
  }

  .banner-list {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .banner-item {
      display: block;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      transition: transform 0.2s;

      &:hover {
        transform: translateY(-2px);
      }

      img {
        width: 100%;
        height: auto;
        display: block;
      }
    }

    .no-banner {
      text-align: center;
      padding: 20px;
      color: $text-secondary;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 12px;
    }
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;

  .modal-content {
    width: 90%;
    max-width: 400px;
    padding: 24px;
    border-radius: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;

    h3 {
      text-align: center;
      color: $primary;
    }

    .upload-form {
      display: flex;
      flex-direction: column;
      gap: 8px;

      input {
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.2);
        padding: 10px;
        border-radius: 8px;
        color: white;

        &::placeholder {
          color: $text-disabled;
        }
      }

      button {
        @include glass-button;
        width: 100%;
        padding: 10px;
      }
    }

    .admin-list {
      max-height: 200px;
      overflow-y: auto;
      list-style: none;
      padding: 0;
      margin: 0;

      li {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);

        span {
          font-size: 0.9rem;
          color: $text-primary;
        }

        .delete-btn {
          color: $danger;
          font-size: 0.8rem;
        }
      }
    }

    .close-btn {
      margin-top: 10px;
      background: transparent;
      border: 1px solid $text-disabled;
      color: $text-secondary;
      padding: 8px;
      border-radius: 8px;
    }
  }
}
</style>
