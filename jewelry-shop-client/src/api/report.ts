import { request } from './base'
import type { RevenueReport, ReportType } from '../types'

export interface GenerateReportRequest {
  fromDate: string  // YYYY-MM-DD
  toDate: string
  reportType: ReportType
  adminId: number
}

export const reportApi = {
  generate: (data: GenerateReportRequest) =>
    request<RevenueReport>('/admin/reports/generate', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  export: async (reportId: number, format: 'pdf' | 'csv' | 'excel', filename: string): Promise<void> => {
    const url = `/api/admin/reports/${reportId}/export?format=${format}&filename=${encodeURIComponent(filename)}`
    const res = await fetch(url)

    if (!res.ok) throw new Error(await res.text())

    const blob = await res.blob()
    const downloadUrl = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = downloadUrl
    a.download = `${filename}.${format === 'excel' ? 'xlsx' : format}`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(downloadUrl)
  },
}