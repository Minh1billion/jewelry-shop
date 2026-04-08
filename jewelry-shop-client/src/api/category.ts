import { request } from './base'
import type { Category } from '../types'

export const categoryApi = {
  getAll: () => request<Category[]>('/categories'),
}