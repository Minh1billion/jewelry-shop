export interface User {
  id: number
  username: string
  role: string
  fullName: string
  phone: string
}

export interface Category {
  id: number
  name: string
  description: string
}

export interface Product {
  id: number
  name: string
  description: string
  price: number
  imageUrl: string | null
  stock: number
  active: boolean
  category: Category
  createdAt: string
}

export interface CartItem {
  id: number
  product: Product
  quantity: number
  unitPrice: number
  selected: boolean
}

export interface OrderItem {
  id: number
  product: Product
  quantity: number
  unitPrice: number
}

export interface Shipper {
  id: number
  fullName: string
  phone: string
  email: string
  status: 'ACTIVE' | 'INACTIVE'
}

export interface Order {
  id: number
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

export type ReportType = 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY'

export interface RevenueReport {
  id: number
  fromDate: string
  toDate: string
  reportType: ReportType
  totalRevenue: number
  totalOrders: number
  createdBy: User
  createdAt: string
}

export interface Page<T> {
  content: T[]
  totalPages: number
  totalElements: number
  number: number
}