package pixelism.jewelryshop;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;
import pixelism.jewelryshop.repositories.OrderRepository;
import pixelism.jewelryshop.repositories.ShipperRepository;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shippers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shipper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shipperId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipperStatus shipperStatus;

    @OneToMany(mappedBy = "shipper", fetch = FetchType.LAZY)
    private List<Order> orders;

    @PrePersist
    protected void onCreate() {
        if (this.shipperStatus == null) this.shipperStatus = ShipperStatus.ACTIVE;
    }

    public enum ShipperStatus {
        ACTIVE, INACTIVE
    }

    public List<Shipper> getAll(ShipperRepository shipperRepository) {
        return shipperRepository.findAll();
    }

    public List<Shipper> getActiveShippers(ShipperRepository shipperRepository) {
        return shipperRepository.findByShipperStatus(ShipperStatus.ACTIVE);
    }

    public Shipper getById(Long id, ShipperRepository shipperRepository) {
        return shipperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy shipper"));
    }

    public Shipper create(Shipper shipper, ShipperRepository shipperRepository) {
        if (shipperRepository.findByPhone(shipper.getPhone()).isPresent())
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        if (shipperRepository.findByEmail(shipper.getEmail()).isPresent())
            throw new RuntimeException("Email đã được sử dụng");
        return shipperRepository.save(shipper);
    }

    public Shipper update(Long id, Shipper request, ShipperRepository shipperRepository) {
        Shipper existing = getById(id, shipperRepository);
        existing.setFullName(request.getFullName());
        existing.setPhone(request.getPhone());
        existing.setEmail(request.getEmail());
        existing.setShipperStatus(request.getShipperStatus());
        return shipperRepository.save(existing);
    }

    @Transactional
    public Order assignShipperToOrder(String orderCode, Long shipperId,
                                      OrderRepository orderRepository,
                                      ShipperRepository shipperRepository) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (order.getStatus() != Order.OrderStatus.CONFIRMED)
            throw new RuntimeException("Chỉ có thể gán shipper cho đơn hàng ở trạng thái CONFIRMED");

        Shipper shipper = getById(shipperId, shipperRepository);
        if (shipper.getShipperStatus() != ShipperStatus.ACTIVE)
            throw new RuntimeException("Shipper không hoạt động");

        order.setShipper(shipper);
        order.setAssignedAt(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.SHIPPING);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateDeliveryStatus(String orderCode, Long shipperId,
                                      Order.OrderStatus newStatus,
                                      OrderRepository orderRepository) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (order.getShipper() == null || !order.getShipper().getShipperId().equals(shipperId))
            throw new RuntimeException("Bạn không được phân công đơn hàng này");

        boolean valid = switch (order.getStatus()) {
            case SHIPPING -> newStatus == Order.OrderStatus.DELIVERED || newStatus == Order.OrderStatus.CANCELLED;
            default -> false;
        };

        if (!valid)
            throw new RuntimeException("Không thể chuyển từ " + order.getStatus() + " sang " + newStatus);

        order.setStatus(newStatus);
        if (newStatus == Order.OrderStatus.DELIVERED)
            order.setPaymentStatus(Order.PaymentStatus.PAID);

        return orderRepository.save(order);
    }
}