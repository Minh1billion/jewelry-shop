package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.features.Cart;
import pixelism.jewelryshop.features.CartItem;
import pixelism.jewelryshop.features.Order;
import pixelism.jewelryshop.features.User;
import pixelism.jewelryshop.repositories.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final Cart cart = new Cart();

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(cart.getCartItems(user, cartRepository, cartItemRepository));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> body) {
        Long userId    = Long.valueOf(body.get("userId").toString());
        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity   = Integer.parseInt(body.get("quantity").toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        cart.addToCart(user, productId, quantity, cartRepository, cartItemRepository, productRepository);
        return ResponseEntity.ok(Map.of("message", "Đã thêm vào giỏ hàng"));
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long cartItemId,
                                            @RequestBody Map<String, Object> body) {
        int quantity = Integer.parseInt(body.get("quantity").toString());
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật số lượng"));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/select")
    public ResponseEntity<Void> select(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Long> cartItemIds = ((List<?>) body.get("cartItemIds"))
                .stream().map(id -> Long.valueOf(id.toString())).toList();
        cart.selectItems(user, cartItemIds, cartRepository, cartItemRepository);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Long> cartItemIds = ((List<?>) body.get("selectedItemIds"))
                .stream().map(id -> Long.valueOf(id.toString())).toList();
        List<CartItem> selectedItems = cartItemRepository.findAllById(cartItemIds);
        Order order = cart.checkout(
                user, selectedItems,
                body.get("recipientName").toString(),
                body.get("recipientPhone").toString(),
                body.get("shippingAddress").toString(),
                body.get("note") != null ? body.get("note").toString() : null,
                orderRepository, cartItemRepository
        );
        return ResponseEntity.ok(order);
    }
}