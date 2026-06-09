package com.monitor.controller;

import com.monitor.model.ApiEntry;
import com.monitor.model.ApiStatusResult;
import com.monitor.service.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.*;

@RestController
@RequestMapping("/api/monitor")
@CrossOrigin(origins = "*")
public class MonitorController {

    @Autowired ExcelReaderService  excelReader;
    @Autowired ApiCheckerService   apiChecker;
    @Autowired AiAnalysisService   aiAnalysis;
    @Autowired PdfReportService    pdfReport;

    // ─── POST /api/monitor/check ──────────────────────────────────────────
    // Upload Excel → check all APIs → return JSON results
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> check(
            @RequestParam("file")   MultipartFile file,
            @RequestParam(value = "apiKey", defaultValue = "") String apiKey) {

        try {
            List<ApiEntry>       entries = excelReader.read(file);
            List<ApiStatusResult> results = apiChecker.checkAll(entries);

            long up   = results.stream().filter(ApiStatusResult::isUp).count();
            long down = results.size() - up;

            return ResponseEntity.ok(Map.of(
                    "total",   results.size(),
                    "up",      up,
                    "down",    down,
                    "results", results
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Failed to process file: " + e.getMessage()));
        }
    }

    // ─── POST /api/monitor/report ─────────────────────────────────────────
    // Upload Excel → check APIs → AI analysis → return PDF
    @PostMapping("/report")
    public ResponseEntity<byte[]> report(
            @RequestParam("file")   MultipartFile file,
            @RequestParam(value = "apiKey", defaultValue = "") String apiKey) {

        try {
            List<ApiEntry>        entries  = excelReader.read(file);
            List<ApiStatusResult> results  = apiChecker.checkAll(entries);
            String                analysis = aiAnalysis.analyze(results, apiKey);
            byte[]                pdf      = pdfReport.generate(results, analysis);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=api-status-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ─── GET /api/monitor/template ────────────────────────────────────────
    // Download a sample Excel template the user can fill in
    @GetMapping("/template")
    public ResponseEntity<byte[]> template() throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("APIs");

            // Header row style
            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font hFont = wb.createFont();
            hFont.setColor(IndexedColors.WHITE.getIndex());
            hFont.setBold(true);
            headerStyle.setFont(hFont);

            Row hRow = sheet.createRow(0);
            String[] cols = {"API Name", "URL", "Description"};
            for (int i = 0; i < cols.length; i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 8000);
            }

            // Sample data rows
            Object[][] data = {
                {"URL Shortener API",       "http://localhost:8081/actuator/health", "URL shortening service"},
                {"Email Validation API",    "http://localhost:8082/actuator/health", "Email validation service"},
                {"Password Validation API", "http://localhost:8083/actuator/health", "Password strength checker"},
            };
            int row = 1;
            for (Object[] d : data) {
                Row r = sheet.createRow(row++);
                for (int i = 0; i < d.length; i++)
                    r.createCell(i).setCellValue((String) d[i]);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=api-list-template.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
    }
}
