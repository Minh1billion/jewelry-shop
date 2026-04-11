import { useState } from 'react'
import { useAllOrders } from '../hooks/useOrder'
import { useActiveShippers } from '../hooks/useShipper'
import { orderApi } from '../api/order'
import { shipperApi } from '../api/shipper'

const STATUS_LABEL: Record<string, string> = {
  PENDING: 'Chờ xử lý', CONFIRMED: 'Đã xác nhận',
  SHIPPING: 'Đang giao', DELIVERED: 'Đã giao', CANCELLED: 'Đã hủy'
}
const STATUS_COLOR: Record<string, string> = {
  PENDING: '#B8975A', CONFIRMED: '#5A8AB8', SHIPPING: '#5AB87A',
  DELIVERED: '#2C2C2C', CANCELLED: '#B85A5A'
}

export default function AdminOrdersPage() {
  const { orders, loading, refresh } = useAllOrders()
  const { shippers } = useActiveShippers()
  const [selectedShipper, setSelectedShipper] = useState<Record<string, number>>({})
  const [error, setError] = useState('')

  const confirm = async (orderCode: string) => {
    try {
      await orderApi.confirm(orderCode)
      refresh()
    } catch (e: any) {
      setError(e.message)
    }
  }

  const assign = async (orderCode: string) => {
    const shipperId = selectedShipper[orderCode]
    if (!shipperId) return setError('Vui lòng chọn shipper')
    try {
      await shipperApi.assignToOrder(orderCode, shipperId)
      refresh()
    } catch (e: any) {
      setError(e.message)
    }
  }

  return (
    <div className="max-w-5xl mx-auto px-8 py-16">
      <div style={{ textAlign: 'center', marginBottom: '48px' }}>
        <p style={{ fontSize: '0.65rem', letterSpacing: '0.3em', textTransform: 'uppercase', color: 'var(--gold)', marginBottom: '12px' }}>Quản lý</p>
        <h1 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '2.8rem', fontWeight: 300 }}>Đơn hàng</h1>
        <div style={{ width: '40px', height: '1px', background: 'var(--gold)', margin: '20px auto 0' }} />
      </div>

      {error && (
        <p style={{ color: '#B85A5A', fontSize: '0.8rem', textAlign: 'center', marginBottom: '16px' }}>{error}</p>
      )}

      {loading ? (
        <p style={{ textAlign: 'center', color: 'var(--muted)', letterSpacing: '0.15em', fontSize: '0.8rem' }}>Đang tải...</p>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {orders.map(o => (
            <div key={o.orderCode} style={{ border: '1px solid var(--border)', padding: '24px 28px', background: 'white' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
                <div>
                  <p style={{ fontSize: '0.7rem', letterSpacing: '0.15em', textTransform: 'uppercase', color: 'var(--muted)', marginBottom: '4px' }}>
                    #{o.orderCode}
                  </p>
                  <p style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.1rem' }}>
                    {o.recipientName} — {o.recipientPhone}
                  </p>
                  <p style={{ fontSize: '0.8rem', color: 'var(--muted)', marginTop: '2px' }}>{o.shippingAddress}</p>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <span style={{ fontSize: '0.68rem', letterSpacing: '0.12em', textTransform: 'uppercase', color: STATUS_COLOR[o.status], background: `${STATUS_COLOR[o.status]}15`, padding: '4px 10px', border: `1px solid ${STATUS_COLOR[o.status]}30` }}>
                    {STATUS_LABEL[o.status]}
                  </span>
                  <p style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.2rem', color: 'var(--gold-dark)', marginTop: '8px' }}>
                    {o.totalAmount.toLocaleString('vi-VN')}₫
                  </p>
                </div>
              </div>

              {o.shipper && (
                <p style={{ fontSize: '0.75rem', color: 'var(--muted)', marginBottom: '12px' }}>
                  Shipper: {o.shipper.fullName} — {o.shipper.phone}
                </p>
              )}

              <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
                {o.status === 'PENDING' && (
                  <button onClick={() => confirm(o.orderCode)}
                    style={{ fontSize: '0.7rem', letterSpacing: '0.1em', textTransform: 'uppercase', padding: '8px 20px', background: 'var(--charcoal)', color: 'white', border: 'none', cursor: 'pointer' }}>
                    Xác nhận đơn
                  </button>
                )}

                {o.status === 'CONFIRMED' && (
                  <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                    <select
                      value={selectedShipper[o.orderCode] ?? ''}
                      onChange={e => setSelectedShipper(prev => ({ ...prev, [o.orderCode]: Number(e.target.value) }))}
                      style={{ fontSize: '0.75rem', padding: '8px 12px', border: '1px solid var(--border)', background: 'white' }}>
                      <option value=''>Chọn shipper</option>
                      {shippers.map(s => (
                        <option key={s.shipperId} value={s.shipperId}>{s.fullName} — {s.phone}</option>
                      ))}
                    </select>
                    <button onClick={() => assign(o.orderCode)}
                      style={{ fontSize: '0.7rem', letterSpacing: '0.1em', textTransform: 'uppercase', padding: '8px 20px', background: 'var(--gold)', color: 'white', border: 'none', cursor: 'pointer' }}>
                      Gán shipper
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}