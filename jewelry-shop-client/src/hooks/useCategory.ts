import { useState, useEffect } from 'react'
import { categoryApi } from '../api/category'
import type { Category } from '../types'

export function useCategories() {
  const [data, setData] = useState<Category[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    categoryApi.getAll().then(setData).finally(() => setLoading(false))
  }, [])

  return { data, loading }
}