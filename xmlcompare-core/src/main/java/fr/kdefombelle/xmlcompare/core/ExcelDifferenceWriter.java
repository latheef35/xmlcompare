/**
 *  This software program has been written by Karim de Fombelle.
 */
package fr.kdefombelle.xmlcompare.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.custommonkey.xmlunit.Difference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExcelDifferenceWriter {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    final Logger logger = LoggerFactory.getLogger(ExcelDifferenceWriter.class);

    private String fileName;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    public ExcelDifferenceWriter(String fileName) {
        this.fileName = fileName;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public void write(File xmlControl, File xmlTest, List<Difference> differences) {
        Workbook wb = new SXSSFWorkbook();

        try(FileOutputStream fileOut = new FileOutputStream(fileName)) {
            Sheet sheet1 = wb.createSheet("Xmlcompare");
            int rowIndex = -1;

            CellStyle titleStyle = getTitleCellStyle(wb);

            Row row0 = sheet1.createRow(++rowIndex);
            row0.createCell(0).setCellValue(xmlControl.getName());

            row0.createCell(0).setCellValue(xmlControl.getAbsolutePath());
            createStyledCell(row0, 1, titleStyle).setCellValue("control");
            createStyledCell(row0, 2, titleStyle).setCellValue("test");
            row0.createCell(3).setCellValue(xmlTest.getAbsolutePath());

            Row row1 = sheet1.createRow(++rowIndex);

            Row row2 = sheet1.createRow(++rowIndex);
            if (differences.isEmpty()) {
                row2.createCell(0).setCellValue("No difference found");
            } else {
                createStyledCell(row2, 0, titleStyle).setCellValue("xpath");
                createStyledCell(row2, 1, titleStyle).setCellValue("value");
                createStyledCell(row2, 2, titleStyle).setCellValue("value");
                createStyledCell(row2, 3, titleStyle).setCellValue("xpath");

                for (Difference difference : differences) {
                    Row row3 = sheet1.createRow(++rowIndex);
                    row3.createCell(0).setCellValue(difference.getControlNodeDetail().getXpathLocation());
                    row3.createCell(1).setCellValue(difference.getControlNodeDetail().getValue());
                    row3.createCell(2).setCellValue(difference.getTestNodeDetail().getValue());
                    row3.createCell(3).setCellValue(difference.getTestNodeDetail().getXpathLocation());
                }
            }
            logger.info("Writing report [" + fileName + "]");
            wb.write(fileOut);
        } catch (IOException e) {
            throw new XmlCompareException("Exception while writing report", e);
        }

    }

    private CellStyle getTitleCellStyle(Workbook wb) {
        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
        titleStyle.setFillPattern(CellStyle.FINE_DOTS);
        titleStyle.setBorderBottom(CellStyle.BORDER_THIN);
        titleStyle.setBorderTop(CellStyle.BORDER_THIN);
        titleStyle.setBorderLeft(CellStyle.BORDER_THIN);
        titleStyle.setBorderRight(CellStyle.BORDER_THIN);
        return titleStyle;
    }

    private Cell createStyledCell(Row row, int index, CellStyle cellStyle) {
        Cell cell = row.createCell(index);
        cell.setCellStyle(cellStyle);
        return cell;
    }
}
