import { BrowserRouter, Routes, Route, Link, useNavigate } from 'react-router-dom'
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

function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  return (
    <header style={{ borderBottom: '1px solid var(--border)', background: 'var(--ivory)' }} className="sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-8 py-5 flex items-center justify-between">
        <Link to="/" style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.6rem', fontWeight: 300, letterSpacing: '0.25em', color: 'var(--charcoal)', textDecoration: 'none', textTransform: 'uppercase' }}>
          Lumière
        </Link>
        <nav className="flex items-center gap-8">
          <Link to="/" className="nav-link">Bộ sưu tập</Link>
          {user ? (
            <>
              <Link to="/cart" className="nav-link">Giỏ hàng</Link>
              <Link to="/orders" className="nav-link">Đơn hàng</Link>
              <button className="nav-link" style={{ background: 'none', border: 'none', cursor: 'pointer' }}
                onClick={() => { logout(); navigate('/login') }}>
                Đăng xuất
              </button>
            </>
          ) : (
            <Link to="/login" className="nav-link">Đăng nhập</Link>
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
          <Route path="/" element={<ProductsPage />} />
          <Route path="/products/:id" element={<ProductDetailPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/orders/:orderCode" element={<OrderDetailPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/report" element={<ReportPage />} />
        </Routes>
      </main>
      <footer style={{ borderTop: '1px solid var(--border)', padding: '40px 0', marginTop: '80px' }}>
        <div className="max-w-6xl mx-auto px-8 text-center">
          <p style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.1rem', letterSpacing: '0.3em', color: 'var(--muted)', textTransform: 'uppercase', fontWeight: 300 }}>
            Lumière Jewelry — Tinh tế từng chi tiết
          </p>
        </div>
      </footer>
    </BrowserRouter>
  )
}