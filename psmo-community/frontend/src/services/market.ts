import { fetchClient } from '@/utils/api'

export interface Product {
  id: number
  title: string
  description: string | null
  price: number
  status: 'SALE' | 'RESERVED' | 'SOLD'
  category: string
  seller: {
    id: number
    displayName: string | null
    username: string | null
    photoUrl: string | null
  }
  viewCount: number
  createdAt: string
  updatedAt: string
}

export interface ProductCreateRequest {
  title: string
  description?: string
  price: number
  category: string
}

export interface ProductResponse<T> {
  status: string
  data: T
}

export const marketService = {
  async getProducts(page = 1, size = 20, category?: string) {
    const params = new URLSearchParams()
    params.append('page', page.toString())
    params.append('size', size.toString())
    if (category) params.append('category', category)

    const response = await fetchClient(`/api/v1/market/products?${params.toString()}`)
    const json = (await response.json()) as ProductResponse<Product[]>
    return json
  },

  async getProduct(id: number) {
    const response = await fetchClient(`/api/v1/market/products/${id}`)
    const json = (await response.json()) as ProductResponse<Product>
    return json
  },

  async createProduct(data: ProductCreateRequest) {
    const response = await fetchClient('/api/v1/market/products', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    })
    const json = (await response.json()) as ProductResponse<Product>
    return json
  },
}
