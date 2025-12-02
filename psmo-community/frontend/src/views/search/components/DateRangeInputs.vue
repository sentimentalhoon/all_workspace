<template>
  <div class="date-inputs">
    <input :value="from" type="date" @input="onInput('from', $event)" />
    <span>~</span>
    <input :value="to" type="date" @input="onInput('to', $event)" />
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{ from: string; to: string }>()

const emit = defineEmits<{
  (e: 'update:from', value: string): void
  (e: 'update:to', value: string): void
}>()

const onInput = (field: 'from' | 'to', event: Event) => {
  const value = (event.target as HTMLInputElement).value
  if (field === 'from') {
    emit('update:from', value)
    return
  }
  emit('update:to', value)
}
</script>
