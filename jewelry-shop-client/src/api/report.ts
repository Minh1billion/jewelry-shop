import { request } from './base'; 
import { RevenueReport, ReportType } from '../types';

export interface GenerateReportRequest {
  fromDate: string;   // YYYY-MM-DD
  toDate: string;
  reportType: ReportType;
  adminId: number;
}


export async function generateReport(data: GenerateReportRequest): Promise<RevenueReport> {
  return request<RevenueReport>('/admin/reports/generate', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}


export async function exportReport(reportId: number, format: 'pdf' | 'csv' | 'excel', filename: string): Promise<void> {
  const baseUrl = '/api'; // hoặc import BASE từ request
  const url = `${baseUrl}/admin/reports/${reportId}/export?format=${format}&filename=${encodeURIComponent(filename)}`;
  const response = await fetch(url, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText);
  }

  const blob = await response.blob();
  const downloadUrl = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = downloadUrl;

  const extension = format === 'excel' ? 'xlsx' : format;
  a.download = `${filename}.${extension}`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(downloadUrl);
}