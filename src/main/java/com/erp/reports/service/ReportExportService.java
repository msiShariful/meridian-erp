package com.erp.reports.service;

import com.erp.accounting.entity.Invoice;
import com.erp.accounting.repository.InvoiceRepository;
import com.erp.accounting.enums.InvoiceStatus;
import com.erp.ecommerce.entity.Order;
import com.erp.ecommerce.repository.OrderRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * Builds Excel (.xlsx via Apache POI) and PDF (via OpenPDF) exports for chosen datasets.
 * Read-only: pulls records through existing repositories only.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportExportService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;

    // ----------------------------------------------------------------------
    // Excel
    // ----------------------------------------------------------------------

    public byte[] buildExcel(String type) {
        String t = (type == null || type.isBlank()) ? "invoices" : type.toLowerCase();
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            if ("orders".equals(t)) {
                Sheet sheet = workbook.createSheet("Orders");
                String[] cols = {"Order #", "Customer", "Status", "Items", "Total"};
                writeHeader(sheet, cols, headerStyle);
                List<Order> orders = orderRepository.findAll();
                int r = 1;
                BigDecimal total = BigDecimal.ZERO;
                for (Order o : orders) {
                    Row row = sheet.createRow(r++);
                    row.createCell(0).setCellValue(safe(o.getOrderNumber()));
                    row.createCell(1).setCellValue(safe(o.getCustomerName()));
                    row.createCell(2).setCellValue(o.getStatus() == null ? "" : o.getStatus().getLabel());
                    row.createCell(3).setCellValue(o.getTotalQuantity());
                    row.createCell(4).setCellValue(o.getTotal() == null ? 0d : o.getTotal().doubleValue());
                    total = total.add(o.getTotal() == null ? BigDecimal.ZERO : o.getTotal());
                }
                writeTotalRow(sheet, r, 4, "Total", total, headerStyle);
                autosize(sheet, cols.length);
            } else {
                Sheet sheet = workbook.createSheet("Invoices");
                String[] cols = {"Invoice #", "Customer", "Issue Date", "Status", "Total"};
                writeHeader(sheet, cols, headerStyle);
                List<Invoice> invoices = invoiceRepository.findAll();
                int r = 1;
                BigDecimal total = BigDecimal.ZERO;
                for (Invoice i : invoices) {
                    Row row = sheet.createRow(r++);
                    row.createCell(0).setCellValue(safe(i.getInvoiceNumber()));
                    row.createCell(1).setCellValue(safe(i.getCustomerName()));
                    row.createCell(2).setCellValue(i.getIssueDate() == null ? "" : i.getIssueDate().toString());
                    row.createCell(3).setCellValue(i.getStatus() == null ? "" : i.getStatus().getLabel());
                    row.createCell(4).setCellValue(i.getTotal() == null ? 0d : i.getTotal().doubleValue());
                    total = total.add(i.getTotal() == null ? BigDecimal.ZERO : i.getTotal());
                }
                writeTotalRow(sheet, r, 4, "Total", total, headerStyle);
                autosize(sheet, cols.length);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to build Excel report", ex);
        }
    }

    private void writeHeader(Sheet sheet, String[] cols, CellStyle style) {
        Row header = sheet.createRow(0);
        for (int c = 0; c < cols.length; c++) {
            Cell cell = header.createCell(c);
            cell.setCellValue(cols[c]);
            cell.setCellStyle(style);
        }
    }

    private void writeTotalRow(Sheet sheet, int rowIdx, int valueCol, String label,
                               BigDecimal total, CellStyle style) {
        Row row = sheet.createRow(rowIdx);
        Cell labelCell = row.createCell(valueCol - 1);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(style);
        Cell valueCell = row.createCell(valueCol);
        valueCell.setCellValue(total.doubleValue());
        valueCell.setCellStyle(style);
    }

    private void autosize(Sheet sheet, int cols) {
        for (int c = 0; c < cols; c++) {
            sheet.autoSizeColumn(c);
        }
    }

    // ----------------------------------------------------------------------
    // PDF
    // ----------------------------------------------------------------------

    public byte[] buildPdf(String type) {
        String t = (type == null || type.isBlank()) ? "invoices" : type.toLowerCase();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 48, 48, 48, 48);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(15, 23, 42));
            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(100, 116, 139));
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new Color(15, 23, 42));

            Paragraph title = new Paragraph("Meridian ERP — Executive Summary", titleFont);
            document.add(title);
            Paragraph sub = new Paragraph("Automated report generated from live data", subFont);
            sub.setSpacingAfter(16f);
            document.add(sub);

            if ("orders".equals(t)) {
                List<Order> orders = orderRepository.findAll();
                long total = orders.stream().filter(o -> o.getTotal() != null)
                        .map(o -> o.getTotal().longValue()).reduce(0L, Long::sum);
                document.add(headline("Orders Overview", headFont));
                document.add(numberLine("Total Orders", String.valueOf(orders.size())));
                document.add(numberLine("Total Order Value", "৳ " + String.format("%,d", total)));
                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(new float[]{2f, 3f, 2f, 2f});
                table.setWidthPercentage(100);
                addHeaderCells(table, "Order #", "Customer", "Status", "Total");
                int shown = 0;
                for (Order o : orders) {
                    if (shown++ >= 15) break;
                    table.addCell(cell(safe(o.getOrderNumber())));
                    table.addCell(cell(safe(o.getCustomerName())));
                    table.addCell(cell(o.getStatus() == null ? "" : o.getStatus().getLabel()));
                    table.addCell(cell("৳ " + (o.getTotal() == null ? "0" : String.format("%,d", o.getTotal().longValue()))));
                }
                document.add(table);
            } else {
                List<Invoice> invoices = invoiceRepository.findAll();
                long total = invoices.stream().filter(i -> i.getTotal() != null)
                        .map(i -> i.getTotal().longValue()).reduce(0L, Long::sum);
                long paid = invoices.stream().filter(i -> i.getStatus() == InvoiceStatus.PAID).count();
                document.add(headline("Invoices Overview", headFont));
                document.add(numberLine("Total Invoices", String.valueOf(invoices.size())));
                document.add(numberLine("Paid Invoices", String.valueOf(paid)));
                document.add(numberLine("Total Invoiced", "৳ " + String.format("%,d", total)));
                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(new float[]{2f, 3f, 2f, 2f});
                table.setWidthPercentage(100);
                addHeaderCells(table, "Invoice #", "Customer", "Status", "Total");
                int shown = 0;
                for (Invoice i : invoices) {
                    if (shown++ >= 15) break;
                    table.addCell(cell(safe(i.getInvoiceNumber())));
                    table.addCell(cell(safe(i.getCustomerName())));
                    table.addCell(cell(i.getStatus() == null ? "" : i.getStatus().getLabel()));
                    table.addCell(cell("৳ " + (i.getTotal() == null ? "0" : String.format("%,d", i.getTotal().longValue()))));
                }
                document.add(table);
            }

            document.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to build PDF report", ex);
        }
    }

    private Paragraph headline(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setSpacingAfter(8f);
        return p;
    }

    private Paragraph numberLine(String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(71, 85, 105));
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(15, 23, 42));
        Paragraph p = new Paragraph();
        p.add(new Phrase(label + ":  ", labelFont));
        p.add(new Phrase(value, valueFont));
        return p;
    }

    private void addHeaderCells(PdfPTable table, String... headers) {
        Font hf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        for (String h : headers) {
            PdfPCell c = new PdfPCell(new Phrase(h, hf));
            c.setBackgroundColor(new Color(99, 102, 241));
            c.setPadding(6f);
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c);
        }
    }

    private PdfPCell cell(String text) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(30, 41, 59));
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setPadding(5f);
        return c;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
