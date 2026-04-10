import { useState, useEffect } from 'react'
import { shipperApi } from '../api/shipper'
import type { Shipper } from '../types'

export function useShippers() {
  const [shippers, setShippers] = useState<Shipper[]>([])
  const [loading, setLoading] = useState(false)

  const fetch = () => {
    setLoading(true)
    shipperApi.getAll().then(setShippers).finally(() => setLoading(false))
  }

  useEffect(() => { fetch() }, [])

  return { shippers, loading, refresh: fetch }
}

export function useActiveShippers() {
  const [shippers, setShippers] = useState<Shipper[]>([])

  useEffect(() => {
    shipperApi.getActive().then(setShippers)
  }, [])

  return { shippers }
}