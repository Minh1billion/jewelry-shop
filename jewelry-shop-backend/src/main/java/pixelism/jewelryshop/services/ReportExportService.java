package pixelism.jewelryshop.services;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pixelism.jewelryshop.entities.RevenueReport;
import java.io.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportExportService {

    private final RevenueReportService reportService;

    public byte[] exportPdf(Long reportId) throws IOException {
        RevenueReport r = reportService.getById(reportId);
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

    public byte[] exportCsv(Long reportId) throws IOException {
        RevenueReport r = reportService.getById(reportId);
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

    public byte[] exportExcel(Long reportId) throws IOException {
        RevenueReport r = reportService.getById(reportId);
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