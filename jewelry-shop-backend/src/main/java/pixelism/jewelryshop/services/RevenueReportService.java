package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pixelism.jewelryshop.entities.Order;
import pixelism.jewelryshop.entities.RevenueReport;
import pixelism.jewelryshop.entities.User;
import pixelism.jewelryshop.repositories.RevenueReportRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueReportService {

    private final RevenueReportRepository reportRepository;

    private BigDecimal calculateRevenue(List<Order> orders) {
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void validateDateRange(LocalDate from, LocalDate to, RevenueReport.ReportType type) {
        if (from == null || to == null)
            throw new RuntimeException("Mốc thời gian không hợp lệ, vui lòng nhập lại");
        if (from.isAfter(to))
            throw new RuntimeException("Mốc thời gian không hợp lệ, vui lòng nhập lại");

        long days = ChronoUnit.DAYS.between(from, to);
        switch (type) {
            case WEEKLY  -> { if (days < 7)   throw new RuntimeException("Mốc thời gian không hợp lệ, vui lòng nhập lại"); }
            case MONTHLY -> { if (days < 30)  throw new RuntimeException("Mốc thời gian không hợp lệ, vui lòng nhập lại"); }
            case YEARLY  -> { if (days < 365) throw new RuntimeException("Mốc thời gian không hợp lệ, vui lòng nhập lại"); }
        }
    }

    public RevenueReport generate(LocalDate from, LocalDate to,
                                  RevenueReport.ReportType type, User admin) {
        List<Order> orders = reportRepository.findPaidOrdersBetween(from, to);

        if (orders.isEmpty())
            throw new RuntimeException("Hiện chưa có dữ liệu, hãy quay lại sau");

        BigDecimal total = calculateRevenue(orders);

        RevenueReport report = RevenueReport.builder()
                .fromDate(from)
                .toDate(to)
                .reportType(type)
                .totalRevenue(total)
                .totalOrders(orders.size())
                .createdBy(admin)
                .build();

        return reportRepository.save(report);
    }

    public RevenueReport getById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Báo cáo không tồn tại"));
    }
}