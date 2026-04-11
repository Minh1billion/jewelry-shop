import { useState, useEffect, useRef } from 'react'
import { useAuth } from '../hooks/useAuth'
import { useNavigate } from 'react-router-dom'

interface Message {
    id: number
    senderType: 'USER' | 'BOT'
    content: string
    status: string
}

export default function ChatBox() {
    const { user } = useAuth()
    const navigate = useNavigate()
    const [open, setOpen] = useState(false)
    const [messages, setMessages] = useState<Message[]>([])
    const [input, setInput] = useState('')
    const [loading, setLoading] = useState(false)
    const bottomRef = useRef<HTMLDivElement>(null)

    // Load lịch sử chat khi mở
    useEffect(() => {
        if (!open || !user) return
        fetch(`/api/chat/history?userId=${user.userId}`)
            .then(r => r.json())
            .then(setMessages)
            .catch(() => {})
    }, [open, user])

    // Auto scroll xuống tin nhắn mới nhất
    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
    }, [messages])

    const send = async () => {
        if (!user) return navigate('/login')
        if (!input.trim() || loading) return

        const userMsg: Message = {
            id: Date.now(),
            senderType: 'USER',
            content: input.trim(),
            status: 'DELIVERED'
        }
        setMessages(prev => [...prev, userMsg])
        setInput('')
        setLoading(true)

        try {
            const res = await fetch('/api/chat/send', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ userId: String(user.userId), content: userMsg.content })
            })

            if (!res.ok) {
                console.error('HTTP error:', res.status, await res.text())
                throw new Error('HTTP error')
            }

            const botMsg: Message = await res.json()
            setMessages(prev => [...prev, botMsg])

        } catch (e) {
            console.error('Fetch error:', e)
            setMessages(prev => [...prev, {
                id: Date.now(),
                senderType: 'BOT',
                content: 'Có lỗi xảy ra, vui lòng thử lại.',
                status: 'DELIVERED'
            }])
        } finally {
            setLoading(false)
        }
    }

    return (
        <>
            {/* Nút mở chatbox */}
            <button
                onClick={() => setOpen(o => !o)}
                style={{
                    position: 'fixed', bottom: '32px', right: '32px', zIndex: 1000,
                    width: '52px', height: '52px', borderRadius: '50%',
                    background: 'var(--charcoal)', border: '1px solid var(--gold)',
                    color: 'var(--gold)', fontSize: '1.3rem', cursor: 'pointer',
                    boxShadow: '0 4px 20px rgba(0,0,0,0.25)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    transition: 'all 0.2s'
                }}
                title="Hỗ trợ tư vấn"
            >
                {open ? '✕' : '💬'}
            </button>

            {/* Cửa sổ chat */}
            {open && (
                <div style={{
                    position: 'fixed', bottom: '96px', right: '32px', zIndex: 999,
                    width: '340px', height: '480px',
                    background: 'var(--ivory)', border: '1px solid var(--border)',
                    borderRadius: '2px', boxShadow: '0 8px 40px rgba(0,0,0,0.15)',
                    display: 'flex', flexDirection: 'column',
                    fontFamily: 'Jost, sans-serif'
                }}>

                    {/* Header */}
                    <div style={{
                        padding: '16px 20px', borderBottom: '1px solid var(--border)',
                        background: 'var(--charcoal)', color: 'var(--ivory)'
                    }}>
                        <p style={{ fontSize: '0.65rem', letterSpacing: '0.3em', textTransform: 'uppercase', color: 'rgba(212,175,100,0.85)', marginBottom: '2px' }}>
                            Lumière
                        </p>
                        <p style={{ fontSize: '0.9rem', letterSpacing: '0.08em' }}>Tư vấn trang sức</p>
                    </div>

                    {/* Danh sách tin nhắn */}
                    <div style={{ flex: 1, overflowY: 'auto', padding: '16px', display: 'flex', flexDirection: 'column', gap: '10px' }}>
                        {messages.length === 0 && !loading && (
                            <p style={{ textAlign: 'center', color: 'var(--muted)', fontSize: '0.78rem', letterSpacing: '0.08em', marginTop: '40px' }}>
                                Xin chào! Tôi có thể giúp gì cho bạn?
                            </p>
                        )}
                        {messages.map(m => (
                            <div key={m.id} style={{ display: 'flex', justifyContent: m.senderType === 'USER' ? 'flex-end' : 'flex-start' }}>
                                <div style={{
                                    maxWidth: '75%', padding: '10px 14px',
                                    background: m.senderType === 'USER' ? 'var(--charcoal)' : 'white',
                                    color: m.senderType === 'USER' ? 'var(--ivory)' : 'var(--charcoal)',
                                    border: '1px solid var(--border)',
                                    fontSize: '0.83rem', lineHeight: 1.5, letterSpacing: '0.03em'
                                }}>
                                    {m.content}
                                </div>
                            </div>
                        ))}
                        {loading && (
                            <div style={{ display: 'flex', justifyContent: 'flex-start' }}>
                                <div style={{
                                    padding: '10px 14px', background: 'white',
                                    border: '1px solid var(--border)',
                                    color: 'var(--muted)', fontSize: '0.78rem', letterSpacing: '0.08em'
                                }}>
                                    Đang soạn...
                                </div>
                            </div>
                        )}
                        <div ref={bottomRef} />
                    </div>

                    {/* Input */}
                    <div style={{ padding: '12px 16px', borderTop: '1px solid var(--border)', display: 'flex', gap: '8px' }}>
                        <input
                            value={input}
                            onChange={e => setInput(e.target.value)}
                            onKeyDown={e => e.key === 'Enter' && send()}
                            placeholder="Nhập câu hỏi..."
                            style={{
                                flex: 1, border: 'none', borderBottom: '1px solid var(--border)',
                                background: 'transparent', padding: '8px 0',
                                fontFamily: 'Jost, sans-serif', fontSize: '0.83rem',
                                letterSpacing: '0.05em', outline: 'none', color: 'var(--charcoal)'
                            }}
                        />
                        <button
                            onClick={send}
                            disabled={loading || !input.trim()}
                            style={{
                                background: 'transparent', border: 'none',
                                color: loading || !input.trim() ? 'var(--border)' : 'var(--charcoal)',
                                cursor: loading || !input.trim() ? 'default' : 'pointer',
                                fontSize: '1rem', padding: '4px 8px',
                                transition: 'color 0.2s'
                            }}
                        >
                            ➤
                        </button>
                    </div>
                </div>
            )}
        </>
    )
}