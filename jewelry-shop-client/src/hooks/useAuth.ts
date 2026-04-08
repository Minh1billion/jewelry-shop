import { useState } from 'react'
import { authApi } from '../api/auth'
import type { User } from '../types'

export function useAuth() {
  const [user, setUser] = useState<User | null>(() => {
    const s = sessionStorage.getItem('user')
    return s ? JSON.parse(s) : null
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const login = async (username: string, password: string) => {
    setLoading(true)
    setError(null)
    try {
      const u = await authApi.login(username, password)
      sessionStorage.setItem('user', JSON.stringify(u))
      setUser(u)
      return u
    } catch (e: any) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  const register = async (data: Parameters<typeof authApi.register>[0]) => {
    setLoading(true)
    setError(null)
    try {
      return await authApi.register(data)
    } catch (e: any) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    sessionStorage.removeItem('user')
    setUser(null)
  }

  return { user, loading, error, login, register, logout }
}