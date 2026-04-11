import { useState } from 'react'
import { reportApi, GenerateReportRequest } from '../api/report'
import type { RevenueReport } from '../types'

export function useGenerateReport() {
  const [data, setData] = useState<RevenueReport | null>(null)
  const [isPending, setIsPending] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const mutateAsync = async (params: GenerateReportRequest) => {
    setIsPending(true)
    setError(null)
    try {
      const result = await reportApi.generate(params)
      setData(result)
      return result
    } catch (err) {
      const errorObj = err instanceof Error ? err : new Error(String(err))
      setError(errorObj)
      throw errorObj
    } finally {
      setIsPending(false)
    }
  }

  return { data, isPending, error, mutateAsync }
}

export function useExportReport() {
  const [isPending, setIsPending] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const mutateAsync = async (params: {
    reportId: number
    format: 'pdf' | 'csv' | 'excel'
    filename: string
  }) => {
    setIsPending(true)
    setError(null)
    try {
      await reportApi.export(params.reportId, params.format, params.filename)
    } catch (err) {
      const errorObj = err instanceof Error ? err : new Error(String(err))
      setError(errorObj)
      throw errorObj
    } finally {
      setIsPending(false)
    }
  }

  return { isPending, error, mutateAsync }
}