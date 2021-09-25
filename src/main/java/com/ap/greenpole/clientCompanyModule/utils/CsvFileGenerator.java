package com.ap.greenpole.clientCompanyModule.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Lewis.Aguh on 28/08/2020.
 */

public class CsvFileGenerator<T> implements IFileGenerator<T>{
	
	private static Logger log = LoggerFactory.getLogger(CsvFileGenerator.class);
	private String filename;
	private Writer writer;
	private CSVPrinter csvPrinter;
	private T target;
	
	public CsvFileGenerator(String filename, T target) {
		this.filename = filename;
		this.target = target;
	}

	@Override
	public void close() throws Exception {}

	@Override
	public void open() {
		try {
			this.writer = Files.newBufferedWriter(new File(this.filename).toPath());
		} catch (IOException ex) {
			log.error("Error occurred {}", ex);
		}
	}
	
	private void createHeaders() {
		try {

			this.csvPrinter = new CSVPrinter(this.writer, CSVFormat.DEFAULT.withHeader(extractHeaders()));
		
		} catch (IOException ex) {
			log.error("Error occurred {}", ex);
		}
	}

	private void createActiveRows(List<T> dataList) {
		try {
			List<Object[]> arrayList = new ArrayList<>(); 
			int headerLength = extractHeaders().length;
			for(T obj: dataList) {
				Object[] rows = new Object[headerLength];
				int counter = 0;
				final BeanWrapper src = new BeanWrapperImpl(obj);
				for (final Field property : obj.getClass().getDeclaredFields()) {
					if (!(Collection.class.isAssignableFrom(property.getType()))) {
						Object providedObject = src.getPropertyValue(property.getName());
						rows[counter] = providedObject != null ? providedObject : "N/A";
						counter++;
					}
				}
				arrayList.add(rows);
			}
			this.csvPrinter.printRecords(arrayList);

		} catch (IOException ex) {
			log.error("Error occurred {}", ex);
		} finally {
			try {
				if (csvPrinter != null)
					csvPrinter.flush();
				if (writer != null)
					writer.flush();
			} catch (IOException e) {
				log.error("Error occurred {}", e);
			}
		}
	}

	@Override
	public void write(List<T> list) {
		createHeaders();
		createActiveRows(list);
	}

	@Override
	public void createDirectory(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			boolean flag = file.mkdirs();
			if (flag) {
				log.debug("Directory is created!");
			} else {
				log.error("Directory creation failed");
			}
		} else {
			log.debug("Directory already exists.");
		}
	}
	
	private String[] extractHeaders() {
		List<Field> fieldsList = Arrays.asList(this.target.getClass().getDeclaredFields());
		List<String> fieldNames = new ArrayList<>();
		for(final Field property: fieldsList) {
			if(!(Collection.class.isAssignableFrom(property.getType()))) {
				fieldNames.add(Utils.splitCamelCase(property.getName()).toUpperCase());
			}
		}
		String[] resultingArray = fieldNames.toArray(new String[fieldNames.size()]);
		return resultingArray;
	}
}
