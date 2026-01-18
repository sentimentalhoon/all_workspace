<script setup lang="ts">
import { useImageOptimization } from "~/composables/useImageOptimization";

interface ExistingImage {
  id: number;
  url: string;
}

const props = defineProps({
  maxCount: {
    type: Number,
    default: 20,
  },
  existingImages: {
    type: Array as PropType<ExistingImage[]>,
    default: () => [],
  },
});

const emit = defineEmits<{
  (e: "update:files", files: File[]): void;
  (e: "update:deleteIds", ids: number[]): void;
  (e: "update:processing", state: boolean): void;
}>();

const { compressImage } = useImageOptimization();

const newFiles = ref<File[]>([]);
const previewUrls = ref<string[]>([]);
const deleteIds = ref<number[]>([]);

// Local processing state
const isProcessing = ref(false);

const handleFileChange = async (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files) {
    const selectedFiles = Array.from(target.files);

    // Limit Check
    const currentTotal =
      props.existingImages.length -
      deleteIds.value.length +
      newFiles.value.length +
      selectedFiles.length;

    if (currentTotal > props.maxCount) {
      alert(`이미지는 최대 ${props.maxCount}장까지만 첨부할 수 있습니다.`);
      return;
    }

    isProcessing.value = true;
    emit("update:processing", true);

    try {
      for (const file of selectedFiles) {
        // Optimization
        const compressed = await compressImage(file, 1920, 1, 0.8);

        newFiles.value.push(compressed);

        // Create Preview
        const reader = new FileReader();
        reader.onload = (e) => {
          if (e.target?.result)
            previewUrls.value.push(e.target.result as string);
        };
        reader.readAsDataURL(compressed);
      }

      emit("update:files", newFiles.value);
    } catch (err) {
      console.error("Image processing error", err);
      alert("이미지 처리 중 오류가 발생했습니다.");
    } finally {
      isProcessing.value = false;
      emit("update:processing", false);
      // Reset input
      target.value = "";
    }
  }
};

const removeNewFile = (index: number) => {
  newFiles.value.splice(index, 1);
  previewUrls.value.splice(index, 1);
  emit("update:files", newFiles.value);
};

const removeExistingImage = (id: number) => {
  if (!deleteIds.value.includes(id)) {
    deleteIds.value.push(id);
    emit("update:deleteIds", deleteIds.value);
  }
};

// Helper to filter visually removed existing images
const visibleExistingImages = computed(() => {
  return props.existingImages.filter(
    (img) => !deleteIds.value.includes(img.id),
  );
});
</script>

<template>
  <div class="image-uploader">
    <div class="file-upload-area">
      <input
        type="file"
        multiple
        @change="handleFileChange"
        accept="image/*"
        id="common-file-input"
        class="hidden-input"
      />
      <label for="common-file-input" class="upload-box hover-glow">
        <span class="icon">📸</span>
        <span>사진 추가하기</span>
        <span class="count-badge">
          {{ visibleExistingImages.length + newFiles.length }} / {{ maxCount }}
        </span>
      </label>

      <div class="preview-grid">
        <!-- Existing Images -->
        <div
          v-for="img in visibleExistingImages"
          :key="'exist-' + img.id"
          class="preview-item existing"
        >
          <img :src="img.url" />
          <button
            type="button"
            class="remove-btn"
            @click="removeExistingImage(img.id)"
          >
            ✕
          </button>
          <span class="badge">기존</span>
        </div>

        <!-- New Images -->
        <div
          v-for="(url, idx) in previewUrls"
          :key="'new-' + idx"
          class="preview-item new"
        >
          <img :src="url" />
          <button type="button" class="remove-btn" @click="removeNewFile(idx)">
            ✕
          </button>
          <span class="badge new-badge">신규</span>
        </div>
      </div>
    </div>

    <div v-if="isProcessing" class="processing-msg glass-panel-sm">
      <div class="spinner-sm"></div>
      <span>이미지 최적화 중입니다...</span>
    </div>
  </div>
</template>

<style scoped lang="scss">
/* --- Variables --- */
$color-primary: #1e88e5;
$color-accent: #c5a059;
$color-danger: #e94560;
$text-secondary: #b0b0b0;

.image-uploader {
  width: 100%;
}

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

    .count-badge {
      background: rgba(0, 0, 0, 0.6);
      padding: 4px 8px;
      border-radius: 10px;
      font-size: 0.8rem;
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
        z-index: 2;
        transition: transform 0.1s;

        &:hover {
          transform: scale(1.1);
        }
      }

      .badge {
        position: absolute;
        bottom: 4px;
        right: 4px;
        background: rgba(0, 0, 0, 0.6);
        color: white;
        font-size: 0.7rem;
        padding: 2px 6px;
        border-radius: 4px;
      }
      .new-badge {
        background: $color-accent;
        color: black;
        font-weight: bold;
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
  margin-top: 16px;
}

/* Spinner Animation */
.spinner-sm {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
