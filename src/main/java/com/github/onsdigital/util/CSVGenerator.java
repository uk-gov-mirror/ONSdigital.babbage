package com.github.onsdigital.util;

import au.com.bytecode.opencsv.CSVWriter;
import com.github.onsdigital.content.page.statistics.data.TimeSeries;
import com.github.onsdigital.content.partial.TimeseriesValue;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 
 * Transforms TimeSeries data into CSV format
 * 
 * @author Brn
 *
 */
public class CSVGenerator {

	private List<TimeSeries> TimeSeries;
	public Map<String, TimeseriesValue[]> data;

	public CSVGenerator(List<TimeSeries> TimeSeries, Map<String, TimeseriesValue[]> data) {
		this.TimeSeries = TimeSeries;
		this.data = data;
	}

	public void write(OutputStream output) throws IOException {
		try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(output, Charset.forName("UTF8")), ',')) {
			generateCsvHeaders(writer);
			generateCsvRows(writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateCsvRows(CSVWriter writer) {
		String[] row;

		// Spacer row
		writer.writeNext(new String[2]);

		for (Entry<String, TimeseriesValue[]> rowData : data.entrySet()) {
			int size = rowData.getValue().length + 1;
			int i = 0;
			row = new String[size];
			row[i++] = rowData.getKey();
			for (TimeseriesValue TimeSeriesValue : rowData.getValue()) {
				row[i++] = (TimeSeriesValue ==null ? null : TimeSeriesValue.value);
			}
			writer.writeNext(row);
		}
	}

	private int generateCsvHeaders(CSVWriter writer) {

		int size = TimeSeries.size() + 1;

		// Rows
		int row = 9;
		String[] name = new String[size];
		String[] cdid = new String[size];
		String[] preUnit = new String[size];
		String[] unit = new String[size];
		String[] source = new String[size];
		String[] keyNote = new String[size];
		String[] additionalText = new String[size];
		String[] note1 = new String[size];
		String[] note2 = new String[size];

		// Labels
		int column = 0;
		name[column] = "Name";
		cdid[column] = "Series ID";
		preUnit[column] = "Pre unit";
		unit[column] = "Units";
		source[column] = "Source";
		keyNote[column] = "Note 1";
		additionalText[column] = "Note 2";
		note1[column] = "Note 3";
		note2[column] = "Note 4";
		column++;

		// Data
		for (TimeSeries TimeSeries : this.TimeSeries) {
			name[column] = TimeSeries.title;
			cdid[column] = TimeSeries.cdid;
			preUnit[column] = TimeSeries.preUnit;
			unit[column] = TimeSeries.unit;
			source[column] = TimeSeries.source;
			keyNote[column] = TimeSeries.keyNote;
			additionalText[column] = TimeSeries.additionalText;
			note1[column] = TimeSeries.notes.get(0);
			note2[column] = TimeSeries.notes.get(1);
			column++;
			System.out.println("Geneararing CSV for: " + TimeSeries.title + " at: " + TimeSeries.uri);
		}
		writer.writeNext(name);
		writer.writeNext(cdid);
		writer.writeNext(preUnit);
		writer.writeNext(unit);
		writer.writeNext(source);
		writer.writeNext(keyNote);
		writer.writeNext(additionalText);
		writer.writeNext(note1);
		writer.writeNext(note2);

		return row;
	}
}
