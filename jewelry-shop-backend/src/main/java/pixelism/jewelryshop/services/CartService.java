package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pixelism.jewelryshop.entities.*;
import pixelism.jewelryshop.repositories.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Cart addToCart(User user, Long productId, int quantity) {
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (product.getStock() == null || product.getStock() <= 0) {
            throw new RuntimeException("Sản phẩm hiện đã hết hàng");
        }

        cartItemRepository.findByCartAndProduct(cart, product).ifPresentOrElse(
                item -> {
                    item.setQuantity(item.getQuantity() + quantity);
                    item.setUnitPrice(product.getPrice());
                    cartItemRepository.save(item);
                },
                () -> {
                    CartItem newItem = CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(quantity)
                            .unitPrice(product.getPrice())
                            .selected(false)
                            .build();
                    cartItemRepository.save(newItem);
                }
        );

        return cart;
    }

    public List<CartItem> getCartItems(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
        return cart.getItems();
    }
}