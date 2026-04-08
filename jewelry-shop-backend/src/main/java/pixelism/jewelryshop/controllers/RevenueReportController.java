package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.entities.RevenueReport;
import pixelism.jewelryshop.entities.User;
import pixelism.jewelryshop.repositories.UserRepository;
import pixelism.jewelryshop.services.ReportExportService;
import pixelism.jewelryshop.services.RevenueReportService;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class RevenueReportController {

    private final RevenueReportService reportService;
    private final ReportExportService exportService;
    private final UserRepository userRepository;

    @PostMapping("/generate")
    public ResponseEntity<RevenueReport> generate(@RequestBody Map<String, String> body) {
        LocalDate from = LocalDate.parse(body.get("fromDate"));
        LocalDate to   = LocalDate.parse(body.get("toDate"));
        RevenueReport.ReportType type = RevenueReport.ReportType.valueOf(body.get("reportType"));
        Long adminId = Long.valueOf(body.get("adminId"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        reportService.validateDateRange(from, to, type);
        return ResponseEntity.ok(reportService.generate(from, to, type, admin));
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> export(@PathVariable Long id,
                                         @RequestParam String format,
                                         @RequestParam String filename) throws Exception {
        if (!filename.matches("^[\\w\\-. ]+$"))
            throw new RuntimeException("Tên file không hợp lệ, vui lòng nhập lại");

        return switch (format.toLowerCase()) {
            case "pdf" -> buildResponse(
                    exportService.exportPdf(id),
                    MediaType.APPLICATION_PDF,
                    filename + ".pdf");
            case "csv" -> buildResponse(
                    exportService.exportCsv(id),
                    MediaType.parseMediaType("text/csv"),
                    filename + ".csv");
            case "excel" -> buildResponse(
                    exportService.exportExcel(id),
                    MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                    filename + ".xlsx");
            default -> throw new RuntimeException("Định dạng không hỗ trợ");
        };
    }

    private ResponseEntity<byte[]> buildResponse(byte[] data, MediaType type, String filename) {
        return ResponseEntity.ok()
                .contentType(type)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(data);
    }
}