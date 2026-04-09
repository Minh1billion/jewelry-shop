import { useParams, useNavigate } from 'react-router-dom'
import { useProduct } from '../hooks/useProduct'
import { useCart } from '../hooks/useCart'
import { useAuth } from '../hooks/useAuth'

export default function ProductDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const { data: product, loading } = useProduct(Number(id))
  const { add } = useCart(user?.id ?? null)

  if (loading) return (
    <div style={{ textAlign: 'center', padding: '120px 0', color: 'var(--muted)', letterSpacing: '0.15em', fontSize: '0.8rem', textTransform: 'uppercase' }}>
      Đang tải...
    </div>
  )
  if (!product) return (
    <div style={{ textAlign: 'center', padding: '120px 0', color: 'var(--muted)' }}>Không tìm thấy sản phẩm</div>
  )

  const handleAdd = async () => {
    if (!user) return navigate('/login')
    await add(product.id, 1)
    navigate('/cart')
  }

  return (
    <div className="page-enter max-w-6xl mx-auto px-8 py-16">
      <button onClick={() => navigate(-1)}
        style={{ fontSize: '0.72rem', letterSpacing: '0.15em', textTransform: 'uppercase', color: 'var(--muted)', background: 'none', border: 'none', cursor: 'pointer', marginBottom: '48px', display: 'flex', alignItems: 'center', gap: '8px' }}>
        ← Quay lại
      </button>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '80px', alignItems: 'start' }}>
        {/* Image */}
        <div style={{ height: '280px', background: 'var(--cream)', overflow: 'hidden' }}>
          {product.imageUrl ? (
            <img src={product.imageUrl} alt={product.name} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
          ) : (
            <div style={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <span style={{ fontSize: '2rem', opacity: 0.15 }}>◇</span>
            </div>
          )}
        </div>

        {/* Info */}
        <div>
          {product.category && (
            <span className="tag" style={{ marginBottom: '20px', display: 'inline-block' }}>{product.category.name}</span>
          )}
          <h1 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '2.8rem', fontWeight: 300, lineHeight: 1.15, marginBottom: '16px', color: 'var(--charcoal)' }}>
            {product.name}
          </h1>
          <p style={{ fontSize: '1.6rem', color: 'var(--gold-dark)', letterSpacing: '0.05em', marginBottom: '32px', fontFamily: 'Cormorant Garamond, serif' }}>
            {product.price.toLocaleString('vi-VN')}₫
          </p>

          <div style={{ width: '100%', height: '1px', background: 'var(--border)', marginBottom: '32px' }} />

          <p style={{ color: 'var(--muted)', fontSize: '0.9rem', lineHeight: 1.8, letterSpacing: '0.04em', marginBottom: '40px' }}>
            {product.description}
          </p>

          <p style={{ fontSize: '0.75rem', letterSpacing: '0.12em', textTransform: 'uppercase', color: 'var(--muted)', marginBottom: '32px' }}>
            Còn lại: <span style={{ color: 'var(--charcoal)' }}>{product.stock} sản phẩm</span>
          </p>

          <button className="btn-primary" style={{ width: '100%' }}
            onClick={handleAdd} disabled={product.stock === 0}>
            {product.stock === 0 ? 'Hết hàng' : 'Thêm vào giỏ hàng'}
          </button>
        </div>
      </div>
    </div>
  )
}