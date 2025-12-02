<template>
  <div class="search-view">
    <div class="page-header">
      <h2>🔍 게시글 검색</h2>
    </div>

    <div class="search-container">
      <SearchBox v-model="searchQuery" @search="handleSearch" />

      <FilterSection title="카테고리">
        <CategoryChips v-model="selectedCategory" :categories="categories" />
      </FilterSection>

      <FilterSection title="정렬">
        <SortButtons v-model="selectedSort" :options="sortOptions" />
      </FilterSection>

      <FilterSection title="기간">
        <DateRangeInputs v-model:from="dateFrom" v-model:to="dateTo" />
      </FilterSection>

      <SearchActions @search="handleSearch" @reset="resetFilters" />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * @fileoverview 게시글 검색 페이지. 필터 조합과 검색 액션을 구성합니다.
 */
import CategoryChips from '@/views/search/components/CategoryChips.vue'
import DateRangeInputs from '@/views/search/components/DateRangeInputs.vue'
import FilterSection from '@/views/search/components/FilterSection.vue'
import SearchActions from '@/views/search/components/SearchActions.vue'
import SearchBox from '@/views/search/components/SearchBox.vue'
import SortButtons from '@/views/search/components/SortButtons.vue'
import { useSearchFilters } from '@/views/search/composables/useSearchFilters'

/**
 * 검색 질의, 카테고리, 기간 필터 등 사용자 입력 상태를 관리합니다.
 * @see useSearchFilters
 */
const {
  searchQuery,
  selectedCategory,
  selectedSort,
  dateFrom,
  dateTo,
  categories,
  sortOptions,
  handleSearch,
  resetFilters,
} = useSearchFilters()
</script>

<style src="@/views/search/styles/search-view.css"></style>
