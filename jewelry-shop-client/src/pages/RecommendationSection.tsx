import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { useCart } from '../hooks/useCart'

export default function RecommendationSection() {
    const { user } = useAuth()
    const navigate = useNavigate()
    const { add } = useCart(user?.userId ?? null)
    const [products, setProducts] = useState<any[]>([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const url = user?.userId
            ? `/api/recommendations/${user.userId}`
            : `/api/recommendations`

        fetch(url)
            .then(r => r.json())
            .then(data => setProducts(Array.isArray(data) ? data : []))
            .catch(() => setProducts([]))
            .finally(() => setLoading(false))
    }, [user?.userId])

    const handleAdd = async (e: React.MouseEvent, productId: number) => {
        e.stopPropagation()
        if (!user) return navigate('/login')
        await add(productId, 1)
    }

    if (!loading && products.length === 0) return null

    return (
        <div style={{ borderTop: '1px solid var(--border)', padding: '64px 0 80px' }}>
            <div className="max-w-6xl mx-auto px-8">
                <div style={{ marginBottom: '40px', textAlign: 'center' }}>
                    <p style={{
                        fontSize: '0.6rem', letterSpacing: '0.4em', textTransform: 'uppercase',
                        color: 'var(--gold-dark)', marginBottom: '10px'
                    }}>
                        {user ? 'Dành riêng cho bạn' : 'Được yêu thích nhất'}
                    </p>
                    <h2 style={{
                        fontFamily: 'Cormorant Garamond, serif', fontSize: '2rem',
                        fontWeight: 300, color: 'var(--charcoal)', letterSpacing: '0.05em'
                    }}>
                        {user ? 'Gợi ý cho bạn' : 'Sản phẩm nổi bật'}
                    </h2>
                </div>

                {loading ? (
                    <div style={{
                        textAlign: 'center', padding: '48px 0',
                        color: 'var(--muted)', fontSize: '0.78rem',
                        letterSpacing: '0.15em', textTransform: 'uppercase'
                    }}>
                        Đang tải gợi ý...
                    </div>
                ) : (
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))',
                        gap: '24px'
                    }}>
                        {products.map((p: any) => (
                            <div key={p.productId} className="product-card" onClick={() => navigate(`/products/${p.productId}`)}>
                                <div style={{ height: '260px', background: 'var(--cream)', overflow: 'hidden', position: 'relative' }}>
                                    {p.imageUrl ? (
                                        <img src={p.imageUrl} alt={p.name}
                                             style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                                    ) : (
                                        <div style={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                            <span style={{ fontSize: '2rem', opacity: 0.15 }}>◇</span>
                                        </div>
                                    )}
                                    {user && (
                                        <div style={{
                                            position: 'absolute', top: '12px', left: '12px',
                                            background: 'rgba(30,26,20,0.75)',
                                            color: 'rgba(212,175,100,0.9)',
                                            fontSize: '0.55rem', letterSpacing: '0.2em',
                                            textTransform: 'uppercase', padding: '4px 8px'
                                        }}>
                                            Gợi ý
                                        </div>
                                    )}
                                </div>
                                <div style={{ padding: '18px 20px 22px' }}>
                                    {p.category && (
                                        <span className="tag" style={{ marginBottom: '8px', display: 'inline-block' }}>
                      {p.category.name}
                    </span>
                                    )}
                                    <h3 style={{
                                        fontFamily: 'Cormorant Garamond, serif',
                                        fontSize: '1.15rem', fontWeight: 400,
                                        margin: '6px 0 5px', color: 'var(--charcoal)'
                                    }}>
                                        {p.name}
                                    </h3>
                                    <p style={{
                                        fontSize: '0.9rem', color: 'var(--gold-dark)',
                                        letterSpacing: '0.05em', marginBottom: '14px'
                                    }}>
                                        {p.price?.toLocaleString('vi-VN')}₫
                                    </p>
                                    <button className="btn-ghost" style={{ width: '100%', padding: '9px' }}
                                            onClick={e => handleAdd(e, p.productId)}>
                                        Thêm vào giỏ
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    )
}