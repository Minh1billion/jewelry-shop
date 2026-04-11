package pixelism.jewelryshop;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.repositories.OrderRepository;
import pixelism.jewelryshop.repositories.UserRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final Order order = new Order();

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(order.getOrdersByUser(user, orderRepository));
    }

    @GetMapping("/{orderCode}")
    public ResponseEntity<Order> getDetail(@PathVariable String orderCode) {
        return ResponseEntity.ok(order.getOrderDetail(orderCode, orderRepository));
    }

    @PutMapping("/{orderCode}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String orderCode) {
        Order o = order.getOrderDetail(orderCode, orderRepository);
        if (o.getStatus() != Order.OrderStatus.PENDING)
            return ResponseEntity.badRequest().body(Map.of("message", "Chỉ hủy được đơn đang chờ xử lý"));
        o.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(o);
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