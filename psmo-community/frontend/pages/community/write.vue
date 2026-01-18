<script setup lang="ts">
import { useRouter } from "vue-router";
import type { PostCreateRequest } from "~/composables/useBoard";
import { BoardCategory, useBoard } from "~/composables/useBoard";

const { createPost } = useBoard();
const router = useRouter();

const form = ref<PostCreateRequest>({
  title: "",
  content: "",
  category: BoardCategory.FREE, // Default
  imageUrls: [], // Future: image upload
});

const loading = ref(false);

const submit = async () => {
  if (!form.value.title || !form.value.content) {
    alert("제목과 내용을 입력해주세요.");
    return;
  }

  loading.value = true;
  try {
    // TODO: Implement image upload here if needed, similar to Market/Blacklist
    await createPost(form.value);
    router.push("/community");
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
      <h2 class="page-title">글쓰기</h2>
      <p class="page-desc">소중한 의견을 공유해주세요.</p>
    </div>

    <form @submit.prevent="submit" class="glass-panel write-form">
      <div class="form-group">
        <label class="section-label">게시판 선택</label>
        <div class="select-wrapper">
          <select v-model="form.category" class="dark-input">
            <option :value="BoardCategory.FREE">자유 게시판</option>
            <option :value="BoardCategory.QA">질문 게시판</option>
            <option :value="BoardCategory.NOTICE">공지사항</option>
          </select>
        </div>
      </div>

      <div class="form-group">
        <label class="section-label">제목</label>
        <input
          v-model="form.title"
          placeholder="제목을 입력하세요"
          required
          class="dark-input"
        />
      </div>

      <div class="form-group">
        <label class="section-label">내용</label>
        <textarea
          v-model="form.content"
          placeholder="내용을 자유롭게 작성해주세요."
          rows="12"
          required
          class="dark-input"
        ></textarea>
      </div>

      <!-- TODO: Image Upload UI -->

      <div class="actions">
        <button type="button" @click="$router.back()" class="cancel-btn">
          취소
        </button>
        <button type="submit" class="submit-btn hover-glow" :disabled="loading">
          {{ loading ? "등록 중..." : "등록" }}
        </button>
      </div>
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
    margin: 0 0 6px 0;
    font-size: 1.5rem;
  }
  .page-desc {
    color: $text-secondary;
    margin: 0;
    font-size: 0.95rem;
  }
}

.glass-panel {
  background: $glass-bg;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid $glass-border;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.form-group {
  margin-bottom: 20px;

  .section-label {
    display: block;
    font-weight: bold;
    color: $text-secondary;
    margin-bottom: 8px;
    font-size: 0.9rem;
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

textarea.dark-input {
  resize: vertical;
  line-height: 1.6;
}

select.dark-input {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='white' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 14px center;
  background-size: 16px;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
}

.cancel-btn {
  padding: 12px 24px;
  border: 1px solid $glass-border;
  background: transparent;
  color: $text-secondary;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
    color: white;
  }
}

.submit-btn {
  padding: 12px 30px;
  background: linear-gradient(135deg, $color-accent, #d4a017);
  color: #1a1a2e;
  border: none;
  border-radius: 12px;
  font-weight: 800;
  cursor: pointer;
  transition: transform 0.2s;
  box-shadow: 0 4px 15px rgba(197, 160, 89, 0.2);

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(197, 160, 89, 0.4);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    background: #555;
    color: #888;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.fade-in {
  animation: fadeIn 0.5s ease-out forwards;
}
</style>
