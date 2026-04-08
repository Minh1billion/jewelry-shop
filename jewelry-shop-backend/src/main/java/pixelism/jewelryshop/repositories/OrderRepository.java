package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pixelism.jewelryshop.entities.Order;
import pixelism.jewelryshop.entities.User;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByOrderCode(String orderCode);
}