package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pixelism.jewelryshop.entities.Order;
import pixelism.jewelryshop.entities.Shipper;
import pixelism.jewelryshop.repositories.OrderRepository;
import pixelism.jewelryshop.repositories.ShipperRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipperService {

    private final ShipperRepository shipperRepository;
    private final OrderRepository orderRepository;

    public List<Shipper> getAll() {
        return shipperRepository.findAll();
    }

    public List<Shipper> getActiveShippers() {
        return shipperRepository.findByStatus(Shipper.ShipperStatus.ACTIVE);
    }

    public Shipper getById(Long id) {
        return shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper"));
    }

    public Shipper create(Shipper shipper) {
        if (shipperRepository.findByPhone(shipper.getPhone()).isPresent())
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        if (shipperRepository.findByEmail(shipper.getEmail()).isPresent())
            throw new RuntimeException("Email đã được sử dụng");
        return shipperRepository.save(shipper);
    }

    public Shipper update(Long id, Shipper request) {
        Shipper existing = getById(id);
        existing.setFullName(request.getFullName());
        existing.setPhone(request.getPhone());
        existing.setEmail(request.getEmail());
        existing.setStatus(request.getStatus());
        return shipperRepository.save(existing);
    }

    @Transactional
    public Order assignShipperToOrder(String orderCode, Long shipperId) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new RuntimeException(
                    "Chỉ có thể gán shipper cho đơn hàng ở trạng thái CONFIRMED"
            );
        }

        Shipper shipper = getById(shipperId);
        if (shipper.getStatus() != Shipper.ShipperStatus.ACTIVE) {
            throw new RuntimeException("Shipper không hoạt động");
        }

        order.setShipper(shipper);
        order.setAssignedAt(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.SHIPPING); // Tự động chuyển sang SHIPPING
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateDeliveryStatus(String orderCode, Long shipperId,
                                      Order.OrderStatus newStatus) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        // Xác minh đúng shipper được gán
        if (order.getShipper() == null || !order.getShipper().getId().equals(shipperId)) {
            throw new RuntimeException("Bạn không được phân công đơn hàng này");
        }

        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        if (newStatus == Order.OrderStatus.DELIVERED) {
            order.setPaymentStatus(Order.PaymentStatus.PAID);
        }
        return orderRepository.save(order);
    }

    private void validateStatusTransition(Order.OrderStatus current,
                                          Order.OrderStatus next) {
        boolean valid = switch (current) {
            case SHIPPING -> next == Order.OrderStatus.DELIVERED
                    || next == Order.OrderStatus.CANCELLED;
            default -> false;
        };
        if (!valid) {
            throw new RuntimeException(
                    "Không thể chuyển từ " + current + " sang " + next
            );
        }
    }
}