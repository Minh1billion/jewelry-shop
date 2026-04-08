import { useState } from 'react'
import { useAuth } from '../hooks/useAuth'
import { useNavigate, Link } from 'react-router-dom'

type FormType = {
  username: string
  email: string
  password: string
  fullName: string
  phone: string
}

type Field = {
  key: keyof FormType
  label: string
  type?: string
}

const FIELDS: readonly Field[] = [
  { key: 'username', label: 'Tên đăng nhập' },
  { key: 'email', label: 'Email' },
  { key: 'password', label: 'Mật khẩu', type: 'password' },
  { key: 'fullName', label: 'Họ và tên' },
  { key: 'phone', label: 'Số điện thoại' },
]

export default function RegisterPage() {
  const { register, loading, error } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState<FormType>({
    username: '',
    email: '',
    password: '',
    fullName: '',
    phone: ''
  })

  const submit = async () => {
    const u = await register(form)
    if (u) navigate('/login')
  }

  return (
    <div className="page-enter" style={{ minHeight: '80vh', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '60px 24px' }}>
      <div style={{ width: '100%', maxWidth: '420px' }}>
        <div className="text-center" style={{ marginBottom: '48px' }}>
          <p style={{ fontSize: '0.65rem', letterSpacing: '0.3em', textTransform: 'uppercase', color: 'var(--gold)', marginBottom: '12px' }}>Tham gia cùng chúng tôi</p>
          <h1 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '2.8rem', fontWeight: 300, color: 'var(--charcoal)', lineHeight: 1.1 }}>Tạo tài khoản</h1>
          <div style={{ width: '40px', height: '1px', background: 'var(--gold)', margin: '20px auto 0' }} />
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
          {FIELDS.map(f => (
            <input key={f.key} className="input-luxury"
              placeholder={f.label}
              type={f.type ?? 'text'}
              value={form[f.key]}
              onChange={e => setForm({ ...form, [f.key]: e.target.value })} />
          ))}
        </div>

        {error && <p style={{ color: '#c0392b', fontSize: '0.8rem', marginTop: '16px', textAlign: 'center' }}>{error}</p>}

        <button className="btn-primary" style={{ width: '100%', marginTop: '40px' }} onClick={submit} disabled={loading}>
          {loading ? '...' : 'Tạo tài khoản'}
        </button>

        <p style={{ textAlign: 'center', marginTop: '24px', fontSize: '0.8rem', color: 'var(--muted)', letterSpacing: '0.05em' }}>
          Đã có tài khoản?{' '}
          <Link to="/login" style={{ color: 'var(--gold-dark)', textDecoration: 'none', borderBottom: '1px solid var(--gold-light)' }}>
            Đăng nhập
          </Link>
        </p>
      </div>
    </div>
  )
}