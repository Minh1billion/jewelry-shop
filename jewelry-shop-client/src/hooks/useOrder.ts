import { useState, useEffect } from 'react'
import { orderApi } from '../api/order'
import type { Order } from '../types'

export function useOrders(userId: number | null) {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!userId) return
    setLoading(true)
    orderApi.getMyOrders(userId).then(setOrders).finally(() => setLoading(false))
  }, [userId])

  return { orders, loading }
}

export function useOrderDetail(orderCode: string) {
  const [order, setOrder] = useState<Order | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    orderApi.getDetail(orderCode).then(setOrder).finally(() => setLoading(false))
  }, [orderCode])

  return { order, loading }
}

export function useAllOrders() {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(false)

  const fetch = () => {
    setLoading(true)
    orderApi.getAll().then(setOrders).finally(() => setLoading(false))
  }

  useEffect(() => { fetch() }, [])

  return { orders, loading, refresh: fetch }
}