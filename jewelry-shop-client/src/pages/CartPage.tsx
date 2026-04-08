import { useState } from 'react'
import { useAuth } from '../hooks/useAuth'
import { useCart } from '../hooks/useCart'
import { useNavigate } from 'react-router-dom'

export default function CartPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const { items, loading, update, remove } = useCart(user?.id ?? null)
  const [selectedIds, setSelectedIds] = useState<number[]>([])

  if (!user) { navigate('/login'); return null }

  const toggle = (id: number) =>
    setSelectedIds(prev => prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id])

  const toggleAll = () =>
    setSelectedIds(selectedIds.length === items.length ? [] : items.map(i => i.id))

  const total = items
    .filter(i => selectedIds.includes(i.id))
    .reduce((s, i) => s + i.unitPrice * i.quantity, 0)

  return (
    <div className="page-enter max-w-4xl mx-auto px-8 py-16">
      <div style={{ marginBottom: '48px', textAlign: 'center' }}>
        <p style={{ fontSize: '0.65rem', letterSpacing: '0.3em', textTransform: 'uppercase', color: 'var(--gold)', marginBottom: '12px' }}>Của bạn</p>
        <h1 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '2.8rem', fontWeight: 300 }}>Giỏ hàng</h1>
        <div style={{ width: '40px', height: '1px', background: 'var(--gold)', margin: '20px auto 0' }} />
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px', color: 'var(--muted)', letterSpacing: '0.15em', fontSize: '0.8rem', textTransform: 'uppercase' }}>Đang tải...</div>
      ) : items.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '80px 0' }}>
          <p style={{ color: 'var(--muted)', marginBottom: '24px', letterSpacing: '0.08em' }}>Giỏ hàng của bạn đang trống</p>
          <button className="btn-ghost" onClick={() => navigate('/')}>Khám phá bộ sưu tập</button>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 320px', gap: '48px', alignItems: 'start' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '24px', paddingBottom: '16px', borderBottom: '1px solid var(--border)' }}>
              <input type="checkbox" checked={selectedIds.length === items.length && items.length > 0}
                onChange={toggleAll} style={{ accentColor: 'var(--gold)', width: '16px', height: '16px' }} />
              <span style={{ fontSize: '0.72rem', letterSpacing: '0.15em', textTransform: 'uppercase', color: 'var(--muted)' }}>
                Chọn tất cả ({items.length})
              </span>
            </div>

            {items.map(item => (
              <div key={item.id} style={{ display: 'flex', gap: '20px', alignItems: 'center', padding: '20px 0', borderBottom: '1px solid var(--border)' }}>
                <input type="checkbox" checked={selectedIds.includes(item.id)} onChange={() => toggle(item.id)}
                  style={{ accentColor: 'var(--gold)', width: '16px', height: '16px', flexShrink: 0 }} />
                <div style={{ width: '80px', height: '80px', background: 'var(--cream)', border: '1px solid var(--border)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  <span style={{ opacity: 0.2 }}>◇</span>
                </div>
                <div style={{ flex: 1 }}>
                  <h3 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.15rem', fontWeight: 400, marginBottom: '4px' }}>{item.product.name}</h3>
                  <p style={{ fontSize: '0.85rem', color: 'var(--gold-dark)' }}>{item.unitPrice.toLocaleString('vi-VN')}₫</p>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                  <button onClick={() => update(item.id, Math.max(1, item.quantity - 1))}
                    style={{ width: '28px', height: '28px', border: '1px solid var(--border)', background: 'transparent', cursor: 'pointer', fontSize: '1rem', color: 'var(--charcoal)' }}>−</button>
                  <span style={{ fontSize: '0.9rem', minWidth: '20px', textAlign: 'center' }}>{item.quantity}</span>
                  <button onClick={() => update(item.id, item.quantity + 1)}
                    style={{ width: '28px', height: '28px', border: '1px solid var(--border)', background: 'transparent', cursor: 'pointer', fontSize: '1rem', color: 'var(--charcoal)' }}>+</button>
                </div>
                <button onClick={() => remove(item.id)}
                  style={{ fontSize: '0.7rem', letterSpacing: '0.1em', textTransform: 'uppercase', color: 'var(--muted)', background: 'none', border: 'none', cursor: 'pointer', textDecoration: 'underline' }}>
                  Xóa
                </button>
              </div>
            ))}
          </div>

          {/* Summary */}
          <div style={{ background: 'var(--cream)', border: '1px solid var(--border)', padding: '32px', position: 'sticky', top: '100px' }}>
            <h2 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.4rem', fontWeight: 400, marginBottom: '24px' }}>Tóm tắt đơn hàng</h2>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '16px', fontSize: '0.85rem', color: 'var(--muted)' }}>
              <span>Đã chọn</span>
              <span>{selectedIds.length} sản phẩm</span>
            </div>
            <div style={{ height: '1px', background: 'var(--border)', margin: '20px 0' }} />
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '32px' }}>
              <span style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.1rem' }}>Tổng cộng</span>
              <span style={{ color: 'var(--gold-dark)', fontFamily: 'Cormorant Garamond, serif', fontSize: '1.2rem' }}>{total.toLocaleString('vi-VN')}₫</span>
            </div>
            <button className="btn-primary" style={{ width: '100%' }}
              disabled={selectedIds.length === 0}
              onClick={() => navigate(`/checkout?ids=${selectedIds.join(',')}`)}>
              Thanh toán ({selectedIds.length})
            </button>
          </div>
        </div>
      )}
    </div>
  )
}