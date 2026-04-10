import { request } from './base'
import type { Order } from '../types'

export const orderApi = {
  getMyOrders: (userId: number) => request<Order[]>(`/orders?userId=${userId}`),

  getDetail: (orderCode: string) => request<Order>(`/orders/${orderCode}`),

  getAll: () => request<Order[]>('/orders/all'),

  confirm: (orderCode: string) =>
    request(`/orders/${orderCode}/confirm`, { method: 'PUT' }),

  cancel: (orderCode: string) =>
    request(`/orders/${orderCode}/cancel`, { method: 'PUT' }),

  checkout: (data: {
    userId: number
    selectedItemIds: number[]
    recipientName: string
    recipientPhone: string
    shippingAddress: string
    note?: string
  }) => request<Order>('/checkout', { method: 'POST', body: JSON.stringify(data) }),
}