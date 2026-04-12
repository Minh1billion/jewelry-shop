package pixelism.jewelryshop.features;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.persistence.*;
import lombok.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pixelism.jewelryshop.repositories.RevenueReportRepository;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Table(name = "revenue_reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RevenueReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private LocalDate fromDate;

    @Column(nullable = false)
    private LocalDate toDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private Integer totalOrders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum ReportType {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }

    public void validateDateRange(LocalDate from, LocalDate to, ReportType type) {
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

    public RevenueReport generateReport(LocalDate from, LocalDate to, ReportType type, User admin,
                                        RevenueReportRepository reportRepository) {
        validateDateRange(from, to, type);

        List<Order> orders = reportRepository.findPaidOrdersBetween(from, to);
        if (orders.isEmpty())
            throw new RuntimeException("Hiện chưa có dữ liệu, hãy quay lại sau");

        BigDecimal total = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return reportRepository.save(RevenueReport.builder()
                .fromDate(from)
                .toDate(to)
                .reportType(type)
                .totalRevenue(total)
                .totalOrders(orders.size())
                .createdBy(admin)
                .build());
    }

    public byte[] exportFile(Long id, String format, String filename,
                             RevenueReportRepository reportRepository) {
        RevenueReport r = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Báo cáo không tồn tại"));

        try {
            return switch (format.toLowerCase()) {
                case "pdf"   -> exportPdf(r);
                case "csv"   -> exportCsv(r);
                case "excel" -> exportExcel(r);
                default      -> throw new RuntimeException("Định dạng không hỗ trợ");
            };
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xuất file: " + e.getMessage());
        }
    }

    private byte[] exportPdf(RevenueReport r) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);
        doc.add(new Paragraph("BÁO CÁO DOANH THU"));
        doc.add(new Paragraph("Loại: " + r.getReportType()));
        doc.add(new Paragraph("Từ: " + r.getFromDate() + " đến: " + r.getToDate()));
        doc.add(new Paragraph("Tổng đơn: " + r.getTotalOrders()));
        doc.add(new Paragraph("Tổng doanh thu: " + r.getTotalRevenue()));
        doc.close();
        return out.toByteArray();
    }

    private byte[] exportCsv(RevenueReport r) {
        StringWriter sw = new StringWriter();
        sw.append("Loại,Từ ngày,Đến ngày,Tổng đơn,Tổng doanh thu\n");
        sw.append(String.join(",",
                r.getReportType().name(),
                r.getFromDate().toString(),
                r.getToDate().toString(),
                r.getTotalOrders().toString(),
                r.getTotalRevenue().toString()
        ));
        return sw.toString().getBytes();
    }

    private byte[] exportExcel(RevenueReport r) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo");

        Row header = sheet.createRow(0);
        List<String> headers = List.of("Loại", "Từ ngày", "Đến ngày", "Tổng đơn", "Tổng doanh thu");
        for (int i = 0; i < headers.size(); i++)
            header.createCell(i).setCellValue(headers.get(i));

        Row data = sheet.createRow(1);
        data.createCell(0).setCellValue(r.getReportType().name());
        data.createCell(1).setCellValue(r.getFromDate().toString());
        data.createCell(2).setCellValue(r.getToDate().toString());
        data.createCell(3).setCellValue(r.getTotalOrders());
        data.createCell(4).setCellValue(r.getTotalRevenue().doubleValue());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}