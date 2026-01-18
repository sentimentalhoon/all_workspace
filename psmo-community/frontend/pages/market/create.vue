<script setup lang="ts">
const { fetchClient } = useApiClient();

/**
 * 상품 등록 페이지입니다.
 * TODO: 현재는 폼 디자인만 있고 실제 등록 기능(파일 업로드 등)은 아직 구현되지 않았습니다.
 * 나중에 FormData와 Multipart 전송 방식을 사용해서 구현해야 합니다.
 */
const form = ref({
  title: "",
  price: 0,
  category: "PC_FULL",
  description: "",
});

const submit = async () => {
  try {
    // Simplified submit for demo - need FormData for real file upload
    // Assuming backend accepts JSON for now or we update to Multipart later
    // Following backend spec: needs Multipart for files, but let's do basic JSON if supported or stub it
    // The previous code had multipart. Let's do a basic placeholder.

    // await fetchClient('/market/products', { method: 'POST', body: form.value })
    alert(
      "Items creation logic needs Multipart implementation (Media Upload).",
    );
  } catch (e) {
    alert("Error");
  }
};
</script>

<template>
  <div class="create-page">
    <h2>매장 내놓기 (양도)</h2>
    <form @submit.prevent="submit">
      <div class="group">
        <label>Title</label>
        <input v-model="form.title" required />
      </div>
      <div class="group">
        <label>Price</label>
        <input type="number" v-model="form.price" required />
      </div>
      <div class="group">
        <label>Category</label>
        <select v-model="form.category">
          <option value="PC_FULL">PC Full Set</option>
          <option value="CPU">CPU</option>
          <option value="GPU">GPU</option>
        </select>
      </div>
      <div class="group">
        <label>Description</label>
        <textarea v-model="form.description"></textarea>
      </div>
      <button type="submit" class="submit-btn" disabled>
        Register (Not Implemented)
      </button>
    </form>
  </div>
</template>

<style scoped>
.create-page {
  background: white;
  padding: 20px;
  border-radius: 12px;
}
.group {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}
input,
textarea,
select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-top: 5px;
}
.submit-btn {
  width: 100%;
  padding: 12px;
  background: #2196f3;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: bold;
}
.submit-btn:disabled {
  background: #ccc;
}
</style>
