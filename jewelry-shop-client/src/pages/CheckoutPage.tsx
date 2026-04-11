import { useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { cartApi } from '../api/cart'

export default function CheckoutPage() {
  const { user } = useAuth()
  const navigate = useNavigate()

  const [params] = useSearchParams()
  const selectedIds = params.get('ids')?.split(',').map(Number) ?? []

  const [form, setForm] = useState({
    recipientName: '',
    recipientPhone: '',
    shippingAddress: '',
    note: ''
  })

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  if (!user) { navigate('/login'); return null }

  const f = (k: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) =>
    setForm({ ...form, [k]: e.target.value })

  const submit = async () => {
    if (selectedIds.length === 0) {
      setError("Không có sản phẩm được chọn")
      return
    }

    setLoading(true)
    setError(null)

    try {
      const order = await cartApi.checkout({
        userId: user.userId,
        selectedItemIds: selectedIds,
        ...form
      })
      navigate(`/orders/${order.orderCode}`)
    } catch (e: any) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page-enter" style={{ maxWidth: '560px', margin: '0 auto', padding: '64px 24px' }}>
      <div style={{ textAlign: 'center', marginBottom: '48px' }}>
        <p style={{ fontSize: '0.65rem', letterSpacing: '0.3em', textTransform: 'uppercase', color: 'var(--gold)', marginBottom: '12px' }}>Bước cuối</p>
        <h1 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '2.8rem', fontWeight: 300 }}>Thông tin giao hàng</h1>
        <div style={{ width: '40px', height: '1px', background: 'var(--gold)', margin: '20px auto 0' }} />
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
        <input className="input-luxury" placeholder="Người nhận" value={form.recipientName} onChange={f('recipientName')} />
        <input className="input-luxury" placeholder="Số điện thoại" value={form.recipientPhone} onChange={f('recipientPhone')} />
        <input className="input-luxury" placeholder="Địa chỉ giao hàng" value={form.shippingAddress} onChange={f('shippingAddress')} />
        <textarea className="input-luxury" placeholder="Ghi chú (tuỳ chọn)"
          value={form.note} onChange={f('note')}
          style={{ resize: 'none', height: '80px', fontFamily: 'Jost, sans-serif' }} />
      </div>

      {error && <p style={{ color: '#c0392b', fontSize: '0.8rem', marginTop: '16px', textAlign: 'center' }}>{error}</p>}

      <button className="btn-primary" style={{ width: '100%', marginTop: '40px' }} onClick={submit} disabled={loading}>
        {loading ? '...' : 'Xác nhận đặt hàng'}
      </button>
    </div>
  )
}