import { request } from './base'
import type { Shipper, Order } from '../types'

export const shipperApi = {
  getAll: () => request<Shipper[]>('/admin/shippers'),
  getActive: () => request<Shipper[]>('/admin/shippers/active'),

  create: (data: { fullName: string; phone: string; email: string }) =>
    request<Shipper>('/admin/shippers', { method: 'POST', body: JSON.stringify(data) }),

  update: (id: number, data: { fullName: string; phone: string; email: string; status: string }) =>
    request<Shipper>(`/admin/shippers/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  assignToOrder: (orderCode: string, shipperId: number) =>
    request(`/admin/shippers/assign/${orderCode}`, { method: 'POST', body: JSON.stringify({ shipperId }) }),

  updateDeliveryStatus: (orderCode: string, shipperId: number, status: string) =>
    request(`/admin/shippers/delivery/${orderCode}/status?shipperId=${shipperId}`, {
      method: 'PUT', body: JSON.stringify({ status })
    }),
}