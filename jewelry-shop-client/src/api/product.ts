import { request } from './base'
import type { Product } from '../types'

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export const productApi = {
  getAll: (params: {
    keyword?: string
    categoryId?: number
    minPrice?: number
    maxPrice?: number
    page?: number
    size?: number
  }) => {
    const q = new URLSearchParams()
    Object.entries(params).forEach(([k, v]) => v !== undefined && q.set(k, String(v)))
    return request<Page<Product>>(`/products?${q}`)
  },

  getById: (id: number) =>
    request<Product>(`/products/${id}`),

  create: (data: Partial<Product>) =>
    request<Product>('/products', { method: 'POST', body: JSON.stringify(data) }),

  update: (id: number, data: Partial<Product>) =>
    request<Product>(`/products/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  delete: (id: number) =>
    request<void>(`/products/${id}`, { method: 'DELETE' }),

  getRecommendations: (userId?: number) => {
    const q = userId !== undefined ? `?userId=${userId}` : ''
    return request<Product[]>(`/products/recommendations${q}`)
  },
}