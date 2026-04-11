package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pixelism.jewelryshop.Order;
import pixelism.jewelryshop.RevenueReport;
import java.time.LocalDate;
import java.util.List;

    public interface RevenueReportRepository extends JpaRepository<RevenueReport, Long> {

    @Query("""
        SELECT o FROM Order o
        WHERE o.paymentStatus = 'PAID'
        AND CAST(o.createdAt AS date) BETWEEN :from AND :to
    """)
    List<Order> findPaidOrdersBetween(LocalDate from, LocalDate to);
}