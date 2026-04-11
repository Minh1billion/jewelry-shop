import { useState } from 'react'
import { useShippers } from '../hooks/useShipper'
import { shipperApi } from '../api/shipper'

export default function AdminShippersPage() {
  const { shippers, loading, refresh } = useShippers()
  const [form, setForm] = useState({ fullName: '', phone: '', email: '' })
  const [error, setError] = useState('')

  const create = async () => {
    if (!form.fullName || !form.phone || !form.email) return setError('Vui lòng điền đầy đủ thông tin')
    try {
      await shipperApi.create(form)
      setForm({ fullName: '', phone: '', email: '' })
      setError('')
      refresh()
    } catch (e: any) {
      setError(e.message)
    }
  }

  const toggleStatus = async (id: number, current: 'ACTIVE' | 'INACTIVE', data: { fullName: string; phone: string; email: string }) => {
    const newStatus = current === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    try {
      await shipperApi.update(id, { ...data, shipperStatus: newStatus })
      refresh()
    } catch (e: any) {
      setError(e.message)
    }
  }

  const inputStyle: React.CSSProperties = {
    fontSize: '0.8rem', padding: '10px 14px',
    border: '1px solid var(--border)', background: 'white',
    width: '100%', outline: 'none', letterSpacing: '0.05em'
  }

  return (
    <div className="max-w-3xl mx-auto px-8 py-16">
      <div style={{ textAlign: 'center', marginBottom: '48px' }}>
        <p style={{ fontSize: '0.65rem', letterSpacing: '0.3em', textTransform: 'uppercase', color: 'var(--gold)', marginBottom: '12px' }}>Quản lý</p>
        <h1 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '2.8rem', fontWeight: 300 }}>Shipper</h1>
        <div style={{ width: '40px', height: '1px', background: 'var(--gold)', margin: '20px auto 0' }} />
      </div>

      <div style={{ border: '1px solid var(--border)', padding: '28px', marginBottom: '32px', background: 'white' }}>
        <p style={{ fontSize: '0.7rem', letterSpacing: '0.2em', textTransform: 'uppercase', marginBottom: '20px' }}>Thêm shipper mới</p>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '12px', marginBottom: '16px' }}>
          <input placeholder="Họ tên" value={form.fullName} onChange={e => setForm(p => ({ ...p, fullName: e.target.value }))} style={inputStyle} />
          <input placeholder="Số điện thoại" value={form.phone} onChange={e => setForm(p => ({ ...p, phone: e.target.value }))} style={inputStyle} />
          <input placeholder="Email" value={form.email} onChange={e => setForm(p => ({ ...p, email: e.target.value }))} style={inputStyle} />
        </div>
        {error && <p style={{ color: '#B85A5A', fontSize: '0.75rem', marginBottom: '12px' }}>{error}</p>}
        <button onClick={create}
          style={{ fontSize: '0.7rem', letterSpacing: '0.1em', textTransform: 'uppercase', padding: '10px 28px', background: 'var(--charcoal)', color: 'white', border: 'none', cursor: 'pointer' }}>
          Thêm mới
        </button>
      </div>

      {loading ? (
        <p style={{ textAlign: 'center', color: 'var(--muted)', fontSize: '0.8rem' }}>Đang tải...</p>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {shippers.map(s => (
            <div key={s.shipperId} style={{ border: '1px solid var(--border)', padding: '20px 24px', background: 'white', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <p style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.1rem', marginBottom: '4px' }}>{s.fullName}</p>
                <p style={{ fontSize: '0.75rem', color: 'var(--muted)' }}>{s.phone} — {s.email}</p>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                <span style={{ fontSize: '0.68rem', letterSpacing: '0.1em', textTransform: 'uppercase', color: s.shipperStatus === 'ACTIVE' ? '#5AB87A' : '#B85A5A', background: s.shipperStatus === 'ACTIVE' ? '#5AB87A15' : '#B85A5A15', padding: '4px 10px', border: `1px solid ${s.shipperStatus === 'ACTIVE' ? '#5AB87A30' : '#B85A5A30'}` }}>
                  {s.shipperStatus === 'ACTIVE' ? 'Hoạt động' : 'Ngừng'}
                </span>
                <button onClick={() => toggleStatus(s.shipperId, s.shipperStatus, { fullName: s.fullName, phone: s.phone, email: s.email })}
                  style={{ fontSize: '0.68rem', letterSpacing: '0.1em', textTransform: 'uppercase', padding: '6px 14px', background: 'none', border: '1px solid var(--border)', cursor: 'pointer', color: 'var(--muted)' }}>
                  {s.shipperStatus === 'ACTIVE' ? 'Vô hiệu hóa' : 'Kích hoạt'}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}