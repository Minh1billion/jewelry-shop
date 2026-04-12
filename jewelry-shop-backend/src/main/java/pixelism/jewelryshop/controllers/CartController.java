package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.features.*;
import pixelism.jewelryshop.repositories.*;
import pixelism.jewelryshop.services.UserBehaviorService;

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
    private final CartItem cartItem = new CartItem();
    private final UserBehaviorService userBehaviorService;

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
        userBehaviorService.track(user, productId, UserBehavior.BehaviorType.ADD_TO_CART);
        return ResponseEntity.ok(Map.of("message", "Đã thêm vào giỏ hàng"));
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long cartItemId,
                                            @RequestBody Map<String, Object> body) {
        int quantity = Integer.parseInt(body.get("quantity").toString());
        cartItem.updateQuantity(cartItemId, quantity, cartItemRepository);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật số lượng"));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId) {
        cartItem.removeItem(cartItemId, cartItemRepository);
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
        for (Long id : cartItemIds) {
            userBehaviorService.track(user, id, UserBehavior.BehaviorType.PURCHASE);
        }
        return ResponseEntity.ok(order);
    }
}