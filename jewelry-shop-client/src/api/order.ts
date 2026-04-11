import { request } from './base'
import type { Order } from '../types'

export const orderApi = {
  getMyOrders: (userId: number) =>
    request<Order[]>(`/orders?userId=${userId}`),

  getDetail: (orderCode: string) =>
    request<Order>(`/orders/${orderCode}`),

  getAll: () =>
    request<Order[]>('/orders/all'),

  confirm: (orderCode: string) =>
    request(`/orders/${orderCode}/confirm`, { method: 'PUT' }),

  cancel: (orderCode: string) =>
    request(`/orders/${orderCode}/cancel`, { method: 'PUT' }),
}