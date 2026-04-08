package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pixelism.jewelryshop.entities.*;
import pixelism.jewelryshop.repositories.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    private BigDecimal calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void selectItems(User user, List<Long> cartItemIds) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));

        List<CartItem> allItems = cartItemRepository.findByCart(cart);

        for (CartItem item : allItems) {
            item.setSelected(cartItemIds.contains(item.getId()));
            cartItemRepository.save(item);
        }
    }

    @Transactional
    public Order checkout(User user, List<CartItem> selectedItems,
                          String recipientName, String recipientPhone,
                          String shippingAddress, String note) {

        if (selectedItems == null || selectedItems.isEmpty()) {
            throw new RuntimeException("Bạn chưa chọn sản phẩm nào để thanh toán");
        }

        BigDecimal total = calculateTotalAmount(selectedItems);

        Order order = Order.builder()
                .orderCode(UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase())
                .user(user)
                .recipientName(recipientName)
                .recipientPhone(recipientPhone)
                .shippingAddress(shippingAddress)
                .note(note)
                .totalAmount(total)
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.UNPAID)
                .build();

        List<OrderItem> orderItems = selectedItems.stream().map(item ->
                OrderItem.builder()
                        .order(order)
                        .product(item.getProduct())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build()
        ).collect(Collectors.toList());

        order.setItems(orderItems);
        Order saved = orderRepository.save(order);

        cartItemRepository.deleteAll(selectedItems);

        return saved;
    }
}