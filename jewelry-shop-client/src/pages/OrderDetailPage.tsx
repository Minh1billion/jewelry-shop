import { useParams, useNavigate } from 'react-router-dom'
import { useOrderDetail } from '../hooks/useOrder'

export default function OrderDetailPage() {
  const { orderCode } = useParams()
  const navigate = useNavigate()
  const { order, loading } = useOrderDetail(orderCode!)

  if (loading) return <p className="p-6">Đang tải...</p>
  if (!order) return <p className="p-6">Không tìm thấy đơn hàng</p>

  return (
    <div className="max-w-xl mx-auto p-6 space-y-4">
      <button className="text-sm underline" onClick={() => navigate('/orders')}>← Quay lại</button>
      <h1 className="text-xl font-bold">Đơn hàng #{order.orderCode}</h1>
      <div className="text-sm space-y-1">
        <p>Trạng thái: <span className="font-medium">{order.status}</span></p>
        <p>Thanh toán: {order.paymentStatus}</p>
        <p>Người nhận: {order.recipientName} — {order.recipientPhone}</p>
        <p>Địa chỉ: {order.shippingAddress}</p>
        {order.note && <p>Ghi chú: {order.note}</p>}
      </div>
      <div className="space-y-2">
        {order.items?.map(item => (
          <div key={item.id} className="flex justify-between border-b pb-2 text-sm">
            <span>{item.product.name} x{item.quantity}</span>
            <span>{(item.unitPrice * item.quantity).toLocaleString()}đ</span>
          </div>
        ))}
      </div>
      <div className="flex justify-between font-bold">
        <span>Tổng cộng</span>
        <span>{order.totalAmount.toLocaleString()}đ</span>
      </div>
    </div>
  )
}