import { useState } from 'react'
import { useProducts } from '../hooks/useProduct'
import { useCategories } from '../hooks/useCategory'
import { useCart } from '../hooks/useCart'
import { useAuth } from '../hooks/useAuth'
import { useNavigate } from 'react-router-dom'
import ChatBox from '../pages/ChatBox'
import RecommendationSection from '../pages/RecommendationSection'

const HERO_IMG = 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=1600&q=85'

export default function ProductsPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const { data: categories } = useCategories()
  const [params, setParams] = useState({ page: 0, size: 12 } as any)
  const { data, loading } = useProducts(params)
  const { add } = useCart(user?.userId ?? null)

  const handleAdd = async (e: React.MouseEvent, productId: number) => {
    e.stopPropagation()
    if (!user) return navigate('/login')
    await add(productId, 1)
  }

  return (
    <div className="page-enter">
      {/* Hero */}
      <div style={{ position: 'relative', height: '520px', overflow: 'hidden', borderBottom: '1px solid var(--border)' }}>
        <img
          src={HERO_IMG}
          alt="Lumière Jewelry"
          style={{ width: '100%', height: '100%', objectFit: 'cover', objectPosition: 'center 40%' }}
        />
        <div style={{
          position: 'absolute', inset: 0,
          background: 'rgba(30, 26, 20, 0.45)',
          display: 'flex', flexDirection: 'column',
          alignItems: 'center', justifyContent: 'center',
          textAlign: 'center', padding: '0 24px'
        }}>
          <p style={{ fontSize: '0.65rem', letterSpacing: '0.4em', textTransform: 'uppercase', color: 'rgba(212,175,100,0.9)', marginBottom: '16px' }}>
            Bộ sưu tập mới nhất
          </p>
          <h1 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '4rem', fontWeight: 300, color: '#f5f0e8', lineHeight: 1.1, marginBottom: '20px', letterSpacing: '0.05em' }}>
            Trang sức cao cấp
          </h1>
          <p style={{ color: 'rgba(245,240,232,0.75)', fontSize: '0.9rem', letterSpacing: '0.1em', maxWidth: '380px' }}>
            Được chế tác thủ công từ những nguyên liệu quý hiếm nhất thế giới
          </p>
        </div>
      </div>

      <div className="max-w-6xl mx-auto px-8 py-12">
        {/* Filters */}
        <div style={{ display: 'flex', gap: '16px', marginBottom: '48px', alignItems: 'center', flexWrap: 'wrap' }}>
          <input
            style={{ flex: 1, minWidth: '200px', background: 'transparent', border: 'none', borderBottom: '1px solid var(--border)', padding: '10px 0', fontFamily: 'Jost, sans-serif', fontSize: '0.85rem', letterSpacing: '0.08em', outline: 'none', color: 'var(--charcoal)' }}
            placeholder="Tìm kiếm trang sức..."
            onChange={e => setParams({ ...params, keyword: e.target.value, page: 0 })} />
          <select
            style={{ background: 'transparent', border: 'none', borderBottom: '1px solid var(--border)', padding: '10px 0', fontFamily: 'Jost, sans-serif', fontSize: '0.78rem', letterSpacing: '0.1em', outline: 'none', color: 'var(--charcoal)', cursor: 'pointer', textTransform: 'uppercase' }}
            onChange={e => setParams({ ...params, categoryId: e.target.value || undefined, page: 0 })}>
            <option value="">Tất cả danh mục</option>
            {categories.map(c => <option key={c.categoryId} value={c.categoryId}>{c.name}</option>)}
          </select>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '80px 0', color: 'var(--muted)', letterSpacing: '0.15em', fontSize: '0.8rem', textTransform: 'uppercase' }}>
            Đang tải...
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '24px' }}>
            {data?.content.map(p => (
              <div key={p.productId} className="product-card" onClick={() => navigate(`/products/${p.productId}`)}>
                <div style={{ height: '280px', background: 'var(--cream)', overflow: 'hidden' }}>
                  {p.imageUrl ? (
                    <img src={p.imageUrl} alt={p.name} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                  ) : (
                    <div style={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <span style={{ fontSize: '2rem', opacity: 0.15 }}>◇</span>
                    </div>
                  )}
                </div>
                <div style={{ padding: '20px 24px 24px' }}>
                  {p.category && <span className="tag" style={{ marginBottom: '10px', display: 'inline-block' }}>{p.category.name}</span>}
                  <h3 style={{ fontFamily: 'Cormorant Garamond, serif', fontSize: '1.25rem', fontWeight: 400, margin: '8px 0 6px', color: 'var(--charcoal)' }}>
                    {p.name}
                  </h3>
                  <p style={{ fontSize: '0.95rem', color: 'var(--gold-dark)', letterSpacing: '0.05em', marginBottom: '16px' }}>
                    {p.price.toLocaleString('vi-VN')}₫
                  </p>
                  <button className="btn-ghost" style={{ width: '100%', padding: '10px' }}
                    onClick={e => handleAdd(e, p.productId)}>
                    Thêm vào giỏ
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {(data?.totalPages ?? 0) > 1 && (
          <div style={{ display: 'flex', gap: '8px', justifyContent: 'center', marginTop: '48px' }}>
            {Array.from({ length: data!.totalPages }, (_, i) => (
              <button key={i}
                onClick={() => setParams({ ...params, page: i })}
                style={{
                  width: '36px', height: '36px', border: '1px solid',
                  borderColor: params.page === i ? 'var(--charcoal)' : 'var(--border)',
                  background: params.page === i ? 'var(--charcoal)' : 'transparent',
                  color: params.page === i ? 'var(--ivory)' : 'var(--charcoal)',
                  fontFamily: 'Jost, sans-serif', fontSize: '0.8rem',
                  cursor: 'pointer', transition: 'all 0.2s'
                }}>
                {i + 1}
              </button>
            ))}
          </div>
        )}
      </div>
        <RecommendationSection />
        <ChatBox />
    </div>
  )
}