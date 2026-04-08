import { useState, useEffect } from 'react'
import { cartApi } from '../api/cart'
import type { CartItem } from '../types'

export function useCart(userId: number | null) {
  const [items, setItems] = useState<CartItem[]>([])
  const [loading, setLoading] = useState(false)

  const fetch = () => {
    if (!userId) return
    setLoading(true)
    cartApi.getCart(userId).then(setItems).finally(() => setLoading(false))
  }

  useEffect(() => { fetch() }, [userId])

  const add = async (productId: number, quantity: number) => {
    if (!userId) return
    await cartApi.addToCart(userId, productId, quantity)
    fetch()
  }

  const update = async (cartItemId: number, quantity: number) => {
    await cartApi.updateQuantity(cartItemId, quantity)
    fetch()
  }

  const remove = async (cartItemId: number) => {
    await cartApi.removeItem(cartItemId)
    fetch()
  }

  return { items, loading, add, update, remove, refetch: fetch }
}