package pixelism.jewelryshop.features;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;
import pixelism.jewelryshop.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "carts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Cart addToCart(User user, Long productId, int quantity,
                          CartRepository cartRepository,
                          CartItemRepository cartItemRepository,
                          ProductRepository productRepository) {
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (product.getStock() == null || product.getStock() <= 0)
            throw new RuntimeException("Sản phẩm hiện đã hết hàng");

        cartItemRepository.findByCartAndProduct(cart, product).ifPresentOrElse(
                item -> {
                    item.setQuantity(item.getQuantity() + quantity);
                    item.setUnitPrice(product.getPrice());
                    cartItemRepository.save(item);
                },
                () -> cartItemRepository.save(CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(quantity)
                        .unitPrice(product.getPrice())
                        .selected(false)
                        .build())
        );

        return cart;
    }

    public List<CartItem> getCartItems(User user,
                                       CartRepository cartRepository,
                                       CartItemRepository cartItemRepository) {
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
        return cart.getItems();
    }

    @Transactional
    public void selectItems(User user, List<Long> cartItemIds,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));

        List<CartItem> allItems = cartItemRepository.findByCart(cart);
        for (CartItem item : allItems) {
            item.setSelected(cartItemIds.contains(item.getCartItemId()));
            cartItemRepository.save(item);
        }
    }

    @Transactional
    public Order checkout(User user, List<CartItem> selectedItems,
                          String recipientName, String recipientPhone,
                          String shippingAddress, String note,
                          OrderRepository orderRepository,
                          CartItemRepository cartItemRepository) {
        if (selectedItems == null || selectedItems.isEmpty())
            throw new RuntimeException("Bạn chưa chọn sản phẩm nào để thanh toán");

        BigDecimal total = selectedItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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