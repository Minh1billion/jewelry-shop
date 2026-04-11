package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.features.Order;
import pixelism.jewelryshop.features.Shipper;
import pixelism.jewelryshop.repositories.OrderRepository;
import pixelism.jewelryshop.repositories.ShipperRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/shippers")
@RequiredArgsConstructor
public class ShipperController {

    private final ShipperRepository shipperRepository;
    private final OrderRepository orderRepository;

    @GetMapping
    public List<Shipper> getAll() {
        return shipperRepository.findAll();
    }

    @GetMapping("/active")
    public List<Shipper> getActive() {
        return shipperRepository.findByShipperStatus(Shipper.ShipperStatus.ACTIVE);
    }

    @PostMapping
    public ResponseEntity<Shipper> create(@RequestBody Shipper s) {
        if (shipperRepository.findByPhone(s.getPhone()).isPresent())
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        if (shipperRepository.findByEmail(s.getEmail()).isPresent())
            throw new RuntimeException("Email đã được sử dụng");
        return ResponseEntity.ok(shipperRepository.save(s));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shipper> update(@PathVariable Long id, @RequestBody Shipper s) {
        Shipper existing = shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper"));
        existing.setFullName(s.getFullName());
        existing.setPhone(s.getPhone());
        existing.setEmail(s.getEmail());
        existing.setShipperStatus(s.getShipperStatus());
        return ResponseEntity.ok(shipperRepository.save(existing));
    }

    @Transactional
    @PostMapping("/assign/{orderCode}")
    public ResponseEntity<?> assign(@PathVariable String orderCode,
                                    @RequestBody Map<String, Long> body) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (order.getStatus() != Order.OrderStatus.CONFIRMED)
            throw new RuntimeException("Chỉ có thể gán shipper cho đơn hàng ở trạng thái CONFIRMED");

        Shipper shipper = shipperRepository.findById(body.get("shipperId"))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper"));

        if (shipper.getShipperStatus() != Shipper.ShipperStatus.ACTIVE)
            throw new RuntimeException("Shipper không hoạt động");

        order.setShipper(shipper);
        order.setAssignedAt(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.SHIPPING);
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Đã gán shipper và chuyển sang trạng thái SHIPPING",
                "orderCode", order.getOrderCode(),
                "status", order.getStatus()
        ));
    }

    @Transactional
    @PutMapping("/delivery/{orderCode}/status")
    public ResponseEntity<?> updateDelivery(@PathVariable String orderCode,
                                            @RequestParam Long shipperId,
                                            @RequestBody Map<String, String> body) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (order.getShipper() == null || !order.getShipper().getShipperId().equals(shipperId))
            throw new RuntimeException("Shipper không được phân công đơn hàng này");

        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(body.get("status"));
        boolean valid = order.getStatus() == Order.OrderStatus.SHIPPING &&
                (newStatus == Order.OrderStatus.DELIVERED || newStatus == Order.OrderStatus.CANCELLED);

        if (!valid)
            throw new RuntimeException("Không thể chuyển từ " + order.getStatus() + " sang " + newStatus);

        order.setStatus(newStatus);
        if (newStatus == Order.OrderStatus.DELIVERED)
            order.setPaymentStatus(Order.PaymentStatus.PAID);

        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật trạng thái thành công",
                "orderCode", order.getOrderCode(),
                "status", order.getStatus()
        ));
    }
}