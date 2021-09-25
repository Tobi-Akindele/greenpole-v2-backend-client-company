package com.ap.greenpole.clientCompanyModule.utils;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Lewis.Aguh on 28/08/2020.
 */

public class PdfFileGenerator<T> implements IFileGenerator<T> {

	private static Logger log = LoggerFactory.getLogger(PdfFileGenerator.class);
	private String filename;
	private PdfWriter writer;
    private PdfDocument pdf;
    private Document document;
    private Table activeTable = null;
	private T target;
	
	public PdfFileGenerator(String filename, T target) {
		this.filename = filename;
		this.target = target;
	}

	@Override
	public void close() throws Exception {}

	@Override
	public void open() {
		try {
			this.writer = new PdfWriter(this.filename);
			this.pdf = new PdfDocument(this.writer);
			this.document = new Document(this.pdf);
			this.pdf.setDefaultPageSize(PageSize.A4.rotate());
		} catch (FileNotFoundException ex) {
			log.error("Error occurred {}", ex);
		}
	}
	
	private void createHeaders() {
		try {
			String[] headers = extractHeaders();
			this.activeTable = new Table(new float[headers.length]).useAllAvailableWidth();
			PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
			
			for(String header: headers) {
				this.activeTable.addHeaderCell(new Cell().add(header)
	        			   .setFont(font).setFontSize(7)).setTextAlignment(TextAlignment.CENTER);
			}
			
		} catch (IOException ex) {
			log.error("Error occurred {}", ex);
		}
	}
	
	private void createActiveRows(List<T> dataList) {
		for (T obj : dataList) {
			final BeanWrapper src = new BeanWrapperImpl(obj);
			List<Field> fieldsList = Arrays.asList(obj.getClass().getDeclaredFields());

			for (final Field property : fieldsList) {
				if (!(Collection.class.isAssignableFrom(property.getType()))) {
					Object providedObject = src.getPropertyValue(property.getName());
					Object value = providedObject != null ? providedObject : "N/A";
					this.activeTable.addCell(new Cell().setPadding(0).setTextAlignment(TextAlignment.CENTER)
							.add(String.valueOf(value)).setFontSize(6));
				}
			}
		}
	}
	
	private void closeDocument() {
		try {
			if (null != this.activeTable) {
				this.document.add(this.activeTable);
			}
			this.document.close();
			if(this.writer != null)
				writer.flush();
		} catch (IOException e) {
			log.error("Error occurred {}", e);
		}
	}

	@Override
	public void write(List<T> dataList) {
		createHeaders();
		createActiveRows(dataList);
		closeDocument();
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
