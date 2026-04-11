package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pixelism.jewelryshop.features.Order;
import pixelism.jewelryshop.features.Shipper;
import pixelism.jewelryshop.features.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByOrderCode(String orderCode);

    @Query("SELECT o FROM Order o WHERE o.paymentStatus = 'PAID' " +
            "AND CAST(o.createdAt AS date) BETWEEN :from AND :to")
    List<Order> findPaidOrdersBetween(@Param("from") LocalDate from,
                                      @Param("to") LocalDate to);

    Optional<Shipper> findByShipper_ShipperId(Long shipperShipperId);
}