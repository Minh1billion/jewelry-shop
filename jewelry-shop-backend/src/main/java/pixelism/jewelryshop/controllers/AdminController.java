package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.features.Order;
import pixelism.jewelryshop.features.RevenueReport;
import pixelism.jewelryshop.features.User;
import pixelism.jewelryshop.repositories.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RevenueReportRepository reportRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @PostMapping("/reports/generate")
    public ResponseEntity<RevenueReport> generateReport(@RequestBody Map<String, String> body) {
        LocalDate from = LocalDate.parse(body.get("fromDate"));
        LocalDate to   = LocalDate.parse(body.get("toDate"));
        RevenueReport.ReportType type = RevenueReport.ReportType.valueOf(body.get("reportType"));

        User admin = userRepository.findById(Long.valueOf(body.get("adminId")))
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        RevenueReport delegate = new RevenueReport();
        return ResponseEntity.ok(delegate.generateReport(from, to, type, admin, reportRepository));
    }

    @GetMapping("/reports/{id}/export")
    public ResponseEntity<byte[]> exportReport(@PathVariable Long id,
                                               @RequestParam String format,
                                               @RequestParam String filename) {
        if (!filename.matches("^[\\w\\-. ]+$"))
            throw new RuntimeException("Tên file không hợp lệ");

        RevenueReport r = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Báo cáo không tồn tại"));
        byte[] data = r.exportFile(id, format, filename, reportRepository);

        MediaType mediaType = switch (format.toLowerCase()) {
            case "pdf"   -> MediaType.APPLICATION_PDF;
            case "csv"   -> MediaType.parseMediaType("text/csv");
            case "excel" -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            default -> throw new RuntimeException("Định dạng không hỗ trợ");
        };

        String ext = switch (format.toLowerCase()) {
            case "pdf" -> ".pdf"; case "csv" -> ".csv"; case "excel" -> ".xlsx"; default -> "";
        };

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + ext + "\"")
                .body(data);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PutMapping("/orders/{orderCode}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String orderCode,
                                               @RequestBody Map<String, String> body) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        order.setStatus(Order.OrderStatus.valueOf(body.get("status")));
        orderRepository.save(order);
        return ResponseEntity.ok(Map.of("message", "Đã cập nhật trạng thái"));
    }
}