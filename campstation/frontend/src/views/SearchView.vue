<script setup lang="ts">
import { ref } from 'vue'

const searchQuery = ref('')
const selectedRegion = ref('')
const checkIn = ref('')
const checkOut = ref('')

const regions = ['전체', '강원도', '경기도', '경상도', '전라도', '충청도', '제주도']

const handleSearch = () => {
  console.log('Search:', { searchQuery: searchQuery.value, selectedRegion: selectedRegion.value })
}
</script>

<template>
  <div class="search-page">
    <header class="page-header">
      <h1>캠핑장 검색</h1>
    </header>

    <div class="search-container">
      <div class="search-box">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="캠핑장 이름, 지역 검색"
          class="search-input"
        />
        <button @click="handleSearch" class="search-btn">🔍</button>
      </div>

      <div class="filter-section">
        <h3>지역</h3>
        <div class="region-chips">
          <button
            v-for="region in regions"
            :key="region"
            :class="['chip', { active: selectedRegion === region }]"
            @click="selectedRegion = region"
          >
            {{ region }}
          </button>
        </div>
      </div>

      <div class="filter-section">
        <h3>날짜</h3>
        <div class="date-inputs">
          <div class="input-group">
            <label>체크인</label>
            <input v-model="checkIn" type="date" class="date-input" />
          </div>
          <div class="input-group">
            <label>체크아웃</label>
            <input v-model="checkOut" type="date" class="date-input" />
          </div>
        </div>
      </div>

      <button class="search-button">검색하기</button>
    </div>
  </div>
</template>

<style scoped>
.search-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-header {
  background: white;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
  position: sticky;
  top: 0;
  z-index: 10;
}

.page-header h1 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 700;
  color: #2d3748;
}

.search-container {
  padding: 1.5rem;
}

.search-box {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
}

.search-input {
  flex: 1;
  padding: 0.875rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
}

.search-btn {
  padding: 0.875rem 1.25rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.25rem;
  cursor: pointer;
}

.filter-section {
  margin-bottom: 2rem;
}

.filter-section h3 {
  font-size: 1rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 1rem 0;
}

.region-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.chip {
  padding: 0.625rem 1.25rem;
  border: 1px solid #dee2e6;
  border-radius: 20px;
  background: white;
  color: #495057;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.chip:hover {
  border-color: #667eea;
  color: #667eea;
}

.chip.active {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

.date-inputs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-group label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #4a5568;
}

.date-input {
  padding: 0.875rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.95rem;
}

.date-input:focus {
  outline: none;
  border-color: #667eea;
}

.search-button {
  width: 100%;
  padding: 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.05rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.search-button:hover {
  background: #5a67d8;
}
</style>
