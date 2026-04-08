import { request } from './base'
import type { User } from '../types'

export const authApi = {
  register: (data: { username: string; email: string; password: string; fullName: string; phone: string }) =>
    request<User>('/auth/register', { method: 'POST', body: JSON.stringify(data) }),

  login: (username: string, password: string) =>
    request<User>('/auth/login', { method: 'POST', body: JSON.stringify({ username, password }) }),
}