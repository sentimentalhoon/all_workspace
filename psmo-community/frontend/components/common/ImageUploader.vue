<script setup lang="ts">
interface ExistingImage {
  id: number;
  url: string;
  thumbnailUrl?: string;
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
  enableBlur: {
    type: Boolean,
    default: false,
  },
});

const newFiles = ref<File[]>([]);
const imagePairs = ref<{ original: File; blurred: File | null }[]>([]);
const previewUrls = ref<string[]>([]);
const deleteIds = ref<number[]>([]);

// Local processing state
const isProcessing = ref(false);

const emit = defineEmits<{
  (e: "update:files", files: File[]): void;
  (
    e: "update:imagePairs",
    pairs: { original: File; blurred: File | null }[],
  ): void;
  (e: "update:deleteIds", ids: number[]): void;
  (e: "update:processing", state: boolean): void;
}>();

// Face API Models Loading
import * as faceapi from "face-api.js";

const { compressImage } = useImageOptimization();

onMounted(async () => {
  if (process.client && props.enableBlur) {
    try {
      await faceapi.nets.tinyFaceDetector.loadFromUri(
        "https://justadudewhohacks.github.io/face-api.js/models",
      );
      console.log("FaceAPI models loaded");
    } catch (e) {
      console.error("Failed to load FaceAPI models", e);
    }
  }
});

const processImageWithBlur = async (
  file: File,
): Promise<{ original: File; blurred: File | null }> => {
  if (!props.enableBlur) {
    return { original: file, blurred: null };
  }
  return new Promise((resolve) => {
    const img = document.createElement("img");
    const reader = new FileReader();
    reader.onload = async (e) => {
      img.src = e.target?.result as string;
      img.onload = async () => {
        try {
          const detections = await faceapi.detectAllFaces(
            img,
            new faceapi.TinyFaceDetectorOptions(),
          );

          if (detections.length > 0) {
            const canvas = document.createElement("canvas");
            canvas.width = img.width;
            canvas.height = img.height;
            const ctx = canvas.getContext("2d");
            if (!ctx) {
              resolve({ original: file, blurred: null });
              return;
            }

            // Draw original
            ctx.drawImage(img, 0, 0);

            // Blur faces
            detections.forEach((det) => {
              const { x, y, width, height } = det.box;
              // Simple blur effect by pixelating or applying filter
              // Using filter requires recreating context state or clipping

              ctx.save();
              ctx.beginPath();
              ctx.rect(x, y, width, height);
              ctx.clip();
              ctx.filter = "blur(15px)";
              ctx.drawImage(img, 0, 0);
              ctx.restore();
            });

            canvas.toBlob((blob) => {
              if (blob) {
                const blurredFile = new File([blob], file.name, {
                  type: file.type,
                });
                resolve({ original: file, blurred: blurredFile });
              } else {
                resolve({ original: file, blurred: null });
              }
            }, file.type);
          } else {
            resolve({ original: file, blurred: null });
          }
        } catch (err) {
          console.error("Face detection error", err);
          resolve({ original: file, blurred: null });
        }
      };
    };
    reader.readAsDataURL(file);
  });
};

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
        // 1. Optimization
        const compressed = await compressImage(file, 1920, 1, 0.8);

        // 2. Face Blurring
        const optimizedPair = await processImageWithBlur(compressed);

        newFiles.value.push(optimizedPair.original);
        imagePairs.value.push(optimizedPair);

        // Create Preview
        const reader = new FileReader();
        reader.onload = (e) => {
          if (e.target?.result)
            previewUrls.value.push(e.target.result as string);
        };
        reader.readAsDataURL(optimizedPair.original);
      }

      emit("update:files", newFiles.value); // Backward compatibility
      emit("update:imagePairs", imagePairs.value); // New event
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
  imagePairs.value.splice(index, 1);
  previewUrls.value.splice(index, 1);
  emit("update:files", newFiles.value);
  emit("update:imagePairs", imagePairs.value);
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
          <img :src="img.thumbnailUrl || img.url" />
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
