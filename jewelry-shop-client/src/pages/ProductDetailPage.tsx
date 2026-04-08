import { useParams, useNavigate } from 'react-router-dom'
import { useOrderDetail } from '../hooks/useOrder'

const STATUS_STEPS = ['PENDING', 'CONFIRMED', 'SHIPPING', 'DELIVERED']
const STATUS_LABEL: Record<string, string> = {
  PENDING: 'Chờ xử lý', CONFIRMED: 'Đã xác nhận',
  SHIPPING: 'Đang giao', DELIVERED: 'Đã giao', CANCELLED: 'Đã hủy'
}

export default function OrderDetailPage() {
  const { orderCode } = useParams()
  const navigate = useNavigate()
  const { order, loading } = useOrderDetail(orderCode!)

  if (loading) return (
    <div style={{ textAlign: 'center', padding: '120px 0', color: 'var(--muted)', letterSpacing: '0.15em', fontSize: '0.8rem', textTransform: 'uppercase' }}>Đang tải...</div>
  )
  if (!order) return (
    <div style={{ textAlign: 'center', padding: '120px 0', color: 'var(--muted)' }}>Không tìm thấy đơn hàng</div>
  )

  const stepIndex = STATUS_STEPS.indexOf(order.status)

  return (
    <div className="page-enter max-w-3xl mx-auto px-8 py-16">
      <button onClick={() => navigate('/orders')}
        style={{ fontSize: '0.72rem', letterSpacing: '0.15em', textTransform: 'uppercase', color: 'var(--muted)', background: 'none', border: 'none', cursor: 'pointer', marginBottom: '48px' }}>
        ← Đơn hàng của tôi
      </button>

      <div style={{ textAlign: 'center', marginBottom: '48px' }}>
        <p style={{ fontSize: '0.65rem', letterSpacing: '0.3em', textTransform: 'uppercase', color: 'var(--gold)', marginBottom: '8px' }}>Đơn hàng</p>
        <h1 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '2rem', fontWeight: 300 }}>#{order.orderCode}</h1>
      </div>

      {/* Progress */}
      {order.status !== 'CANCELLED' && (
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '48px' }}>
          {STATUS_STEPS.map((step, i) => (
            <div key={step} style={{ display: 'flex', alignItems: 'center', flex: i < STATUS_STEPS.length - 1 ? 1 : 'none' }}>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '6px' }}>
                <div style={{
                  width: '28px', height: '28px', borderRadius: '50%',
                  background: i <= stepIndex ? 'var(--gold)' : 'transparent',
                  border: `1px solid ${i <= stepIndex ? 'var(--gold)' : 'var(--border)'}`,
                  display: 'flex', alignItems: 'center', justifyContent: 'center'
                }}>
                  {i < stepIndex && <span style={{ color: 'white', fontSize: '0.7rem' }}>✓</span>}
                </div>
                <span style={{ fontSize: '0.6rem', letterSpacing: '0.1em', textTransform: 'uppercase', color: i <= stepIndex ? 'var(--gold-dark)' : 'var(--muted)', whiteSpace: 'nowrap' }}>
                  {STATUS_LABEL[step]}
                </span>
              </div>
              {i < STATUS_STEPS.length - 1 && (
                <div style={{ flex: 1, height: '1px', background: i < stepIndex ? 'var(--gold)' : 'var(--border)', margin: '0 8px', marginBottom: '20px' }} />
              )}
            </div>
          ))}
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '32px', marginBottom: '40px' }}>
        <div style={{ background: 'var(--cream)', padding: '24px', border: '1px solid var(--border)' }}>
          <p style={{ fontSize: '0.65rem', letterSpacing: '0.2em', textTransform: 'uppercase', color: 'var(--gold)', marginBottom: '12px' }}>Thông tin giao hàng</p>
          <p style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.1rem', marginBottom: '4px' }}>{order.recipientName}</p>
          <p style={{ fontSize: '0.85rem', color: 'var(--muted)', marginBottom: '4px' }}>{order.recipientPhone}</p>
          <p style={{ fontSize: '0.85rem', color: 'var(--muted)' }}>{order.shippingAddress}</p>
        </div>
        <div style={{ background: 'var(--cream)', padding: '24px', border: '1px solid var(--border)' }}>
          <p style={{ fontSize: '0.65rem', letterSpacing: '0.2em', textTransform: 'uppercase', color: 'var(--gold)', marginBottom: '12px' }}>Thanh toán</p>
          <p style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.1rem', marginBottom: '4px' }}>{order.paymentStatus === 'PAID' ? 'Đã thanh toán' : 'Chưa thanh toán'}</p>
          <p style={{ fontSize: '0.8rem', color: 'var(--muted)' }}>{new Date(order.createdAt).toLocaleDateString('vi-VN', { year: 'numeric', month: 'long', day: 'numeric' })}</p>
        </div>
      </div>

      {/* Items */}
      <div style={{ border: '1px solid var(--border)' }}>
        {order.items?.map((item, i) => (
          <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '20px 24px', borderBottom: i < order.items.length - 1 ? '1px solid var(--border)' : 'none' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{ width: '52px', height: '52px', background: 'var(--cream)', display: 'flex', alignItems: 'center', justifyContent: 'center', border: '1px solid var(--border)' }}>
                <span style={{ opacity: 0.2, fontSize: '0.8rem' }}>◇</span>
              </div>
              <div>
                <p style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.05rem' }}>{item.product.name}</p>
                <p style={{ fontSize: '0.78rem', color: 'var(--muted)' }}>x{item.quantity}</p>
              </div>
            </div>
            <p style={{ color: 'var(--gold-dark)', fontFamily: 'Cormorant Garamond, serif', fontSize: '1rem' }}>
              {(item.unitPrice * item.quantity).toLocaleString('vi-VN')}₫
            </p>
          </div>
        ))}
        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '20px 24px', background: 'var(--cream)', borderTop: '1px solid var(--border)' }}>
          <span style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.1rem' }}>Tổng cộng</span>
          <span style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.3rem', color: 'var(--gold-dark)' }}>
            {order.totalAmount.toLocaleString('vi-VN')}₫
          </span>
        </div>
      </div>
    </div>
  )
}