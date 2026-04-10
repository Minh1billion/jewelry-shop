import { BrowserRouter, Routes, Route, Link, useNavigate, NavLink } from 'react-router-dom'
import { useAuth } from './hooks/useAuth'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ProductsPage from './pages/ProductsPage'
import ProductDetailPage from './pages/ProductDetailPage'
import CartPage from './pages/CartPage'
import CheckoutPage from './pages/CheckoutPage'
import OrdersPage from './pages/OrdersPage'
import OrderDetailPage from './pages/OrderDetailPage'
import ReportPage from './pages/ReportPage'
import AdminOrdersPage from './pages/AdminOrdersPage'
import AdminShippersPage from './pages/AdminShippersPage'

function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const navLinkStyle = ({ isActive }: { isActive: boolean }): React.CSSProperties => ({
    fontSize: 13,
    letterSpacing: '0.12em',
    textTransform: 'uppercase',
    textDecoration: 'none',
    color: isActive ? 'var(--color-text-primary)' : 'var(--color-text-secondary)',
    fontWeight: isActive ? 500 : 400,
    paddingBottom: 2,
    borderBottom: isActive ? '1px solid var(--color-text-primary)' : '1px solid transparent',
    transition: 'color 0.2s, border-color 0.2s',
  })

  return (
    <header style={{ borderBottom: '0.5px solid var(--color-border-tertiary)', background: 'var(--color-background-primary)' }} className="sticky top-0 z-50">
      <div style={{ maxWidth: 1100, margin: '0 auto', padding: '18px 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Link to="/" style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.5rem', fontWeight: 300, letterSpacing: '0.3em', color: 'var(--color-text-primary)', textDecoration: 'none', textTransform: 'uppercase' }}>
          Lumière
        </Link>

        <nav style={{ display: 'flex', alignItems: 'center', gap: 28 }}>
          {/* Always visible */}
          <NavLink to="/" end style={navLinkStyle}>Bộ sưu tập</NavLink>

          {!user && (
            <>
              <NavLink to="/login" style={navLinkStyle}>Đăng nhập</NavLink>
              <NavLink to="/register" style={navLinkStyle}>Đăng ký</NavLink>
            </>
          )}

          {user?.role === 'CUSTOMER' && (
            <>
              <NavLink to="/cart" style={navLinkStyle}>Giỏ hàng</NavLink>
              <NavLink to="/orders" style={navLinkStyle}>Đơn hàng</NavLink>
            </>
          )}

          {user?.role === 'ADMIN' && (
            <>
              <NavLink to="/admin/orders" style={navLinkStyle}>Đơn hàng</NavLink>
              <NavLink to="/admin/shippers" style={navLinkStyle}>Shipper</NavLink>
              <NavLink to="/report" style={navLinkStyle}>Báo cáo</NavLink>
            </>
          )}

          {user?.role === 'SHIPPER' && (
            <NavLink to="/shipper/orders" style={navLinkStyle}>Đơn của tôi</NavLink>
          )}

          {user && (
            <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
              <span style={{ fontSize: 12, color: 'var(--color-text-secondary)', letterSpacing: '0.05em' }}>
                {user.fullName}
              </span>
              <button
                onClick={() => { logout(); navigate('/login') }}
                style={{
                  fontSize: 12, letterSpacing: '0.12em', textTransform: 'uppercase',
                  background: 'none', border: 'none', cursor: 'pointer',
                  color: 'var(--color-text-secondary)', padding: 0,
                }}
              >
                Đăng xuất
              </button>
            </div>
          )}
        </nav>
      </div>
    </header>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <main>
        <Routes>
          {/* Public */}
          <Route path="/" element={<ProductsPage />} />
          <Route path="/products/:id" element={<ProductDetailPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Customer */}
          <Route path="/cart" element={<CartPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/orders/:orderCode" element={<OrderDetailPage />} />

          {/* Admin */}
          <Route path="/report" element={<ReportPage />} />
          <Route path="/admin/orders" element={<AdminOrdersPage />} />
          <Route path="/admin/shippers" element={<AdminShippersPage />} />
        </Routes>
      </main>
      <footer style={{ borderTop: '0.5px solid var(--color-border-tertiary)', padding: '40px 0', marginTop: '80px' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto', padding: '0 24px', textAlign: 'center' }}>
          <p style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1rem', letterSpacing: '0.3em', color: 'var(--color-text-secondary)', textTransform: 'uppercase', fontWeight: 300, margin: 0 }}>
            Lumière Jewelry — Tinh tế từng chi tiết
          </p>
        </div>
      </footer>
    </BrowserRouter>
  )
}