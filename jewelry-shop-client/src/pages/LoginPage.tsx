import { useState } from 'react'
import { useAuth } from '../hooks/useAuth'
import { useNavigate, Link } from 'react-router-dom'

export default function LoginPage() {
  const { login, loading, error } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '' })

  const submit = async () => {
    const u = await login(form.username, form.password)
    if (u) navigate('/')
  }

  return (
    <div className="page-enter" style={{ minHeight: '80vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div style={{ width: '100%', maxWidth: '420px', padding: '0 24px' }}>
        <div className="text-center" style={{ marginBottom: '48px' }}>
          <p style={{ fontSize: '0.65rem', letterSpacing: '0.3em', textTransform: 'uppercase', color: 'var(--gold)', marginBottom: '12px' }}>Chào mừng trở lại</p>
          <h1 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '2.8rem', fontWeight: 300, color: 'var(--charcoal)', lineHeight: 1.1 }}>Đăng nhập</h1>
          <div style={{ width: '40px', height: '1px', background: 'var(--gold)', margin: '20px auto 0' }} />
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
          <input className="input-luxury" placeholder="Tên đăng nhập"
            value={form.username} onChange={e => setForm({ ...form, username: e.target.value })} />
          <input className="input-luxury" type="password" placeholder="Mật khẩu"
            value={form.password} onChange={e => setForm({ ...form, password: e.target.value })}
            onKeyDown={e => e.key === 'Enter' && submit()} />
        </div>

        {error && <p style={{ color: '#c0392b', fontSize: '0.8rem', letterSpacing: '0.05em', marginTop: '16px', textAlign: 'center' }}>{error}</p>}

        <button className="btn-primary" style={{ width: '100%', marginTop: '40px' }} onClick={submit} disabled={loading}>
          {loading ? '...' : 'Đăng nhập'}
        </button>

        <p style={{ textAlign: 'center', marginTop: '24px', fontSize: '0.8rem', letterSpacing: '0.05em', color: 'var(--muted)' }}>
          Chưa có tài khoản?{' '}
          <Link to="/register" style={{ color: 'var(--gold-dark)', textDecoration: 'none', borderBottom: '1px solid var(--gold-light)' }}>
            Đăng ký ngay
          </Link>
        </p>
      </div>
    </div>
  )
}