<script setup lang="ts">
import { useMarket, type ProductCreateRequest } from "~/composables/useMarket";

const { createProduct } = useMarket();
const router = useRouter();

const form = ref<ProductCreateRequest>({
  title: "",
  description: "",
  price: 0,
  category: "PC_BUSINESS", // Default to Store Trading
  realEstate: {
    locationCity: "",
    locationDistrict: "",
    pcCount: 0,
    deposit: 0,
    monthlyRent: 0,
    managementFee: 0,
    averageMonthlyRevenue: 0,
    floor: null,
    areaMeters: null,
  },
});

const isStoreTrading = computed(() => form.value.category === "PC_BUSINESS");
const files = ref<File[]>([]);
const previewUrls = ref<string[]>([]);
const loading = ref(false);

const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files) {
    const newFiles = Array.from(target.files);
    files.value = [...files.value, ...newFiles];

    // Create previews
    newFiles.forEach((file) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        if (e.target?.result) previewUrls.value.push(e.target.result as string);
      };
      reader.readAsDataURL(file);
    });
  }
};

const removeFile = (index: number) => {
  files.value.splice(index, 1);
  previewUrls.value.splice(index, 1);
};

const submit = async () => {
  if (!form.value.title || !form.value.price) {
    alert("필수 정보를 입력해주세요.");
    return;
  }

  // If not store trading, remove realEstate
  const payload = { ...form.value };
  if (!isStoreTrading.value) {
    delete payload.realEstate;
  }

  loading.value = true;
  try {
    await createProduct(payload, files.value);
    alert("등록되었습니다.");
    router.push("/market");
  } catch (e: any) {
    alert("등록 실패: " + (e.response?.data?.message || e.message));
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="page-container">
    <div class="header">
      <h2>상품/매장 등록</h2>
    </div>

    <form @submit.prevent="submit" class="form-card">
      <!-- Category Selection -->
      <div class="form-section">
        <label>카테고리</label>
        <div class="category-toggle">
          <button
            type="button"
            :class="{ active: form.category === 'PC_BUSINESS' }"
            @click="form.category = 'PC_BUSINESS'"
          >
            🤝 매장 양도
          </button>
          <button
            type="button"
            :class="{ active: form.category !== 'PC_BUSINESS' }"
            @click="form.category = 'PC_FULL'"
          >
            📦 중고 물품
          </button>
        </div>
        <select
          v-if="form.category !== 'PC_BUSINESS'"
          v-model="form.category"
          class="sub-category"
        >
          <option value="PC_FULL">PC 본체</option>
          <option value="GPU">그래픽카드</option>
          <option value="CPU">CPU</option>
          <option value="RAM">RAM</option>
          <option value="MONITOR">모니터</option>
          <option value="ETC">기타</option>
        </select>
      </div>

      <!-- Common Fields -->
      <div class="form-section">
        <label>제목</label>
        <input v-model="form.title" placeholder="제목을 입력하세요" required />
      </div>

      <div class="form-section">
        <label>{{ isStoreTrading ? "권리금 (원)" : "판매 가격 (원)" }}</label>
        <input type="number" v-model="form.price" required />
      </div>

      <!-- Real Estate Specific Fields -->
      <div v-if="isStoreTrading" class="real-estate-grid">
        <div class="field">
          <label>지역 (시/도)</label>
          <input
            v-model="form.realEstate!.locationCity"
            placeholder="예: 서울"
            required
          />
        </div>
        <div class="field">
          <label>지역 (구/군)</label>
          <input
            v-model="form.realEstate!.locationDistrict"
            placeholder="예: 강남구"
            required
          />
        </div>
        <div class="field">
          <label>보증금 (원)</label>
          <input type="number" v-model="form.realEstate!.deposit" required />
        </div>
        <div class="field">
          <label>월세 (원)</label>
          <input
            type="number"
            v-model="form.realEstate!.monthlyRent"
            required
          />
        </div>
        <div class="field">
          <label>관리비 (원)</label>
          <input
            type="number"
            v-model="form.realEstate!.managementFee"
            required
          />
        </div>
        <div class="field">
          <label>PC 대수</label>
          <input type="number" v-model="form.realEstate!.pcCount" required />
        </div>
        <div class="field">
          <label>평균 월매출 (원)</label>
          <input
            type="number"
            v-model="form.realEstate!.averageMonthlyRevenue"
            required
          />
        </div>
      </div>

      <!-- Description -->
      <div class="form-section">
        <label>상세 설명</label>
        <textarea
          v-model="form.description"
          rows="10"
          placeholder="상세 내용을 입력하세요."
        ></textarea>
      </div>

      <!-- Image Upload -->
      <div class="form-section">
        <label>사진 첨부</label>
        <div class="file-upload">
          <input
            type="file"
            multiple
            @change="handleFileChange"
            accept="image/*"
            id="file-input"
          />
          <label for="file-input" class="upload-btn">+ 사진 추가</label>

          <div class="preview-list">
            <div
              v-for="(url, idx) in previewUrls"
              :key="idx"
              class="preview-item"
            >
              <img :src="url" />
              <button type="button" @click="removeFile(idx)">x</button>
            </div>
          </div>
        </div>
      </div>

      <button type="submit" class="submit-btn" :disabled="loading">
        {{ loading ? "등록 중..." : "등록 완료" }}
      </button>
    </form>
  </div>
</template>

<style scoped>
.page-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.form-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-section {
  margin-bottom: 25px;
}

.form-section label {
  display: block;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

input,
textarea,
select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
}

.category-toggle {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.category-toggle button {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  background: #f9f9f9;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
}

.category-toggle button.active {
  background: #16213e;
  color: #c5a059;
  border-color: #16213e;
}

.real-estate-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 25px;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
}

.file-upload {
  margin-top: 10px;
}

input[type="file"] {
  display: none;
}

.upload-btn {
  display: inline-block;
  padding: 10px 20px;
  background: #eee;
  border-radius: 8px;
  cursor: pointer;
}

.preview-list {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  overflow-x: auto;
}

.preview-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.preview-item button {
  position: absolute;
  top: -5px;
  right: -5px;
  background: red;
  color: white;
  border: none;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
}

.submit-btn {
  width: 100%;
  padding: 15px;
  background: #e94560;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s;
}

.submit-btn:hover {
  background: #d63d55;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

@media (max-width: 600px) {
  .real-estate-grid {
    grid-template-columns: 1fr;
  }
}
</style>
