package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.entities.Order;
import pixelism.jewelryshop.entities.Shipper;
import pixelism.jewelryshop.services.ShipperService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/shippers")
@RequiredArgsConstructor
public class ShipperController {

    private final ShipperService shipperService;

    @GetMapping
    public List<Shipper> getAll() {
        return shipperService.getAll();
    }

    @GetMapping("/active")
    public List<Shipper> getActive() {
        return shipperService.getActiveShippers();
    }

    @PostMapping
    public ResponseEntity<Shipper> create(@RequestBody Shipper shipper) {
        return ResponseEntity.ok(shipperService.create(shipper));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shipper> update(@PathVariable Long id,
                                          @RequestBody Shipper shipper) {
        return ResponseEntity.ok(shipperService.update(id, shipper));
    }

    @PostMapping("/assign/{orderCode}")
    public ResponseEntity<?> assignToOrder(@PathVariable String orderCode,
                                           @RequestBody Map<String, Long> body) {
        Order order = shipperService.assignShipperToOrder(orderCode, body.get("shipperId"));
        return ResponseEntity.ok(Map.of(
                "message", "Đã gán shipper và chuyển sang trạng thái SHIPPING",
                "orderCode", order.getOrderCode(),
                "status", order.getStatus()
        ));
    }

    @PutMapping("/delivery/{orderCode}/status")
    public ResponseEntity<?> updateDeliveryStatus(@PathVariable String orderCode,
                                                @RequestParam Long shipperId,
                                                @RequestBody Map<String, String> body) {
        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(body.get("status"));
        Order order = shipperService.updateDeliveryStatus(orderCode, shipperId, newStatus);
        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật trạng thái thành công",
                "orderCode", order.getOrderCode(),
                "status", order.getStatus()
        ));
    }
}