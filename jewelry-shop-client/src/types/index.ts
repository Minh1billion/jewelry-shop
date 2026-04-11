export type ReportType = 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY'

export interface User {
  userId: number
  username: string
  role: string
  fullName: string
  phone: string
  email: string
}

// Category
export interface Category {
  categoryId: number
  name: string
  description: string
}

// Product
export interface Product {
  productId: number
  name: string
  description: string
  price: number
  imageUrl: string | null
  stock: number
  active: boolean
  category: Category
  createdAt: string
}

// CartItem
export interface CartItem {
  cartItemId: number
  product: Product
  quantity: number
  unitPrice: number
  selected: boolean
}

// OrderItem
export interface OrderItem {
  orderItemId: number
  product: Product
  quantity: number
  unitPrice: number
  discountPercent?: number
}

// Shipper
export interface Shipper {
  shipperId: number
  fullName: string
  phone: string
  email: string
  shipperStatus: 'ACTIVE' | 'INACTIVE'
}

// Order
export interface Order {
  orderId: number
  orderCode: string
  user: User
  status: 'PENDING' | 'CONFIRMED' | 'SHIPPING' | 'DELIVERED' | 'CANCELLED'
  paymentStatus: 'UNPAID' | 'PAID' | 'REFUNDED'
  recipientName: string
  recipientPhone: string
  shippingAddress: string
  note?: string
  totalAmount: number
  createdAt: string
  items: OrderItem[]
  shipper?: Shipper
  assignedAt?: string
}

// RevenueReport
export interface RevenueReport {
  reportId: number
  fromDate: string
  toDate: string
  reportType: ReportType
  totalRevenue: number
  totalOrders: number
  createdBy: User
  createdAt: string
}