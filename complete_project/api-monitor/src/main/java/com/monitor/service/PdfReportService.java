package com.monitor.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.monitor.model.ApiStatusResult;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfReportService {

    // ─── Colors ────────────────────────────────────────────────────────────
    private static final Color NAVY       = new Color(15,  43, 77);
    private static final Color UP_GREEN   = new Color(21, 128, 61);
    private static final Color UP_LIGHT   = new Color(220, 252, 231);
    private static final Color DOWN_RED   = new Color(185, 28, 28);
    private static final Color DOWN_LIGHT = new Color(254, 226, 226);
    private static final Color ROW_ALT    = new Color(248, 250, 252);
    private static final Color BORDER     = new Color(226, 232, 240);
    private static final Color GRAY_TEXT  = new Color(100, 116, 139);

    public byte[] generate(List<ApiStatusResult> results, String aiAnalysis) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            doc.open();

            addHeader(doc, results);
            addSummaryBar(doc, results);
            doc.add(Chunk.NEWLINE);
            addResultsTable(doc, results);
            doc.add(Chunk.NEWLINE);
            addAiSection(doc, aiAnalysis);
            addFooter(writer, doc);

            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
        return out.toByteArray();
    }

    // ─── Header ─────────────────────────────────────────────────────────────
    private void addHeader(Document doc, List<ApiStatusResult> results) throws DocumentException {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(NAVY);
        cell.setPadding(20);
        cell.setBorder(Rectangle.NO_BORDER);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.WHITE);
        Font subFont   = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(180, 200, 220));

        String generated = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss"));

        Paragraph titlePara = new Paragraph();
        titlePara.add(new Chunk("API STATUS REPORT\n", titleFont));
        titlePara.add(new Chunk("Generated: " + generated, subFont));
        cell.addElement(titlePara);
        header.addCell(cell);
        doc.add(header);
        doc.add(new Paragraph(" "));
    }

    // ─── Summary bar ────────────────────────────────────────────────────────
    private void addSummaryBar(Document doc, List<ApiStatusResult> results) throws DocumentException {
        long up   = results.stream().filter(ApiStatusResult::isUp).count();
        long down = results.size() - up;
        double pct = results.isEmpty() ? 0 : (up * 100.0 / results.size());

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1, 1});

        table.addCell(summaryCell("TOTAL APIs", String.valueOf(results.size()),
                NAVY, Color.WHITE));
        table.addCell(summaryCell("UP", String.valueOf(up),
                UP_GREEN, Color.WHITE));
        table.addCell(summaryCell("DOWN", String.valueOf(down),
                down > 0 ? DOWN_RED : UP_GREEN, Color.WHITE));

        doc.add(table);

        // Availability text
        Font availFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, NAVY);
        Paragraph avail = new Paragraph(
                String.format("System Availability: %.1f%%", pct), availFont);
        avail.setAlignment(Element.ALIGN_RIGHT);
        avail.setSpacingBefore(6);
        doc.add(avail);
    }

    private PdfPCell summaryCell(String label, String value, Color bg, Color fg) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 9, fg);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, fg);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bg);
        cell.setPadding(14);
        cell.setBorderColor(Color.WHITE);
        cell.setBorderWidth(2);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", labelFont));
        p.add(new Chunk(value, valueFont));
        cell.addElement(p);
        return cell;
    }

    // ─── Results table ───────────────────────────────────────────────────────
    private void addResultsTable(Document doc, List<ApiStatusResult> results)
            throws DocumentException {

        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, NAVY);
        Paragraph section = new Paragraph("Detailed Results", sectionFont);
        section.setSpacingAfter(6);
        doc.add(section);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.5f, 2.5f, 2.5f, 1f, 1f});

        // Table headers
        String[] headers = {"#", "API Name", "URL", "Status", "Response"};
        Font hFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        for (String h : headers) {
            PdfPCell hCell = new PdfPCell(new Phrase(h, hFont));
            hCell.setBackgroundColor(NAVY);
            hCell.setPadding(8);
            hCell.setBorderColor(BORDER);
            hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hCell);
        }

        // Data rows
        Font rowFont  = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
        Font urlFont  = FontFactory.getFont(FontFactory.HELVETICA, 7, GRAY_TEXT);
        Font upFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, UP_GREEN);
        Font downFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, DOWN_RED);

        for (int i = 0; i < results.size(); i++) {
            ApiStatusResult r = results.get(i);
            Color rowBg = i % 2 == 0 ? Color.WHITE : ROW_ALT;

            // # column
            table.addCell(centeredCell(String.valueOf(i + 1), rowFont, rowBg));

            // API Name
            PdfPCell nameCell = new PdfPCell(new Phrase(r.getName(), rowFont));
            nameCell.setBackgroundColor(rowBg);
            nameCell.setPadding(7);
            nameCell.setBorderColor(BORDER);
            table.addCell(nameCell);

            // URL
            String displayUrl = r.getUrl().length() > 45
                    ? r.getUrl().substring(0, 45) + "..." : r.getUrl();
            PdfPCell urlCell = new PdfPCell(new Phrase(displayUrl, urlFont));
            urlCell.setBackgroundColor(rowBg);
            urlCell.setPadding(7);
            urlCell.setBorderColor(BORDER);
            table.addCell(urlCell);

            // Status
            boolean up = r.isUp();
            PdfPCell statusCell = new PdfPCell(
                    new Phrase(r.getStatus(), up ? upFont : downFont));
            statusCell.setBackgroundColor(up ? UP_LIGHT : DOWN_LIGHT);
            statusCell.setPadding(7);
            statusCell.setBorderColor(BORDER);
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(statusCell);

            // Response time
            String rt = up ? r.getResponseTimeMs() + " ms" : "-";
            table.addCell(centeredCell(rt, rowFont, rowBg));
        }
        doc.add(table);

        // Error details for DOWN APIs
        List<ApiStatusResult> downApis = results.stream().filter(r -> !r.isUp()).toList();
        if (!downApis.isEmpty()) {
            doc.add(new Paragraph(" "));
            Font errTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, DOWN_RED);
            doc.add(new Paragraph("Error Details", errTitle));

            Font errFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            for (ApiStatusResult r : downApis) {
                String msg = String.format("• %s — %s",
                        r.getName(),
                        r.getErrorMessage() != null ? r.getErrorMessage() : "Unreachable");
                doc.add(new Paragraph(msg, errFont));
            }
        }
    }

    private PdfPCell centeredCell(String text, Font font, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(7);
        cell.setBorderColor(BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    // ─── AI Analysis section ─────────────────────────────────────────────────
    private void addAiSection(Document doc, String analysis) throws DocumentException {
        if (analysis == null || analysis.isBlank()) return;

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, NAVY);
        Paragraph title = new Paragraph("AI Analysis & Recommendations", titleFont);
        title.setSpacingAfter(8);
        doc.add(title);

        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(239, 246, 255));
        cell.setBorderColor(new Color(59, 130, 246));
        cell.setBorderWidthLeft(4);
        cell.setBorderWidthTop(0.5f);
        cell.setBorderWidthRight(0.5f);
        cell.setBorderWidthBottom(0.5f);
        cell.setPadding(14);

        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(30, 60, 100));
        cell.addElement(new Paragraph(analysis, bodyFont));
        box.addCell(cell);
        doc.add(box);
    }

    // ─── Footer ──────────────────────────────────────────────────────────────
    private void addFooter(PdfWriter writer, Document doc) {
        PdfContentByte cb = writer.getDirectContent();
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, GRAY_TEXT);

        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                new Phrase("API Status Monitor — Confidential", footerFont),
                doc.getPageSize().getWidth() / 2, 30, 0);
    }
}
