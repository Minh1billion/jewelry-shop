package pixelism.jewelryshop;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.repositories.OrderRepository;
import pixelism.jewelryshop.repositories.ShipperRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/shippers")
@RequiredArgsConstructor
public class ShipperController {

    private final ShipperRepository shipperRepository;
    private final OrderRepository orderRepository;
    private final Shipper shipper = new Shipper();

    @GetMapping
    public List<Shipper> getAll() {
        return shipper.getAll(shipperRepository);
    }

    @GetMapping("/active")
    public List<Shipper> getActive() {
        return shipper.getActiveShippers(shipperRepository);
    }

    @PostMapping
    public ResponseEntity<Shipper> create(@RequestBody Shipper s) {
        return ResponseEntity.ok(shipper.create(s, shipperRepository));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shipper> update(@PathVariable Long id, @RequestBody Shipper s) {
        return ResponseEntity.ok(shipper.update(id, s, shipperRepository));
    }

    @PostMapping("/assign/{orderCode}")
    public ResponseEntity<?> assign(@PathVariable String orderCode,
                                    @RequestBody Map<String, Long> body) {
        Order order = shipper.assignShipperToOrder(orderCode, body.get("shipperId"),
                orderRepository, shipperRepository);
        return ResponseEntity.ok(Map.of(
                "message", "Đã gán shipper và chuyển sang trạng thái SHIPPING",
                "orderCode", order.getOrderCode(),
                "status", order.getStatus()
        ));
    }

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