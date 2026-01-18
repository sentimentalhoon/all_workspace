<script setup lang="ts">
import { useImageOptimization } from "~/composables/useImageOptimization";
import { useMarket, type ProductCreateRequest } from "~/composables/useMarket";

const { createProduct } = useMarket();
const { compressImage } = useImageOptimization();
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
    rightsMoney: 0,
    floor: null,
    areaMeters: null,
    areaPyeong: null,
    facilities: "",
    moveInDate: "",
    permitStatus: "",
    adminActionHistory: "",
    contactNumber: "",
  },
});

const isStoreTrading = computed(() => form.value.category === "PC_BUSINESS");
const files = ref<File[]>([]);
const previewUrls = ref<string[]>([]);
const loading = ref(false);
const processingImages = ref(false); // processing state

const handleFileChange = async (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files) {
    const newFiles = Array.from(target.files);

    // Limit Check (Max 20 total)
    if (files.value.length + newFiles.length > 20) {
      alert("이미지는 최대 20장까지만 첨부할 수 있습니다.");
      return;
    }

    processingImages.value = true;
    try {
      for (const file of newFiles) {
        // Optimization
        const compressed = await compressImage(file, 1920, 1, 0.8);

        files.value.push(compressed);

        // Create Preview
        const reader = new FileReader();
        reader.onload = (e) => {
          if (e.target?.result)
            previewUrls.value.push(e.target.result as string);
        };
        reader.readAsDataURL(compressed);
      }
    } catch (err) {
      console.error("Image processing error", err);
      alert("이미지 처리 중 오류가 발생했습니다.");
    } finally {
      processingImages.value = false;
      // Reset input to allow re-selecting same files if needed
      target.value = "";
    }
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
  <div class="page-container fade-in">
    <div class="header">
      <h2 class="page-title">상품/매장 등록</h2>
      <p class="page-desc">새로운 매물을 등록하여 거래를 시작해보세요.</p>
    </div>

    <form @submit.prevent="submit" class="glass-panel form-card">
      <!-- Category Selection -->
      <div class="form-section">
        <label class="section-label">카테고리</label>
        <div class="category-toggle glass-pills">
          <button
            type="button"
            class="pill-btn"
            :class="{ active: form.category === 'PC_BUSINESS' }"
            @click="form.category = 'PC_BUSINESS'"
          >
            🤝 매장 양도
          </button>
          <button
            type="button"
            class="pill-btn"
            :class="{ active: form.category !== 'PC_BUSINESS' }"
            @click="form.category = 'PC_FULL'"
          >
            📦 중고 물품
          </button>
        </div>

        <div v-if="form.category !== 'PC_BUSINESS'" class="select-wrapper">
          <select v-model="form.category" class="dark-input sub-category">
            <option value="PC_FULL">PC 본체</option>
            <option value="GPU">그래픽카드</option>
            <option value="CPU">CPU</option>
            <option value="RAM">RAM</option>
            <option value="MONITOR">모니터</option>
            <option value="ETC">기타</option>
          </select>
        </div>
      </div>

      <!-- Common Fields -->
      <div class="form-section">
        <label class="section-label">기본 정보</label>
        <div class="input-group">
          <div class="field">
            <label>제목</label>
            <input
              v-model="form.title"
              placeholder="매물 제목을 입력하세요"
              required
              class="dark-input"
            />
          </div>

          <div class="field">
            <label>{{
              isStoreTrading ? "권리금 (원)" : "판매 가격 (원)"
            }}</label>
            <input
              type="number"
              v-model="form.price"
              required
              class="dark-input"
              placeholder="0"
            />
          </div>
        </div>
      </div>

      <!-- Real Estate Specific Fields -->
      <div v-if="isStoreTrading" class="real-estate-section">
        <h3 class="sub-title">매장 상세 정보</h3>
        <div class="real-estate-grid">
          <div class="field">
            <label>지역 (시/도)</label>
            <input
              v-model="form.realEstate!.locationCity"
              placeholder="예: 서울"
              required
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>지역 (구/군)</label>
            <input
              v-model="form.realEstate!.locationDistrict"
              placeholder="예: 강남구"
              required
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>보증금 (원)</label>
            <input
              type="number"
              v-model="form.realEstate!.deposit"
              required
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>월세 (원)</label>
            <input
              type="number"
              v-model="form.realEstate!.monthlyRent"
              required
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>관리비 (원)</label>
            <input
              type="number"
              v-model="form.realEstate!.managementFee"
              required
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>PC 대수</label>
            <input
              type="number"
              v-model="form.realEstate!.pcCount"
              required
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>평균 월매출 (원)</label>
            <input
              type="number"
              v-model="form.realEstate!.averageMonthlyRevenue"
              required
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>권리금 (원)</label>
            <input
              type="number"
              v-model="form.realEstate!.rightsMoney"
              required
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>평수 (평)</label>
            <input
              type="number"
              v-model="form.realEstate!.areaPyeong"
              step="0.1"
              required
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>연락처</label>
            <input
              v-model="form.realEstate!.contactNumber"
              placeholder="010-0000-0000"
              required
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>입주 가능일</label>
            <input
              v-model="form.realEstate!.moveInDate"
              placeholder="즉시 가능 / 협의"
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>허가 여부</label>
            <input
              v-model="form.realEstate!.permitStatus"
              placeholder="허가 완료 / 진행 중"
              class="dark-input"
            />
          </div>
          <div class="field full-width">
            <label>행정 처분 이력</label>
            <input
              v-model="form.realEstate!.adminActionHistory"
              placeholder="없음 / 상세 내용"
              class="dark-input"
            />
          </div>
          <div class="field full-width">
            <label>시설/집기 정보</label>
            <textarea
              v-model="form.realEstate!.facilities"
              rows="3"
              placeholder="PC 사양, 에어컨, 인테리어 상태 등"
              class="dark-input"
            ></textarea>
          </div>
          <div class="field">
            <label>층수</label>
            <input
              type="number"
              v-model="form.realEstate!.floor"
              class="dark-input"
            />
          </div>
          <div class="field">
            <label>면적 (㎡)</label>
            <input
              type="number"
              v-model="form.realEstate!.areaMeters"
              step="0.1"
              class="dark-input"
            />
          </div>
        </div>
      </div>

      <!-- Description -->
      <div class="form-section">
        <label class="section-label">상세 설명</label>
        <textarea
          v-model="form.description"
          rows="10"
          placeholder="상세 내용을 입력하세요."
          class="dark-input"
        ></textarea>
      </div>

      <!-- Image Upload -->
      <div class="form-section">
        <label class="section-label">사진 첨부 (최대 20장)</label>
        <div class="file-upload-area">
          <input
            type="file"
            multiple
            @change="handleFileChange"
            accept="image/*"
            id="file-input"
            class="hidden-input"
          />
          <label for="file-input" class="upload-box hover-glow">
            <span class="icon">📸</span>
            <span>사진 추가하기</span>
          </label>

          <div class="preview-grid">
            <div
              v-for="(url, idx) in previewUrls"
              :key="idx"
              class="preview-item"
            >
              <img :src="url" />
              <button type="button" class="remove-btn" @click="removeFile(idx)">
                ✕
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="processingImages" class="processing-msg glass-panel-sm">
        <div class="spinner-sm"></div>
        <span>이미지 최적화 중입니다... ({{ files.length }}장)</span>
      </div>

      <button
        type="submit"
        class="submit-btn hover-glow"
        :disabled="loading || processingImages"
      >
        {{
          loading
            ? "등록 중..."
            : processingImages
              ? "이미지 처리 대기 중..."
              : "등록 완료"
        }}
      </button>
    </form>
  </div>
</template>

<style scoped lang="scss">
/* --- Variables --- */
$color-primary: #1e88e5;
$color-accent: #c5a059;
$color-danger: #e94560;
$glass-bg: rgba(255, 255, 255, 0.05);
$glass-border: rgba(255, 255, 255, 0.1);
$text-primary: #ffffff;
$text-secondary: #b0b0b0;

.page-container {
  padding: 16px;
  max-width: 800px;
  margin: 0 auto;
  padding-bottom: 60px;
}

.header {
  margin-bottom: 24px;
  text-align: center;

  .page-title {
    color: $text-primary;
    margin: 0 0 8px 0;
    font-size: 1.5rem;
  }
  .page-desc {
    color: $text-secondary;
    margin: 0;
    font-size: 0.95rem;
  }
}

/* Glass Panel */
.glass-panel {
  background: $glass-bg;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid $glass-border;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.glass-pills {
  display: flex;
  background: rgba(0, 0, 0, 0.2);
  padding: 4px;
  border-radius: 12px;
  gap: 4px;

  .pill-btn {
    flex: 1;
    padding: 12px;
    border: none;
    background: transparent;
    color: $text-secondary;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.2s;

    &.active {
      background: $color-accent;
      color: #1a1a2e;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    }
  }
}

/* Form Styling */
.form-section {
  margin-bottom: 30px;

  .section-label {
    display: block;
    font-size: 1.1rem;
    font-weight: bold;
    color: $text-primary;
    margin-bottom: 12px;
    border-left: 3px solid $color-accent;
    padding-left: 10px;
  }
}

.dark-input {
  width: 100%;
  padding: 14px;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid $glass-border;
  border-radius: 10px;
  color: white;
  font-size: 1rem;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: $color-accent;
  }
  &::placeholder {
    color: #666;
  }
}

select.dark-input {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='white' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 14px center;
  background-size: 16px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;

  label {
    color: $text-secondary;
    font-size: 0.9rem;
    font-weight: 500;
  }
}

/* Real Estate Section */
.real-estate-section {
  margin-top: 20px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.05);

  .sub-title {
    color: $color-accent;
    font-size: 1rem;
    margin-bottom: 16px;
  }

  .real-estate-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
}

.field.full-width {
  grid-column: 1 / -1;
}

/* File Upload */
.file-upload-area {
  .hidden-input {
    display: none;
  }

  .upload-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 30px;
    border: 2px dashed rgba(255, 255, 255, 0.2);
    border-radius: 16px;
    cursor: pointer;
    background: rgba(0, 0, 0, 0.2);
    transition: all 0.2s;
    color: $text-secondary;
    gap: 8px;

    &:hover {
      background: rgba(255, 255, 255, 0.05);
      border-color: $color-accent;
      color: $color-accent;
    }

    .icon {
      font-size: 2rem;
    }
  }

  .preview-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 12px;
    margin-top: 16px;

    .preview-item {
      position: relative;
      aspect-ratio: 1;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 8px;
        border: 1px solid rgba(255, 255, 255, 0.1);
      }

      .remove-btn {
        position: absolute;
        top: -6px;
        right: -6px;
        background: $color-danger;
        color: white;
        border: none;
        border-radius: 50%;
        width: 20px;
        height: 20px;
        font-size: 0.7rem;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }
}

.processing-msg {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: $color-accent;
  padding: 12px;
  border-radius: 8px;
  background: rgba(197, 160, 89, 0.1);
  margin-bottom: 16px;
}

.submit-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, $color-accent, #d4a017);
  color: #1a1a2e;
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 800;
  cursor: pointer;
  transition: transform 0.2s;
  box-shadow: 0 4px 15px rgba(197, 160, 89, 0.2);
  margin-top: 20px;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(197, 160, 89, 0.4);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    background: #555;
    color: #888;
    box-shadow: none;
  }
}

.spinner-sm {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(197, 160, 89, 0.3);
  border-top-color: $color-accent;
  border-radius: 50%;
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

@media (max-width: 600px) {
  .real-estate-grid {
    grid-template-columns: 1fr;
  }
}
</style>
