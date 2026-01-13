<script setup lang="ts">
import { marketService, type ProductCreateRequest } from '@/services/market'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = ref<ProductCreateRequest>({
  title: '',
  description: '',
  price: 0,
  category: 'PC_FULL',
})

const categories = [
  'PC_FULL',
  'CPU',
  'GPU',
  'RAM',
  'MAINBOARD',
  'SSD_HDD',
  'CASE',
  'POWER',
  'MONITOR',
  'GEAR',
  'SOFTWARE',
  'ETC',
]

const submit = async () => {
  try {
    await marketService.createProduct(form.value)
    alert('Product registered!')
    router.push('/market')
  } catch (e) {
    alert('Failed to create product')
    console.error(e)
  }
}
</script>

<template>
  <div class="create-view">
    <h1>Sell Item</h1>
    <form @submit.prevent="submit">
      <div class="form-group">
        <label>Title</label>
        <input v-model="form.title" required placeholder="Item Name" />
      </div>

      <div class="form-group">
        <label>Category</label>
        <select v-model="form.category">
          <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
        </select>
      </div>

      <div class="form-group">
        <label>Price</label>
        <input type="number" v-model="form.price" required />
      </div>

      <div class="form-group">
        <label>Description</label>
        <textarea v-model="form.description" rows="5"></textarea>
      </div>

      <button type="submit" class="btn-primary">Register Item</button>
    </form>
  </div>
</template>

<style scoped>
.create-view {
  max-width: 600px;
  margin: 0 auto;
  padding: 24px;
}
.form-group {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
}
input,
select,
textarea {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-top: 4px;
}
.btn-primary {
  background-color: #2196f3;
  color: white;
  border: none;
  padding: 12px;
  width: 100%;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
}
</style>
