package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.features.Order;
import pixelism.jewelryshop.features.Shipper;
import pixelism.jewelryshop.repositories.OrderRepository;
import pixelism.jewelryshop.repositories.ShipperRepository;

import java.util.Map;

// Shipper endpoints: /api/shipper
@RestController
@RequestMapping("/api/shipper")
@RequiredArgsConstructor
public class DeliveryController {

    private final ShipperRepository shipperRepository;
    private final OrderRepository orderRepository;
    private final Shipper shipper = new Shipper();

    // Shipper xem danh sách đơn được gán
    @GetMapping("/orders")
    public ResponseEntity<?> getMyOrders(@RequestParam Long shipperId) {
        return ResponseEntity.ok(orderRepository.findByShipper_ShipperId(shipperId));
    }

    // Shipper cập nhật trạng thái giao hàng
    @PutMapping("/delivery/{orderCode}/status")
    public ResponseEntity<?> updateDelivery(@PathVariable String orderCode,
                                            @RequestParam Long shipperId,
                                            @RequestBody Map<String, String> body) {
        Order order = shipper.updateDeliveryStatus(orderCode, shipperId,
                Order.OrderStatus.valueOf(body.get("status")), orderRepository);
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật trạng thái thành công",
                "orderCode", order.getOrderCode(),
                "status", order.getStatus()
        ));
    }
}