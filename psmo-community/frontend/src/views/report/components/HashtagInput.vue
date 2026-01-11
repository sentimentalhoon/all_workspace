<template>
  <div class="hashtag-input">
    <div class="tags-container">
      <span v-for="(tag, index) in modelValue" :key="tag" class="tag-chip">
        #{{ tag }}
        <button type="button" class="btn-remove" @click="removeTag(index)">×</button>
      </span>
      <input
        v-model="inputValue"
        type="text"
        placeholder="해시태그 입력 (Enter로 추가)"
        class="input-field"
        @keydown.enter.prevent="addTag"
        @keydown.backspace="handleBackspace"
      />
    </div>
    <p class="helper-text">자동 생성된 태그를 수정하거나 추가할 수 있습니다.</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps({
  modelValue: {
    type: Array as () => string[],
    default: () => [],
  },
})

const emit = defineEmits<{
  (event: 'update:modelValue', value: string[]): void
}>()

const inputValue = ref('')

const addTag = () => {
  const val = inputValue.value.trim()
  if (!val) return

  // Prevent duplicates
  if (!props.modelValue.includes(val)) {
    emit('update:modelValue', [...props.modelValue, val])
  }
  inputValue.value = ''
}

const removeTag = (index: number) => {
  const newTags = [...props.modelValue]
  newTags.splice(index, 1)
  emit('update:modelValue', newTags)
}

const handleBackspace = (e: KeyboardEvent) => {
  if (!inputValue.value && props.modelValue.length > 0) {
    removeTag(props.modelValue.length - 1)
  }
}
</script>

<style scoped>
.hashtag-input {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background-color: #fff;
  transition: border-color 0.2s;
}

.tags-container:focus-within {
  border-color: #3b82f6;
  box-shadow: 0 0 0 1px #3b82f6;
}

.tag-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background-color: #eff6ff;
  color: #2563eb;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
}

.btn-remove {
  border: none;
  background: none;
  color: #93c5fd;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-remove:hover {
  color: #1d4ed8;
}

.input-field {
  border: none;
  outline: none;
  background: transparent;
  flex: 1;
  min-width: 120px;
  font-size: 14px;
  padding: 4px 0;
  color: #1e293b;
}

.input-field::placeholder {
  color: #94a3b8;
}

.helper-text {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
}
</style>
