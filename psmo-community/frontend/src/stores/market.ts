import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useMarketStore = defineStore('market', () => {
  const categoryFilter = ref<string | undefined>(undefined)
  const searchQuery = ref<string>('')

  function setCategory(category: string | undefined) {
    categoryFilter.value = category
  }

  return { categoryFilter, searchQuery, setCategory }
})
