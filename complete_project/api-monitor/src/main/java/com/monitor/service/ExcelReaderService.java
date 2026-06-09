package com.monitor.service;

import com.monitor.model.ApiEntry;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelReaderService {

    /**
     * Reads an Excel file and returns list of API entries.
     * Expected columns:
     *   A = API Name  (required)
     *   B = URL       (required)
     *   C = Description (optional)
     * Row 1 is treated as a header and skipped.
     */
    public List<ApiEntry> read(MultipartFile file) throws IOException {
        List<ApiEntry> entries = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = cell(row, 0);
                String url  = cell(row, 1);
                String desc = cell(row, 2);

                if (name.isBlank() || url.isBlank()) continue;

                // Auto-add http:// if missing
                if (!url.startsWith("http")) url = "http://" + url;

                entries.add(new ApiEntry(name.trim(), url.trim(), desc.trim()));
            }
        }
        return entries;
    }

    private String cell(Row row, int index) {
        Cell c = row.getCell(index);
        if (c == null) return "";
        return switch (c.getCellType()) {
            case STRING  -> c.getStringCellValue();
            case NUMERIC -> String.valueOf((long) c.getNumericCellValue());
            case BOOLEAN -> String.valueOf(c.getBooleanCellValue());
            default      -> "";
        };
    }
}
