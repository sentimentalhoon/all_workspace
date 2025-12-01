<template>
  <div class="search">
    <h1>검색</h1>
    <div class="search-box">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="캠핑장을 검색하세요..."
        @keyup.enter="handleSearch"
      />
      <button @click="handleSearch">검색</button>
    </div>
    <div v-if="searchResults.length" class="results">
      <h2>검색 결과</h2>
      <ul>
        <li v-for="result in searchResults" :key="result.id">
          {{ result.name }}
        </li>
      </ul>
    </div>
    <div v-else-if="searched" class="no-results">검색 결과가 없습니다.</div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const searchQuery = ref("");
const searchResults = ref([]);
const searched = ref(false);

const handleSearch = () => {
  searched.value = true;
  // TODO: API 연동
  searchResults.value = [];
};
</script>

<style scoped>
.search {
  max-width: 600px;
  margin: 0 auto;
}

h1 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.search-box {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
}

.search-box input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.search-box button {
  padding: 0.75rem 1.5rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}

.search-box button:active {
  opacity: 0.8;
}

.results h2 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
}

.results ul {
  list-style: none;
}

.results li {
  padding: 1rem;
  background: #f5f5f5;
  margin-bottom: 0.5rem;
  border-radius: 4px;
}

.no-results {
  text-align: center;
  color: #666;
  padding: 2rem;
}
</style>
