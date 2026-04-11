import React, { useState } from 'react'
import { useGenerateReport, useExportReport } from '../hooks/useReport'
import { ReportType } from '../types'
import { useAuth } from '../hooks/useAuth'

const ReportPage: React.FC = () => {
  const { user, loading: authLoading } = useAuth()
  const adminId = user?.userId ?? null

  const [fromDate, setFromDate] = useState('')
  const [toDate, setToDate] = useState('')
  const [reportType, setReportType] = useState<ReportType>('DAILY')

  const [filename, setFilename] = useState('doanh_thu')
  const [exportFormat, setExportFormat] = useState<'pdf' | 'csv' | 'excel'>('pdf')

  const { data: currentReport, isPending: isGenerating, error: generateError, mutateAsync: generateReport } = useGenerateReport()
  const { isPending: isExporting, error: exportError, mutateAsync: exportReport } = useExportReport()

  const currentReportId = currentReport?.reportId ?? null

  if (!authLoading && !adminId) {
    return (
      <div className="min-h-screen bg-[#FAF7F2] flex items-center justify-center px-6">
        <div className="text-center max-w-md">
          <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-[#E2D9CC] flex items-center justify-center">
            <span className="text-3xl">🔒</span>
          </div>
          <h2 className="serif text-3xl font-light mb-3">Yêu cầu đăng nhập</h2>
          <p className="text-[#8A8480] mb-6">Vui lòng đăng nhập để xem thống kê doanh thu.</p>
          <button className="btn-primary" onClick={() => window.location.href = '/login'}>
            Đăng nhập ngay
          </button>
        </div>
      </div>
    )
  }

  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!adminId) {
      alert('Bạn cần đăng nhập để tạo báo cáo')
      return
    }
    if (!fromDate || !toDate) {
      alert('Vui lòng chọn đầy đủ ngày bắt đầu và kết thúc')
      return
    }
    if (new Date(fromDate) > new Date(toDate)) {
      alert('Mốc thời gian không hợp lệ, vui lòng nhập lại')
      return
    }

    try {
      await generateReport({ fromDate, toDate, reportType, adminId })
    } catch (err: any) {
      alert(err.message)
    }
  }

  const handleExport = async () => {
    if (!currentReportId) {
      alert('Chưa có báo cáo nào được tạo. Hãy tạo báo cáo trước.')
      return
    }
    if (!filename.trim()) {
      alert('Tên file không hợp lệ, vui lòng nhập lại')
      return
    }
    const nameRegex = /^[\w\-. ]+$/
    if (!nameRegex.test(filename)) {
      alert('Tên file không hợp lệ, vui lòng nhập lại')
      return
    }

    try {
      await exportReport({ reportId: currentReportId, format: exportFormat, filename })
    } catch (err: any) {
      alert(err.message)
    }
  }

  return (
    <div className="min-h-screen bg-[#FAF7F2] py-12 px-6">
      <div className="max-w-3xl mx-auto page-enter">
        <div className="text-center mb-12">
          <span className="tag inline-block mb-3">Jewelry Analytics</span>
          <h1 className="serif text-4xl md:text-5xl font-light tracking-wide text-[#2C2C2C]">
            Thống kê doanh thu
          </h1>
          <div className="w-16 h-px bg-[#B8975A] mx-auto mt-4"></div>
        </div>

        <form onSubmit={handleGenerate} className="bg-white border border-[#E2D9CC] p-8 mb-10 shadow-sm">
          <h2 className="serif text-2xl font-medium text-[#2C2C2C] mb-6">Tạo báo cáo</h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
            <div>
              <label className="block text-[0.7rem] uppercase tracking-[0.2em] text-[#8A8480] mb-2">
                Từ ngày
              </label>
              <input
                type="date"
                value={fromDate}
                onChange={(e) => setFromDate(e.target.value)}
                required
                className="input-luxury w-full"
              />
            </div>
            <div>
              <label className="block text-[0.7rem] uppercase tracking-[0.2em] text-[#8A8480] mb-2">
                Đến ngày
              </label>
              <input
                type="date"
                value={toDate}
                onChange={(e) => setToDate(e.target.value)}
                required
                className="input-luxury w-full"
              />
            </div>
          </div>

          <div className="mb-8">
            <label className="block text-[0.7rem] uppercase tracking-[0.2em] text-[#8A8480] mb-2">
              Kiểu thống kê
            </label>
            <select
              value={reportType}
              onChange={(e) => setReportType(e.target.value as ReportType)}
              className="w-full bg-transparent border-b border-[#E2D9CC] py-3 font-jost text-[0.9rem] text-[#2C2C2C] outline-none focus:border-[#B8975A]"
            >
              <option value="DAILY">Theo ngày</option>
              <option value="WEEKLY">Theo tuần</option>
              <option value="MONTHLY">Theo tháng</option>
              <option value="YEARLY">Theo năm</option>
            </select>
          </div>

          <button
            type="submit"
            disabled={isGenerating || !adminId}
            className="btn-primary w-full md:w-auto"
          >
            {isGenerating ? 'Đang tạo...' : 'Tạo báo cáo'}
          </button>

          {generateError && (
            <p className="text-red-600 text-sm mt-4">{generateError.message}</p>
          )}
        </form>

        {currentReport && (
          <div className="bg-white border border-[#E2D9CC] p-8 mb-10 shadow-sm">
            <h2 className="serif text-2xl font-medium text-[#2C2C2C] mb-6">Báo cáo chi tiết</h2>
            <div className="space-y-3 text-[#2C2C2C]">
              <div className="flex justify-between border-b border-[#E2D9CC] pb-2">
                <span className="text-[0.75rem] uppercase tracking-wide text-[#8A8480]">Khoảng thời gian</span>
                <span className="font-medium">{currentReport.fromDate} → {currentReport.toDate}</span>
              </div>
              <div className="flex justify-between border-b border-[#E2D9CC] pb-2">
                <span className="text-[0.75rem] uppercase tracking-wide text-[#8A8480]">Kiểu báo cáo</span>
                <span className="font-medium">{currentReport.reportType}</span>
              </div>
              <div className="flex justify-between border-b border-[#E2D9CC] pb-2">
                <span className="text-[0.75rem] uppercase tracking-wide text-[#8A8480]">Tổng doanh thu</span>
                <span className="font-serif text-xl text-[#B8975A]">
                  {currentReport.totalRevenue?.toLocaleString()} ₫
                </span>
              </div>
              <div className="flex justify-between border-b border-[#E2D9CC] pb-2">
                <span className="text-[0.75rem] uppercase tracking-wide text-[#8A8480]">Tổng đơn hàng</span>
                <span className="font-medium">{currentReport.totalOrders}</span>
              </div>
              <div className="flex justify-between pt-2">
                <span className="text-[0.75rem] uppercase tracking-wide text-[#8A8480]">Người tạo</span>
                <span className="text-sm">{currentReport.createdBy?.username || `ID ${currentReport.createdBy?.userId}`}</span>
              </div>
            </div>
          </div>
        )}

        {currentReportId && (
          <div className="bg-white border border-[#E2D9CC] p-8 shadow-sm">
            <h2 className="serif text-2xl font-medium text-[#2C2C2C] mb-6">Xuất báo cáo</h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
              <div>
                <label className="block text-[0.7rem] uppercase tracking-[0.2em] text-[#8A8480] mb-2">
                  Tên file
                </label>
                <input
                  type="text"
                  value={filename}
                  onChange={(e) => setFilename(e.target.value)}
                  placeholder="doanh_thu_thang_1"
                  className="input-luxury w-full"
                />
                <p className="text-[0.65rem] text-[#8A8480] mt-1">
                  Chỉ chữ, số, dấu gạch, dấu chấm và khoảng trắng
                </p>
              </div>
              <div>
                <label className="block text-[0.7rem] uppercase tracking-[0.2em] text-[#8A8480] mb-2">
                  Định dạng
                </label>
                <select
                  value={exportFormat}
                  onChange={(e) => setExportFormat(e.target.value as any)}
                  className="w-full bg-transparent border-b border-[#E2D9CC] py-3 font-jost text-[0.9rem] text-[#2C2C2C] outline-none focus:border-[#B8975A]"
                >
                  <option value="pdf">PDF</option>
                  <option value="csv">CSV</option>
                  <option value="excel">Excel</option>
                </select>
              </div>
            </div>

            <button
              onClick={handleExport}
              disabled={isExporting}
              className="btn-ghost w-full md:w-auto"
            >
              {isExporting ? 'Đang xuất...' : 'Xuất file'}
            </button>

            {exportError && (
              <p className="text-red-600 text-sm mt-4">{exportError.message}</p>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

export default ReportPage