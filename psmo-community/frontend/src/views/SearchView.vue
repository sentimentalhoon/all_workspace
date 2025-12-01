<template>
  <div class="search-view">
    <div class="page-header">
      <h2>🔍 게시글 검색</h2>
    </div>

    <div class="search-container">
      <div class="search-box">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="게시글, 태그, 작성자 검색..."
          @keyup.enter="handleSearch"
        />
        <button @click="handleSearch">검색</button>
      </div>

      <div class="filter-section">
        <h3>카테고리</h3>
        <div class="category-chips">
          <button
            v-for="category in categories"
            :key="category"
            :class="{ active: selectedCategory === category }"
            @click="selectedCategory = category"
          >
            {{ category }}
          </button>
        </div>
      </div>

      <div class="filter-section">
        <h3>정렬</h3>
        <div class="sort-buttons">
          <button
            v-for="sort in sortOptions"
            :key="sort.value"
            :class="{ active: selectedSort === sort.value }"
            @click="selectedSort = sort.value"
          >
            {{ sort.label }}
          </button>
        </div>
      </div>

      <div class="filter-section">
        <h3>기간</h3>
        <div class="date-inputs">
          <input v-model="dateFrom" type="date" />
          <span>~</span>
          <input v-model="dateTo" type="date" />
        </div>
      </div>

      <div class="search-actions">
        <button class="btn-primary" @click="handleSearch">검색하기</button>
        <button class="btn-secondary" @click="resetFilters">초기화</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const searchQuery = ref('')
const selectedCategory = ref('전체')
const selectedSort = ref('latest')
const dateFrom = ref('')
const dateTo = ref('')

const categories = ['전체', '질문', '정보공유', '자유게시판', '프로젝트', '스터디', '채용']

const sortOptions = [
  { value: 'latest', label: '최신순' },
  { value: 'popular', label: '인기순' },
  { value: 'views', label: '조회순' },
  { value: 'comments', label: '댓글순' },
]

const handleSearch = () => {
  console.log('Search:', {
    query: searchQuery.value,
    category: selectedCategory.value,
    sort: selectedSort.value,
    dateFrom: dateFrom.value,
    dateTo: dateTo.value,
  })
  // TODO: API 호출로 검색 결과 가져오기
}

const resetFilters = () => {
  searchQuery.value = ''
  selectedCategory.value = '전체'
  selectedSort.value = 'latest'
  dateFrom.value = ''
  dateTo.value = ''
}
</script>

<style scoped>
.search-view {
  background: #f8f9fa;
  min-height: calc(100vh - 120px);
}

.page-header {
  background: white;
  padding: 1.5rem 1rem;
  border-bottom: 1px solid #e0e0e0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.page-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #2d3748;
}

.search-container {
  padding: 1rem;
}

.search-box {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
  background: white;
  padding: 1rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-box input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
}

.search-box button {
  padding: 0.75rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

.filter-section {
  background: white;
  padding: 1rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.filter-section h3 {
  margin: 0 0 0.75rem 0;
  font-size: 0.9rem;
  color: #666;
  font-weight: 600;
}

.category-chips,
.sort-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.category-chips button,
.sort-buttons button {
  padding: 0.5rem 1rem;
  border: 1px solid #e0e0e0;
  background: white;
  border-radius: 20px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.category-chips button:hover,
.sort-buttons button:hover {
  background: #f5f5f5;
}

.category-chips button.active,
.sort-buttons button.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.date-inputs {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.date-inputs input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 0.9rem;
}

.search-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

.btn-primary,
.btn-secondary {
  padding: 1rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover {
  background: #5568d3;
}

.btn-secondary {
  background: #f5f5f5;
  color: #666;
}

.btn-secondary:hover {
  background: #e0e0e0;
}
</style>
