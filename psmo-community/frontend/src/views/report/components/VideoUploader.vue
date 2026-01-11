<template>
  <div class="video-upload">
    <label class="upload-dropzone" :class="{ disabled: !!videoFile }">
      <input
        type="file"
        accept="video/*"
        :disabled="!!videoFile"
        @change="handleFileChange"
        hidden
      />
      <span class="dropzone-icon">🎥</span>
      <div>
        <p class="dropzone-title">동영상을 드래그하거나 클릭해서 추가하세요</p>
        <p class="dropzone-desc">mp4, mov · 1분 미만 · 500MB 이하 권장</p>
      </div>
    </label>

    <div v-if="videoFile" class="video-preview-grid">
      <div class="video-preview-item">
        <video controls :src="videoPreviewUrl"></video>
        <button class="btn-remove" @click="removeVideo">삭제</button>
      </div>
    </div>

    <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
  </div>
</template>

<script setup lang="ts">
import { onUnmounted, ref, watch } from 'vue'

const props = defineProps({
  videoFile: {
    type: Object as () => File | null,
    default: null,
  },
})

const emit = defineEmits<{
  (event: 'upload', file: File): void
  (event: 'remove'): void
}>()

const videoPreviewUrl = ref<string>('')
const errorMsg = ref<string>('')

// Watch for external file changes (e.g., reset)
watch(
  () => props.videoFile,
  (newFile) => {
    if (!newFile) {
      revokeUrl()
    } else {
      videoPreviewUrl.value = URL.createObjectURL(newFile)
    }
  },
  { immediate: true },
)

const handleFileChange = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (!input.files || input.files.length === 0) return

  const file = input.files[0]
  validateAndEmit(file)

  // Reset input so same file can be selected again if needed (after remove)
  input.value = ''
}

const validateAndEmit = (file: File) => {
  errorMsg.value = ''

  // 1. Create temporary URL
  const url = URL.createObjectURL(file)

  // 2. Load metadata to check duration
  const video = document.createElement('video')
  video.preload = 'metadata'
  video.src = url

  video.onloadedmetadata = () => {
    window.URL.revokeObjectURL(url)

    if (video.duration >= 61) {
      // 1분 (여유 1초)
      errorMsg.value = '영상 길이는 1분 미만이어야 합니다.'
      return
    }

    // Pass validation
    emit('upload', file)
  }

  video.onerror = () => {
    window.URL.revokeObjectURL(url)
    errorMsg.value = '이 영상은 재생할 수 없거나 형식이 올바르지 않습니다.'
  }
}

const removeVideo = () => {
  emit('remove')
}

const revokeUrl = () => {
  if (videoPreviewUrl.value) {
    URL.revokeObjectURL(videoPreviewUrl.value)
    videoPreviewUrl.value = ''
  }
}

onUnmounted(() => {
  revokeUrl()
})
</script>

<style scoped>
.video-upload {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.upload-dropzone {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  background-color: #f8fafc;
}

.upload-dropzone:hover:not(.disabled) {
  border-color: #64748b;
  background-color: #f1f5f9;
}

.upload-dropzone.disabled {
  opacity: 0.5;
  cursor: default;
}

.dropzone-icon {
  font-size: 24px;
}

.dropzone-title {
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  margin: 0 0 4px 0;
}

.dropzone-desc {
  font-size: 12px;
  color: #64748b;
  margin: 0;
}

.video-preview-grid {
  margin-top: 10px;
}

.video-preview-item {
  position: relative;
  width: 100%;
  max-width: 320px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.video-preview-item video {
  width: 100%;
  display: block;
}

.btn-remove {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 11px;
  cursor: pointer;
  backdrop-filter: blur(4px);
  transition: background 0.2s;
}

.btn-remove:hover {
  background: rgba(239, 68, 68, 0.9);
}

.error-msg {
  color: #ef4444;
  font-size: 13px;
  margin-top: 4px;
}
</style>
