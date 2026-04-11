import { useAuth } from '../hooks/useAuth'
import { useOrders } from '../hooks/useOrder'
import { useNavigate } from 'react-router-dom'
import { orderApi } from '../api/order'

const STATUS_LABEL: Record<string, string> = {
  PENDING: 'Chờ xử lý', CONFIRMED: 'Đã xác nhận',
  SHIPPING: 'Đang giao', DELIVERED: 'Đã giao', CANCELLED: 'Đã hủy'
}
const STATUS_COLOR: Record<string, string> = {
  PENDING: '#B8975A', CONFIRMED: '#5A8AB8', SHIPPING: '#5AB87A',
  DELIVERED: '#2C2C2C', CANCELLED: '#B85A5A'
}

export default function OrdersPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const { orders, loading, refresh } = useOrders(user?.userId ?? null)

  if (!user) { navigate('/login'); return null }

  const cancel = async (orderCode: string) => {
    await orderApi.cancel(orderCode)
    refresh()
  }

  return (
    <div className="page-enter max-w-3xl mx-auto px-8 py-16">
      <div style={{ textAlign: 'center', marginBottom: '48px' }}>
        <p style={{ fontSize: '0.65rem', letterSpacing: '0.3em', textTransform: 'uppercase', color: 'var(--gold)', marginBottom: '12px' }}>Lịch sử</p>
        <h1 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '2.8rem', fontWeight: 300 }}>Đơn hàng của tôi</h1>
        <div style={{ width: '40px', height: '1px', background: 'var(--gold)', margin: '20px auto 0' }} />
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px', color: 'var(--muted)', letterSpacing: '0.15em', fontSize: '0.8rem', textTransform: 'uppercase' }}>Đang tải...</div>
      ) : orders.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '80px 0' }}>
          <p style={{ color: 'var(--muted)', marginBottom: '24px' }}>Bạn chưa có đơn hàng nào</p>
          <button className="btn-ghost" onClick={() => navigate('/')}>Khám phá bộ sưu tập</button>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {orders.map(o => (
            <div key={o.orderCode}
              style={{ border: '1px solid var(--border)', padding: '24px 28px', background: 'white', transition: 'border-color 0.2s', cursor: 'pointer' }}
              onClick={() => navigate(`/orders/${o.orderCode}`)}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <p style={{ fontSize: '0.7rem', letterSpacing: '0.15em', textTransform: 'uppercase', color: 'var(--muted)', marginBottom: '6px' }}>
                    #{o.orderCode}
                  </p>
                  <p style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.1rem', color: 'var(--charcoal)' }}>
                    {new Date(o.createdAt).toLocaleDateString('vi-VN', { year: 'numeric', month: 'long', day: 'numeric' })}
                  </p>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <span style={{ fontSize: '0.68rem', letterSpacing: '0.12em', textTransform: 'uppercase', color: STATUS_COLOR[o.status], background: `${STATUS_COLOR[o.status]}15`, padding: '4px 10px', border: `1px solid ${STATUS_COLOR[o.status]}30` }}>
                    {STATUS_LABEL[o.status] ?? o.status}
                  </span>
                  <p style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.2rem', color: 'var(--gold-dark)', marginTop: '8px' }}>
                    {o.totalAmount.toLocaleString('vi-VN')}₫
                  </p>
                </div>
              </div>
              {o.status === 'PENDING' && (
                <button onClick={e => { e.stopPropagation(); cancel(o.orderCode) }}
                  style={{ marginTop: '16px', fontSize: '0.7rem', letterSpacing: '0.1em', textTransform: 'uppercase', color: '#B85A5A', background: 'none', border: 'none', cursor: 'pointer', textDecoration: 'underline' }}>
                  Hủy đơn hàng
                </button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}