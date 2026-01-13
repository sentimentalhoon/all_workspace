<script setup lang="ts">
import PhotoUploader from '@/components/common/media/PhotoUploader.vue'
import VideoUploader from '@/components/common/media/VideoUploader.vue'
import { useMediaUpload } from '@/composables/useMediaUpload'
import { marketService, type ProductCreateRequest } from '@/services/market'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref<'GENERAL' | 'BUSINESS'>('GENERAL')

// Media Upload Logic
const {
  photos,
  photoPreviews,
  isOptimizingPhotos,
  handlePhotoUpload,
  removePhoto,
  videoFile,
  handleVideoUpload,
  removeVideo,
  MAX_PHOTOS,
  MAX_FILE_SIZE_MB,
} = useMediaUpload({ maxPhotos: 20 })

const form = ref<
  ProductCreateRequest & {
    realEstate?: {
      locationCity: string
      locationDistrict: string
      pcCount: number
      deposit: number
      monthlyRent: number
      managementFee: number
      averageMonthlyRevenue: number
      floor?: number
      areaMeters?: number
    }
  }
>({
  title: '',
  description: '',
  price: 0,
  category: 'PC_FULL',
  realEstate: undefined, // Initially undefined
})

const categories = [
  'PC_FULL',
  'CPU',
  'GPU',
  'RAM',
  'MAINBOARD',
  'SSD_HDD',
  'CASE',
  'POWER',
  'MONITOR',
  'GEAR',
  'SOFTWARE',
  'ETC',
]

const switchTab = (tab: 'GENERAL' | 'BUSINESS') => {
  activeTab.value = tab
  if (tab === 'BUSINESS') {
    form.value.category = 'PC_BUSINESS'
    form.value.realEstate = {
      locationCity: '',
      locationDistrict: '',
      pcCount: 0,
      deposit: 0,
      monthlyRent: 0,
      managementFee: 0,
      averageMonthlyRevenue: 0,
      floor: undefined,
      areaMeters: undefined,
    }
  } else {
    form.value.category = 'PC_FULL'
    form.value.realEstate = undefined
  }
}

const submit = async () => {
  try {
    // Prepare FormData for multipart/form-data
    const formData = new FormData()

    // JSON part
    const jsonPart = {
      title: form.value.title,
      description: form.value.description,
      price: form.value.price,
      category: form.value.category,
      realEstate: form.value.realEstate,
    }
    formData.append('product', JSON.stringify(jsonPart))

    // Images
    photos.value.forEach((file) => {
      formData.append('files', file)
    })

    // Video
    if (videoFile.value) {
      formData.append('files', videoFile.value)
    }

    await marketService.createProductMultipart(formData)

    alert('Product registered successfully!')
    router.push('/market')
  } catch (e) {
    alert('Failed to create product')
    console.error(e)
  }
}
</script>

<template>
  <div class="create-view">
    <div class="header">
      <h1>Sell Item</h1>
      <div class="tabs">
        <button :class="{ active: activeTab === 'GENERAL' }" @click="switchTab('GENERAL')">
          일반 중고거래
        </button>
        <button :class="{ active: activeTab === 'BUSINESS' }" @click="switchTab('BUSINESS')">
          PC방 매매 (부동산)
        </button>
      </div>
    </div>

    <form @submit.prevent="submit">
      <div class="section">
        <h3>기본 정보</h3>
        <div class="form-group">
          <label>Title</label>
          <input v-model="form.title" required placeholder="제목을 입력하세요" />
        </div>

        <div class="form-group" v-if="activeTab === 'GENERAL'">
          <label>Category</label>
          <select v-model="form.category">
            <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
          </select>
        </div>

        <div class="form-group">
          <label>{{ activeTab === 'BUSINESS' ? '권리금 (Premium)' : 'Price' }}</label>
          <input type="number" v-model="form.price" required />
        </div>
      </div>

      <!-- Business Specific Fields -->
      <div class="section business-info" v-if="activeTab === 'BUSINESS' && form.realEstate">
        <h3>매장 정보</h3>
        <div class="grid-2">
          <div class="form-group">
            <label>지역 (시/도)</label>
            <input v-model="form.realEstate.locationCity" required placeholder="예: 서울" />
          </div>
          <div class="form-group">
            <label>상세 지역 (구/동)</label>
            <input v-model="form.realEstate.locationDistrict" required placeholder="예: 강남구" />
          </div>
        </div>

        <div class="grid-3">
          <div class="form-group">
            <label>PC 대수</label>
            <input type="number" v-model="form.realEstate.pcCount" required />
          </div>
          <div class="form-group">
            <label>층수</label>
            <input type="number" v-model="form.realEstate.floor" />
          </div>
          <div class="form-group">
            <label>면적 (m²)</label>
            <input type="number" v-model="form.realEstate.areaMeters" step="0.1" />
          </div>
        </div>

        <div class="grid-2">
          <div class="form-group">
            <label>보증금</label>
            <input type="number" v-model="form.realEstate.deposit" required />
          </div>
          <div class="form-group">
            <label>월세</label>
            <input type="number" v-model="form.realEstate.monthlyRent" required />
          </div>
        </div>

        <div class="grid-2">
          <div class="form-group">
            <label>관리비</label>
            <input type="number" v-model="form.realEstate.managementFee" required />
          </div>
          <div class="form-group">
            <label>월 평균 매출</label>
            <input type="number" v-model="form.realEstate.averageMonthlyRevenue" required />
          </div>
        </div>
      </div>

      <div class="section">
        <h3>상세 설명</h3>
        <div class="form-group">
          <textarea
            v-model="form.description"
            rows="8"
            placeholder="상세한 내용을 적어주세요."
          ></textarea>
        </div>
      </div>

      <div class="section upload-section">
        <h3>사진 / 동영상</h3>
        <div class="form-group">
          <label>사진 (최대 20장)</label>
          <PhotoUploader
            :max-photos="MAX_PHOTOS"
            :max-file-size-mb="MAX_FILE_SIZE_MB"
            :photo-previews="photoPreviews"
            :is-optimizing="isOptimizingPhotos"
            @upload="handlePhotoUpload"
            @remove="removePhoto"
          />
        </div>
        <div class="form-group">
          <label>동영상 (1개)</label>
          <VideoUploader
            :video-file="videoFile"
            @upload="handleVideoUpload"
            @remove="removeVideo"
          />
        </div>
      </div>

      <button type="submit" class="btn-primary">Register Item</button>
    </form>
  </div>
</template>

<style scoped>
.create-view {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.tabs {
  display: flex;
  gap: 10px;
  background: #f0f2f5;
  padding: 4px;
  border-radius: 8px;
}
.tabs button {
  padding: 8px 16px;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 6px;
  font-weight: 500;
}
.tabs button.active {
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  color: #2196f3;
}

.section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  border: 1px solid #e1e4e8;
}
.section h3 {
  margin-bottom: 16px;
  font-size: 1.1rem;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.form-group {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
}
input,
select,
textarea {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-top: 6px;
  font-size: 14px;
}
input:focus,
select:focus,
textarea:focus {
  border-color: #2196f3;
  outline: none;
}

.grid-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.grid-3 {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 16px;
}

.btn-primary {
  background-color: #2196f3;
  color: white;
  border: none;
  padding: 16px;
  width: 100%;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-primary:hover {
  background-color: #1976d2;
}
</style>
