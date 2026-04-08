package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.entities.Order;
import pixelism.jewelryshop.services.OrderService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Order> getAll() {
        return orderService.getAll();
    }

    @PutMapping("/{orderCode}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String orderCode,
                                          @RequestBody Map<String, String> body) {
        Order.OrderStatus status = Order.OrderStatus.valueOf(body.get("status"));
        Order order = orderService.getOrderDetail(orderCode);
        order.setStatus(status);
        orderService.save(order);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật trạng thái"));
    }
}