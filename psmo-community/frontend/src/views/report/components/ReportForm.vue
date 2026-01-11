<template>
  <div class="report-form">
    <h3>새 진상 등록</h3>

    <div class="form-group">
      <label>진상 유형 *</label>
      <div class="type-chips">
        <button
          v-for="type in reportTypes"
          :key="type"
          :class="{ active: selectedType === type }"
          @click="emit('update:selectedType', type)"
          type="button"
        >
          {{ type }}
        </button>
      </div>
    </div>

    <div class="form-group">
      <label>발생 지역 *</label>
      <select
        :value="region"
        @change="emit('update:region', ($event.target as HTMLSelectElement).value)"
      >
        <option value="">선택하세요</option>
        <option value="서울">서울</option>
        <option value="경기">경기</option>
        <option value="인천">인천</option>
        <option value="강원">강원</option>
        <option value="충북">충북</option>
        <option value="충남">충남</option>
        <option value="대전">대전</option>
        <option value="경북">경북</option>
        <option value="경남">경남</option>
        <option value="대구">대구</option>
        <option value="울산">울산</option>
        <option value="부산">부산</option>
        <option value="전북">전북</option>
        <option value="전남">전남</option>
        <option value="광주">광주</option>
        <option value="제주">제주</option>
      </select>
    </div>

    <div class="form-group">
      <label>피씨방명</label>
      <input
        :value="pcRoomName"
        type="text"
        placeholder="피씨방 이름"
        @input="emit('update:pcRoomName', ($event.target as HTMLInputElement).value)"
      />
    </div>

    <div class="form-group">
      <label>발생 날짜 *</label>
      <input
        :value="incidentDate"
        type="date"
        @input="emit('update:incidentDate', ($event.target as HTMLInputElement).value)"
      />
    </div>

    <div class="form-group">
      <label>진상 특징 (간략히)</label>
      <input
        :value="characteristic"
        type="text"
        placeholder="예: 30대 남성, 검은색 모자"
        @input="emit('update:characteristic', ($event.target as HTMLInputElement).value)"
      />
    </div>

    <div class="form-group">
      <label>상세 내용 *</label>
      <textarea
        :value="content"
        rows="8"
        placeholder="어떤 일이 있었는지 자세히 작성해주세요&#10;&#10;예시:&#10;- 음식물 쏟고 치우지 않음&#10;- 큰 소리로 욕설&#10;- 물건 파손 후 배상 거부&#10;- 성인물 시청 중 경고 무시"
        @input="emit('update:content', ($event.target as HTMLTextAreaElement).value)"
      ></textarea>
    </div>

    <div class="form-group">
      <label>사진 업로드 (최대 {{ maxPhotos }}장)</label>
      <PhotoUploader
        :max-photos="maxPhotos"
        :max-file-size-mb="maxFileSizeMb"
        :photo-previews="photoPreviews"
        :is-optimizing="isOptimizingPhotos"
        @upload="emit('photo-upload', $event)"
        @remove="emit('remove-photo', $event)"
      />
    </div>

    <div class="form-group">
      <label>영상 업로드 (1분 미만)</label>
      <VideoUploader
        :video-file="videoFile"
        @upload="emit('video-upload', $event)"
        @remove="emit('remove-video')"
      />
    </div>

    <div class="form-group">
      <label>심각도</label>
      <div class="severity-buttons">
        <button
          v-for="s in severityLevels"
          :key="s.value"
          :class="['severity-btn', s.class, { active: severity === s.value }]"
          type="button"
          @click="emit('update:severity', s.value)"
        >
          {{ s.label }}
        </button>
      </div>
    </div>

    <div class="form-actions">
      <button class="btn-submit" type="button" @click="emit('submit')">등록하기</button>
      <button class="btn-cancel" type="button" @click="emit('reset')">취소</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { SeverityLevel } from '../useReportForm'
import PhotoUploader from './PhotoUploader.vue'
import VideoUploader from './VideoUploader.vue'

defineProps({
  reportTypes: {
    type: Array as () => string[],
    required: true,
  },
  severityLevels: {
    type: Array as () => SeverityLevel[],
    required: true,
  },
  selectedType: {
    type: String,
    required: true,
  },
  region: {
    type: String,
    required: true,
  },
  pcRoomName: {
    type: String,
    required: true,
  },
  incidentDate: {
    type: String,
    required: true,
  },
  characteristic: {
    type: String,
    required: true,
  },
  content: {
    type: String,
    required: true,
  },
  severity: {
    type: String,
    required: true,
  },
  maxPhotos: {
    type: Number,
    required: true,
  },
  maxFileSizeMb: {
    type: Number,
    required: true,
  },
  photoPreviews: {
    type: Array as () => string[],
    required: true,
  },
  isOptimizingPhotos: {
    type: Boolean,
    required: true,
  },
  videoFile: {
    type: Object as () => File | null,
    default: null,
  },
})

const emit = defineEmits<{
  (event: 'update:selectedType', value: string): void
  (event: 'update:region', value: string): void
  (event: 'update:pcRoomName', value: string): void
  (event: 'update:incidentDate', value: string): void
  (event: 'update:characteristic', value: string): void
  (event: 'update:content', value: string): void
  (event: 'update:severity', value: string): void
  (event: 'photo-upload', payload: Event): void
  (event: 'remove-photo', index: number): void
  (event: 'video-upload', file: File): void
  (event: 'remove-video'): void
  (event: 'submit'): void
  (event: 'reset'): void
}>()

// This component simply forwards user interactions upward to keep it stateless.
</script>
