<template>
  <div class="photo-upload">
    <label class="upload-dropzone">
      <input
        type="file"
        accept="image/*"
        multiple
        :disabled="isOptimizing"
        @change="emit('upload', $event)"
        hidden
      />
      <span class="dropzone-icon">📷</span>
      <div>
        <p class="dropzone-title">사진을 드래그하거나 클릭해서 추가하세요</p>
        <p class="dropzone-desc">
          jpg, png · 최대 {{ maxPhotos }}장 · {{ maxFileSizeMb }}MB 이하 권장
        </p>
      </div>
    </label>
    <p class="optimization-hint">
      큰 이미지는 프론트에서 1920px 기준으로 가볍게 줄이고 나머지 최적화는 서버에서 처리합니다.
    </p>
    <div v-if="isOptimizing" class="optimization-status">이미지 준비 중...</div>
    <div v-if="photoPreviews.length" class="photo-preview-grid">
      <div v-for="(src, idx) in photoPreviews" :key="src" class="photo-preview-item">
        <img :src="src" alt="업로드한 사진 미리보기" />
        <button class="btn-remove" @click="emit('remove', idx)">삭제</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps({
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
  isOptimizing: {
    type: Boolean,
    required: true,
  },
})

const emit = defineEmits<{
  (event: 'upload', payload: Event): void
  (event: 'remove', index: number): void
}>()

// props are referenced in template; component is stateless otherwise.
</script>
