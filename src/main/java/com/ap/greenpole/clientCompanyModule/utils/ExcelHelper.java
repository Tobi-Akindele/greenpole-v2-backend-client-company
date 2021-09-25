package com.ap.greenpole.clientCompanyModule.utils;

import com.ap.greenpole.clientCompanyModule.entity.Shareholder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelHelper {

    static String[] HEADERS = {"FIRST NAME", "MIDDLE NAME", "LAST NAME","EMAIL", "ADDRESS", "HOLDING","BANK NAME"};
    static String SHEET = "Shareholders";

    public static ByteArrayInputStream shareholdersToExcel(List<Shareholder> shareholders) {
        try {

            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
            }

            int rowIdx = 1;
            for (Shareholder shareholder : shareholders) {
                Row row = sheet.createRow(rowIdx++);
                DataFormatter formatter = new DataFormatter();

                row.createCell(0).setCellValue(shareholder.getFirstName());
                row.createCell(1).setCellValue(shareholder.getMiddleName());
                row.createCell(2).setCellValue(shareholder.getLastName());
                row.createCell(3).setCellValue(shareholder.getEmail());
                row.createCell(4).setCellValue(shareholder.getAddress());
                row.createCell(5).setCellValue(String.valueOf(shareholder.getShareUnit()));
                row.createCell(6).setCellValue(shareholder.getBankName());


            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to import data to Excel file: " + ex.getMessage());
        }
    }
}
