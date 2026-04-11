import { useState, useEffect } from 'react'
import { productApi } from '../api/product'
import type { Page } from '../api/product'
import type { Product } from '../types'

export function useProducts(params: Parameters<typeof productApi.getAll>[0]) {
  const [data, setData] = useState<Page<Product> | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    productApi.getAll(params).then(setData).finally(() => setLoading(false))
  }, [JSON.stringify(params)])

  return { data, loading }
}

export function useProduct(id: number | null) {
  const [data, setData] = useState<Product | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (id === null || isNaN(id)) return
    setLoading(true)
    productApi.getById(id).then(setData).finally(() => setLoading(false))
  }, [id])

  return { data, loading }
}