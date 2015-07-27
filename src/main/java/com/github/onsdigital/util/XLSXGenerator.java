package com.github.onsdigital.util;

import com.github.onsdigital.content.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigital.content.partial.TimeseriesValue;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class XLSXGenerator {

    private List<TimeSeries> TimeSeries;
    public Map<String, TimeseriesValue[]> data;

    public XLSXGenerator(List<TimeSeries> TimeSeries, Map<String, TimeseriesValue[]> data) {
        this.TimeSeries = TimeSeries;
        this.data = data;
    }

    public void write(OutputStream output) throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("data");
        int startRow = generateHeaders(wb, sheet, TimeSeries);
        generateRows(sheet, TimeSeries, startRow);
        wb.write(output);
    }

    private void generateRows(Sheet sheet, List<TimeSeries> TimeSeriesList, int startRow) {
        int rownum = startRow + 1;

        for (Entry<String, TimeseriesValue[]> rowValues : data.entrySet()) {
            int i = 0;
            Row row = sheet.createRow(rownum++);
            row.createCell(i++).setCellValue(rowValues.getKey());
            for (TimeseriesValue TimeSeriesValue : rowValues.getValue()) {
                row.createCell(i++).setCellValue(TimeSeriesValue == null ? null : TimeSeriesValue.value);
            }
        }
    }

    private int generateHeaders(Workbook wb, Sheet sheet, List<TimeSeries> TimeSeriesList) {

        // Rows
        int row = 0;
        Row name = sheet.createRow(row++);
        Row cdid = sheet.createRow(row++);
        Row preUnit = sheet.createRow(row++);
        Row unit = sheet.createRow(row++);
        Row source = sheet.createRow(row++);
        Row keyNote = sheet.createRow(row++);
        Row additionalText = sheet.createRow(row++);
        Row note1 = sheet.createRow(row++);
        Row note2 = sheet.createRow(row++);

        // Labels
        int column = 0;
        name.createCell(column).setCellValue("Name");
        cdid.createCell(column).setCellValue("Series ID");
        preUnit.createCell(column).setCellValue("Pre unit");
        unit.createCell(column).setCellValue("Units");
        source.createCell(column).setCellValue("Source");
        keyNote.createCell(column).setCellValue("Note 1");
        additionalText.createCell(column).setCellValue("Note 2");
        note1.createCell(column).setCellValue("Note 3");
        note2.createCell(column).setCellValue("Note 4");
        column++;

        // Data
        for (TimeSeries timeseries : this.TimeSeries) {
            name.createCell(column).setCellValue(timeseries.getDescription().getTitle());
            cdid.createCell(column).setCellValue(timeseries.getCdid());
            preUnit.createCell(column).setCellValue(timeseries.getDescription().getPreUnit());
            unit.createCell(column).setCellValue(timeseries.getDescription().getUnit());
            source.createCell(column).setCellValue(timeseries.getDescription().getSource());
            keyNote.createCell(column).setCellValue(timeseries.getDescription().getKeyNote());
            additionalText.createCell(column).setCellValue(timeseries.getDescription().getAdditionalText());
            List<String> notes = timeseries.getNotes();
            if (notes != null) {
                //TODO: CSV. XLS generation should be pre-generated
                note1.createCell(column).setCellValue(notes.size() >= 1 ? timeseries.getNotes().get(0) : null);
                note2.createCell(column).setCellValue(notes.size() >=2 ?  timeseries.getNotes().get(1) : null);
            }


            column++;
            System.out.println("Geneararing XLSX for: " + timeseries.getDescription().getTitle() + " at: " + timeseries.getUri());
        }

        return row;
    }
}
