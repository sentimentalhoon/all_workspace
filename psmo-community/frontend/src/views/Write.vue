<template>
  <div class="write">
    <h1>글쓰기</h1>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>카테고리</label>
        <select v-model="post.category">
          <option value="">선택하세요</option>
          <option value="자유게시판">자유게시판</option>
          <option value="질문">질문</option>
          <option value="정보공유">정보공유</option>
          <option value="후기">후기</option>
        </select>
      </div>

      <div class="form-group">
        <label>제목</label>
        <input
          type="text"
          v-model="post.title"
          placeholder="제목을 입력하세요"
          required
        />
      </div>

      <div class="form-group">
        <label>내용</label>
        <textarea
          v-model="post.content"
          placeholder="내용을 입력하세요"
          rows="10"
          required
        ></textarea>
      </div>

      <div class="button-group">
        <button type="button" @click="handleCancel" class="btn-cancel">
          취소
        </button>
        <button type="submit" class="btn-submit">작성 완료</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const post = ref({
  category: "",
  title: "",
  content: "",
});

const handleSubmit = () => {
  // TODO: API 연동
  console.log("Submitting post:", post.value);
  router.push("/community");
};

const handleCancel = () => {
  router.push("/community");
};
</script>

<style scoped>
.write {
  max-width: 600px;
  margin: 0 auto;
}

h1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 1.5rem;
}

form {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #2c3e50;
}

.form-group select,
.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
}

.form-group textarea {
  resize: vertical;
}

.button-group {
  display: flex;
  gap: 0.5rem;
}

.button-group button {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-cancel {
  background: #e0e0e0;
  color: #666;
}

.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.button-group button:active {
  opacity: 0.8;
}
</style>
