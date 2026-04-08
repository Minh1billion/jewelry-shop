package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.entities.CartItem;
import pixelism.jewelryshop.entities.Order;
import pixelism.jewelryshop.entities.User;
import pixelism.jewelryshop.repositories.CartItemRepository;
import pixelism.jewelryshop.repositories.UserRepository;
import pixelism.jewelryshop.services.CheckoutService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    @PostMapping("/select")
    public ResponseEntity<Void> selectItems(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Long> cartItemIds = ((List<?>) body.get("cartItemIds"))
                .stream()
                .map(id -> Long.valueOf(id.toString()))
                .toList();

        checkoutService.selectItems(user, cartItemIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Order> checkout(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Long> cartItemIds = ((List<?>) body.get("selectedItemIds"))
                .stream()
                .map(id -> Long.valueOf(id.toString()))
                .toList();

        List<CartItem> selectedItems = cartItemRepository.findAllById(cartItemIds);

        Order order = checkoutService.checkout(
                user,
                selectedItems,
                body.get("recipientName").toString(),
                body.get("recipientPhone").toString(),
                body.get("shippingAddress").toString(),
                body.get("note") != null ? body.get("note").toString() : null
        );

        return ResponseEntity.ok(order);
    }
}