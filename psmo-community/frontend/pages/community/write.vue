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
  <div class="page-container">
    <div class="header">
      <h2>글쓰기</h2>
    </div>

    <form @submit.prevent="submit" class="write-form">
      <div class="form-group">
        <select v-model="form.category">
          <option :value="BoardCategory.FREE">자유 게시판</option>
          <option :value="BoardCategory.QA">질문 게시판</option>
          <option :value="BoardCategory.NOTICE">
            공지사항 (관리자만 가능하게?)
          </option>
        </select>
      </div>

      <div class="form-group">
        <input v-model="form.title" placeholder="제목을 입력하세요" required />
      </div>

      <div class="form-group">
        <textarea
          v-model="form.content"
          placeholder="내용을 자유롭게 작성해주세요."
          rows="12"
          required
        ></textarea>
      </div>

      <!-- TODO: Image Upload UI -->

      <div class="actions">
        <button type="button" @click="$router.back()" class="cancel-btn">
          취소
        </button>
        <button type="submit" class="submit-btn" :disabled="loading">
          등록
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.write-form {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-group {
  margin-bottom: 15px;
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

textarea {
  resize: vertical;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn {
  padding: 10px 20px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 8px;
  cursor: pointer;
}

.submit-btn {
  padding: 10px 20px;
  background: #e94560;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
}

.submit-btn:disabled {
  background: #ccc;
}
</style>
