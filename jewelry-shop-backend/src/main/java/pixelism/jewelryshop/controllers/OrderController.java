package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.features.Order;
import pixelism.jewelryshop.features.User;
import pixelism.jewelryshop.repositories.OrderRepository;
import pixelism.jewelryshop.repositories.UserRepository;
import pixelism.jewelryshop.services.OrderService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final Order order = new Order();
    private final OrderRepository orderRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(orderService.getOrdersByUser(user));
    }

    @GetMapping("/{orderCode}")
    public ResponseEntity<Order> getOrderDetail(@PathVariable String orderCode) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderCode));
    }

    @PutMapping("/{orderCode}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String orderCode) {
        Order order = orderService.getOrderDetail(orderCode);
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            return ResponseEntity.badRequest().body(Map.of("message", "Chỉ hủy được đơn đang chờ xử lý"));
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderService.save(order);
        return ResponseEntity.ok(Map.of("message", "Đã hủy đơn"));
    }

    @PutMapping("/{orderCode}/confirm")
    public ResponseEntity<?> confirm(@PathVariable String orderCode) {
        Order o = order.confirmOrder(orderCode, orderRepository);
        return ResponseEntity.ok(Map.of(
                "message", "Đã xác nhận đơn hàng",
                "orderCode", o.getOrderCode(),
                "status", o.getStatus()
        ));
    }
}