import { createContext, useContext, useState, type ReactNode } from 'react'
import { authApi } from '../api/auth'
import type { User } from '../types'

interface AuthContextType {
  user: User | null
  loading: boolean
  error: string | null
  login: (username: string, password: string) => Promise<User | undefined>
  register: (data: Parameters<typeof authApi.register>[0]) => Promise<any>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
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

  return (
    <AuthContext.Provider value={{ user, loading, error, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}