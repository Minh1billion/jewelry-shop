package pixelism.jewelryshop.features;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pixelism.jewelryshop.repositories.CartItemRepository;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Boolean selected = false;

    @Transient
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void removeItem(Long cartItemId, CartItemRepository cartItemRepository) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item không tồn tại"));
        cartItemRepository.delete(item);
    }

    public CartItem updateQuantity(Long cartItemId, int quantity, CartItemRepository cartItemRepository) {
        if (quantity <= 0)
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item không tồn tại"));
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }
}