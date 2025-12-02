import { categories, sortOptions } from '@/views/search/constants'
import { ref } from 'vue'

const defaultCategory = categories[0] ?? '전체'
const defaultSort = sortOptions[0]?.value ?? 'latest'

export function useSearchFilters() {
  const searchQuery = ref('')
  const selectedCategory = ref<string>(defaultCategory)
  const selectedSort = ref<string>(defaultSort)
  const dateFrom = ref('')
  const dateTo = ref('')

  const handleSearch = () => {
    console.info('Search filters (placeholder)', {
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
    selectedCategory.value = defaultCategory
    selectedSort.value = defaultSort
    dateFrom.value = ''
    dateTo.value = ''
  }

  return {
    searchQuery,
    selectedCategory,
    selectedSort,
    dateFrom,
    dateTo,
    categories,
    sortOptions,
    handleSearch,
    resetFilters,
  }
}
