import { request } from './base'
import type { Product, Page } from '../types'

export const productApi = {
  getAll: (params: { keyword?: string; categoryId?: number; minPrice?: number; maxPrice?: number; page?: number; size?: number }) => {
    const q = new URLSearchParams()
    Object.entries(params).forEach(([k, v]) => v !== undefined && q.set(k, String(v)))
    return request<Page<Product>>(`/products?${q}`)
  },

  getById: (id: number) => request<Product>(`/products/${id}`),
}