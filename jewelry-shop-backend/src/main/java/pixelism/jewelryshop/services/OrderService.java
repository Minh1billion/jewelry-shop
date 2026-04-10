package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pixelism.jewelryshop.entities.Order;
import pixelism.jewelryshop.entities.User;
import pixelism.jewelryshop.repositories.OrderRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Order getOrderDetail(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order confirmOrder(String orderCode) {
        Order order = getOrderDetail(orderCode);
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể xác nhận đơn hàng ở trạng thái PENDING");
        }
        order.setStatus(Order.OrderStatus.CONFIRMED);
        return orderRepository.save(order);
    }
}