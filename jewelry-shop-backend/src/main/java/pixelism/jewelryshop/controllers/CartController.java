package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.entities.CartItem;
import pixelism.jewelryshop.entities.User;
import pixelism.jewelryshop.repositories.CartItemRepository;
import pixelism.jewelryshop.repositories.UserRepository;
import pixelism.jewelryshop.services.CartService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(cartService.getCartItems(user));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = Integer.parseInt(body.get("quantity").toString());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartService.addToCart(user, productId, quantity);
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
}