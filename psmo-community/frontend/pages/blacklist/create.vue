<script setup lang="ts">
import {
  useBlacklist,
  type BadUserCreateRequest,
} from "~/composables/useBlacklist";

const { reportBadUser, updateBadUser, fetchBadUserById } = useBlacklist();
const router = useRouter();
const route = useRoute();

definePageMeta({
  auth: true,
});

const form = ref<BadUserCreateRequest>({
  region: "",
  reason: "",
  physicalDescription: "",
  incidentDate: "",
});

const files = ref<{ original: File; blurred: File | null }[]>([]);
const existingImages = ref<{ id: number; url: string; thumbnailUrl: string }[]>(
  [],
);
const deleteImageIds = ref<number[]>([]);

const loading = ref(false);
const processingImages = ref(false);

const isEditMode = computed(() => !!route.query.id);
const reportId = computed(() => Number(route.query.id));

onMounted(async () => {
  if (isEditMode.value) {
    try {
      loading.value = true;
      const data = await fetchBadUserById(reportId.value);

      form.value = {
        region: data.region,
        reason: data.reason,
        physicalDescription: data.physicalDescription || "",
        incidentDate: data.incidentDate || "",
      };

      if (data.images) {
        existingImages.value = data.images;
      }
    } catch (e) {
      alert("정보를 불러오는데 실패했습니다.");
      router.back();
    } finally {
      loading.value = false;
    }
  }
});

const submit = async () => {
  if (!form.value.region.trim()) {
    alert("활동지역을 입력해주세요.");
    return;
  }
  if (!form.value.reason.trim()) {
    alert("피해사유를 입력해주세요.");
    return;
  }
  if (
    !confirm(
      "등록하신 내용은 사실이어야 하며, 허위 사실 유포 시 책임이 따를 수 있습니다. 진행하시겠습니까?",
    )
  ) {
    return;
  }

  loading.value = true;
  try {
    // Cast files to any or match the expected type if exported.
    // Since useBlacklist.ts function arguments updated to accept { original, blurred }[], this matches.
    if (isEditMode.value) {
      await updateBadUser(
        reportId.value,
        form.value,
        files.value as any,
        deleteImageIds.value,
      );
      alert("수정되었습니다.");
    } else {
      await reportBadUser(form.value, files.value as any);
      alert("등록되었습니다.");
    }
    router.push("/blacklist");
  } catch (e: any) {
    alert((isEditMode.value ? "수정" : "등록") + " 실패: " + (e.message || e));
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="page-container fade-in">
    <div class="header">
      <h2 class="page-title">
        {{ isEditMode ? "피해 사례 수정" : "피해 사례 등록" }}
      </h2>
      <p class="page-desc">
        {{
          isEditMode
            ? "등록된 내용을 수정합니다."
            : "정확한 피해 사실만 공유해주시기 바랍니다."
        }}
      </p>
    </div>

    <form @submit.prevent="submit" class="glass-panel form-card">
      <div class="notice">
        <strong>⚠️ 주의사항</strong>
        <p>
          사실에 근거하지 않은 비방 목적의 글은 법적 책임이 따를 수 있습니다.<br />
          정확한 피해 사실만 공유해주시기 바랍니다.
        </p>
      </div>

      <!-- Form Fields -->
      <div class="form-section">
        <div class="field">
          <label>활동지역 <span class="required">*</span></label>
          <input
            v-model="form.region"
            placeholder="예: 서울시 강남구, 경기도 수원시"
            required
            class="dark-input"
          />
        </div>

        <div class="form-row">
          <div class="field">
            <label>피해 발생일 (선택)</label>
            <input type="date" v-model="form.incidentDate" class="dark-input" />
          </div>

          <div class="field">
            <label>신체적 특징 (선택)</label>
            <input
              v-model="form.physicalDescription"
              placeholder="예: 키 약 175cm, 안경 착용"
              class="dark-input"
            />
          </div>
        </div>

        <div class="field full-width">
          <label>피해 사유 <span class="required">*</span></label>
          <textarea
            v-model="form.reason"
            rows="10"
            maxlength="2000"
            placeholder="구체적인 피해 내용을 작성해주세요 (최대 2000자)"
            class="dark-input"
            required
          ></textarea>
          <small class="char-count">{{ form.reason.length }} / 2000자</small>
        </div>
      </div>

      <!-- Image Upload -->
      <div class="form-section">
        <label class="section-label">증거 사진 (최대 20장)</label>

        <common-image-uploader
          :existing-images="existingImages"
          :max-count="20"
          :enable-blur="true"
          @update:image-pairs="(f: any[]) => (files = f)"
          @update:delete-ids="(ids: number[]) => (deleteImageIds = ids)"
          @update:processing="(state: boolean) => (processingImages = state)"
        />
      </div>

      <div v-if="processingImages" class="processing-msg glass-panel-sm">
        <div class="spinner-sm"></div>
        <span>이미지 최적화 중입니다... ({{ files.length }}장)</span>
      </div>

      <button
        type="submit"
        class="submit-btn hover-glow"
        :disabled="
          loading ||
          processingImages ||
          existingImages.length + files.length > 20
        "
      >
        {{
          loading
            ? isEditMode
              ? "수정 중..."
              : "등록 중..."
            : processingImages
              ? "이미지 처리 대기 중..."
              : isEditMode
                ? "수정 완료"
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
  max-width: 800px;
  margin: 0 auto;
  padding: 16px;
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

.notice {
  background: rgba(233, 69, 96, 0.1);
  border: 1px solid rgba(233, 69, 96, 0.3);
  color: #ff8a8a;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 24px;
  font-size: 0.9rem;
  line-height: 1.5;

  strong {
    display: block;
    margin-bottom: 6px;
    color: $color-danger;
  }
  p {
    margin: 0;
  }
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
}

.form-row {
  display: flex;
  gap: 16px;

  .field {
    flex: 1;
  }
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;

  label {
    color: $text-secondary;
    font-size: 0.9rem;
    font-weight: 500;

    .required {
      color: $color-danger;
    }
  }

  .char-count {
    text-align: right;
    color: #666;
    font-size: 0.8rem;
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
    border-color: $color-danger;
  }
}

.section-label {
  display: block;
  font-size: 1.1rem;
  font-weight: bold;
  color: $text-primary;
  margin-bottom: 12px;
  border-left: 3px solid $color-danger;
  padding-left: 10px;
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
  background: $color-danger;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 800;
  cursor: pointer;
  transition: transform 0.2s;
  box-shadow: 0 4px 15px rgba(233, 69, 96, 0.2);
  margin-top: 10px;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(233, 69, 96, 0.4);
    background: lighten($color-danger, 5%);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    background: #555;
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
  .form-row {
    flex-direction: column;
  }
}
</style>
