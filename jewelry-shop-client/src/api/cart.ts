import { request } from './base'
import type { CartItem } from '../types'

export const cartApi = {
  getCart: (userId: number) => request<CartItem[]>(`/cart?userId=${userId}`),

  addToCart: (userId: number, productId: number, quantity: number) =>
    request('/cart/add', { method: 'POST', body: JSON.stringify({ userId, productId, quantity }) }),

  updateQuantity: (cartItemId: number, quantity: number) =>
    request(`/cart/${cartItemId}`, { method: 'PUT', body: JSON.stringify({ quantity }) }),

  removeItem: (cartItemId: number) =>
    request(`/cart/${cartItemId}`, { method: 'DELETE' }),
}